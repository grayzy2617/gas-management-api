---

### 🟢 GIAI ĐOẠN 1: KHI ỨNG DỤNG SPRING BOOT KHỞI ĐỘNG (STARTUP PHASE)

Khi em bấm **Run** `GasManagementApplication.java`, Spring Boot sẽ nạp và khởi tạo các file theo thứ tự sau:

1. **`application.yml`**: 
   - Spring Boot đọc file này đầu tiên để lấy cấu hình Database PostgreSQL (URL, username, password), Server Port (8080) và các thông số JWT (`signer-key`, `valid-duration`).
2. **`SecurityConfig.java`** (`@Configuration`, `@EnableWebSecurity`): 
   - Khởi tạo Bean `passwordEncoder()` (`BCryptPasswordEncoder`) dùng cho việc băm mật khẩu.
   - Khởi tạo Bean `AuthenticationManager` dùng cho quá trình đăng nhập.
   - Khởi tạo Bean `SecurityFilterChain`: 
     - Tắt CSRF (vì dùng REST Stateless JWT).
     - Đặt Session Management thành `STATELESS`.
     - Khai báo danh sách đường dẫn Public (`/api/v1/auth/**`, `/api/v1/health-check/**`) và các đường dẫn yêu cầu Role cụ thể (`/api/v1/admin/**` ➔ `ROLE_ADMIN`).
     - Gắn `jwtAuthenticationFilter` vào trước `UsernamePasswordAuthenticationFilter`.
3. **`JwtTokenProvider.java`** (`@Component`): 
   - Đọc các giá trị `@Value("${spring.jwt.signer-key}")` từ `application.yml` và chuẩn bị sẵn `SecretKey` (HMAC-SHA256).
4. **`JwtAuthenticationFilter.java`** (`@Component`): 
   - Đã được tiêm `JwtTokenProvider` và `CustomUserDetailsService`, sẵn sàng đứng canh ở cửa để soi từng HTTP Request gửi lên.
5. **`CustomUserDetailsService.java`** (`@Service`): 
   - Đã được tiêm `UserRepository`, sẵn sàng truy vấn thông tin User từ DB.
6. **`DataSeeder.java`** (`CommandLineRunner`): 
   - Chạy cuối cùng sau khi Spring Context khởi tạo hoàn tất ➔ Kiểm tra bảng `roles` trong PostgreSQL, nếu rỗng thì tự động chèn 4 Roles (`ADMIN`, `OPERATOR`, `DRIVER`, `CUSTOMER`).

---

### 🔵 GIAI ĐOẠN 2: KHI CÓ HTTP REQUEST GỬI ĐẾN (PER-REQUEST EXECUTION FLOW)

Giả sử Khách hàng/Admin gửi một Request:

`GET /api/v1/admin/dashboard` kèm Header `Authorization: Bearer <access_token>`

Luồng xử lý đi qua các file như sau:

1. **`CorsConfig.java`** **/ Spring Security CORS**: 
   - Chạy đầu tiên để kiểm tra Origin/Header của ứng dụng client xem có bị chặn CORS không.
2. **`JwtAuthenticationFilter.java`** (Phương thức `doFilterInternal`): 
   - **Bước 2.1:** Gọi `getJwtFromRequest(request)` để bóc tách lấy chuỗi Token đứng sau chữ `"Bearer "`.
   - **Bước 2.2:** Gọi `jwtTokenProvider.validateToken(token)` ➔ `JwtTokenProvider` dùng `SecretKey` kiểm tra chữ ký và Hạn sử dụng của Token.
   - **Bước 2.3:** Nếu Token chuẩn, gọi `jwtTokenProvider.extractPhone(token)` để lấy ra Số điện thoại (Subject).
   - **Bước 2.4:** Gọi `userDetailsService.loadUserByUsername(phone)`: 
     - `CustomUserDetailsService` truy vấn `userRepository.findByPhone(phone)`.
     - Trả về đối tượng `CustomUserDetails` bọc lấy `User` Entity, tự động convert `Role` thành `SimpleGrantedAuthority("ROLE_ADMIN")`.
   - **Bước 2.5:** Tạo đối tượng `UsernamePasswordAuthenticationToken` chứa thông tin User & Quyền hạn, rồi nạp vào **`SecurityContextHolder.getContext().setAuthentication(...)`**.
   - **Bước 2.6:** Gọi `filterChain.doFilter(request, response)` để cho Request đi tiếp.
3. **`SecurityConfig.java`** (Đoạn `SecurityFilterChain` kiểm tra Authorization): 
   - Spring Security soi đường dẫn `/api/v1/admin/dashboard` với luật đã cấu hình: `.requestMatchers("/api/v1/admin/**").hasRole("ADMIN")`.
   - Kiểm tra `SecurityContextHolder`: Thấy User hiện tại có quyền `ROLE_ADMIN` ➔ **Cho phép trôi tiếp vào** **`DashboardController.java`**.
   - *(Nếu không có Token hoặc Token của tài khoản Khách* *`ROLE_CUSTOMER`* *➔ Ngắt luồng ngay tại đây và trả về HTTP 401 Unauthorized hoặc 403 Forbidden)*.
4. **`GlobalExceptionHandler.java`** (`@RestControllerAdvice`): 
   - Nếu trong quá trình Controller hoặc Service xử lý bị ném ra `AppException` (ví dụ `RESOURCE_NOT_FOUND`), `GlobalExceptionHandler` sẽ lập tức bắt lấy và đóng gói thành JSON `ApiResponse.error(code, message)` trả về cho Client.