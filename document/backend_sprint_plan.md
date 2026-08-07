# 📋 KẾ HOẠCH SPRINT BACKEND — DỰ ÁN GAS PRO

> **Mentor:** Senior Backend Developer  
> **Mentee:** Em (Sinh viên năm 4)  
> **Stack:** Java 17 + Spring Boot 3.x + Maven + PostgreSQL  
> **Quy tắc:** Chậm mà chắc — Hiểu từng dòng code, từng luồng flow  

---

## 🗓️ TỔNG QUAN CÁC PHASE (OVERVIEW)

| Phase | Tên Phase | Số ngày (ước lượng) | Trạng thái |
|:---:|:---|:---:|:---:|
| 0 | Khởi tạo Dự án & Nền tảng (Foundation) | 2 ngày | `[ ]` |
| 1 | Module Auth — Đăng ký, Đăng nhập, JWT & RBAC | 4 ngày | `[ ]` |
| 2 | Module Product — Danh mục Sản phẩm & Quản lý Giá | 3 ngày | `[ ]` |
| 3 | Module Supplier — Nhà sản xuất & Nhập kho Multi-item | 3 ngày | `[ ]` |
| 4 | Module Customer & Cart — Khách hàng, Giỏ hàng & Công nợ | 3 ngày | `[ ]` |
| 5 | Module Order & Delivery — Đơn hàng, Chợ đơn & Giao hàng | 4 ngày | `[ ]` |
| 6 | Module Driver & Shift — Tài xế, Sự cố & Quyết toán ca | 3 ngày | `[ ]` |
| 7 | Module Warranty — Bảo hành, Linh kiện & Bếp mượn | 3 ngày | `[ ]` |
| 8 | Module System Settings, Dashboard & Media Upload | 2 ngày | `[ ]` |
| 9 | Kiểm thử Tích hợp & Hoàn thiện (Testing & Polish) | 3 ngày | `[ ]` |
| | **TỔNG CỘNG (ƯỚC LƯỢNG)** | **~30 ngày** | |

> **Lưu ý:** Số ngày chỉ là ước lượng. Em code đến đâu chắc đến đó, không chạy deadline.

---

## 📌 CHI TIẾT TỪNG NGÀY (DAILY TASK BREAKDOWN)

---

### PHASE 0: KHỞI TẠO DỰ ÁN & NỀN TẢNG (2 NGÀY)

#### 🔖 DAY 1 — Cài đặt Môi trường & Khởi tạo Spring Boot Project
- `[ ]` Cài đặt PostgreSQL 16 + tạo Database `gas_management_db`
- `[ ]` Khởi tạo Spring Boot Project (Maven, Java 17, dependencies)
- `[ ]` Tạo đầy đủ cấu trúc 14 packages theo `project_structure.md`
- `[ ]` Cấu hình `application.yml` (Datasource, JPA, Server port)
- `[ ]` Chạy thử App lần đầu — Kiểm tra kết nối DB thành công
- `[ ]` Git init + Commit đầu tiên: `"feat: init spring boot project structure"`

#### 🔖 DAY 2 — Xây dựng Lớp Nền tảng Dùng Chung (Base Infrastructure)
- `[ ]` Tạo `ApiResponse<T>` — Wrapper Response chuẩn `[code, message, data, paging]`
- `[ ]` Tạo `PagingResponse` — Object chứa `page, limit, total_items, total_pages`
- `[ ]` Tạo `ErrorCode` Enum — Toàn bộ mã lỗi hệ thống (theo `00_error_codes.md`)
- `[ ]` Tạo `AppException` — Custom RuntimeException nhận `ErrorCode`
- `[ ]` Tạo `GlobalExceptionHandler` — `@RestControllerAdvice` bắt lỗi tập trung
- `[ ]` Tạo 1 endpoint test `GET /api/v1/health-check` để kiểm tra Response format
- `[ ]` Test bằng Postman: Gọi thử health-check và throw AppException xem JSON trả về
- `[ ]` Git commit: `"feat: add base response, error codes & global exception handler"`

---

### PHASE 1: MODULE AUTH — ĐĂNG KÝ, ĐĂNG NHẬP, JWT & RBAC (4 NGÀY)

#### 🔖 DAY 3 — Entity User, Role, Permission & Database Migration
- `[ ]` Tạo Entity: `Role`, `Permission`, `RolePermission`, `User`
- `[ ]` Tạo Repository: `RoleRepository`, `PermissionRepository`, `UserRepository`
- `[ ]` Tạo file migration `V1__init_auth_tables.sql` (Flyway) hoặc để JPA auto-ddl
- `[ ]` Tạo file seed data `V2__seed_roles_permissions.sql` (4 Roles + Permissions cơ bản)
- `[ ]` Chạy App — Kiểm tra các bảng đã tạo đúng trong PostgreSQL
- `[ ]` Git commit: `"feat: add auth entities (User, Role, Permission) + seed data"`

#### 🔖 DAY 4 — Spring Security + JWT Token Provider + Filter
- `[ ]` Tạo `JwtTokenProvider` — Sinh Access Token & Refresh Token (HS256)
- `[ ]` Tạo `JwtAuthenticationFilter` — OncePerRequestFilter kiểm tra Bearer Token
- `[ ]` Tạo `CustomUserDetailsService` — Load User từ DB
- `[ ]` Tạo `SecurityConfig` — Cấu hình URL public/protected, CORS, CSRF
- `[ ]` Test: Gọi API protected mà không có Token → Trả 401 Unauthorized
- `[ ]` Git commit: `"feat: add JWT security (token provider, filter, security config)"`

#### 🔖 DAY 5 — Auth Controller: Đăng ký & Đăng nhập
- `[ ]` Tạo `RegisterRequest` DTO + Validation (`@NotBlank`, `@Size`)
- `[ ]` Tạo `LoginRequest` DTO
- `[ ]` Tạo `AuthResponse` DTO (user_id, phone, role_code, access_token, refresh_token)
- `[ ]` Tạo `AuthService` interface + `AuthServiceImpl`
- `[ ]` Tạo `AuthController`: `POST /api/v1/auth/register` + `POST /api/v1/auth/login`
- `[ ]` Test Postman: Đăng ký → Đăng nhập → Nhận JWT Token
- `[ ]` Git commit: `"feat: add register & login APIs with JWT response"`

#### 🔖 DAY 6 — Refresh Token & Get Profile /me
- `[ ]` Tạo `POST /api/v1/auth/refresh-token` — Đổi refresh lấy access mới
- `[ ]` Tạo `GET /api/v1/auth/me` — Trả thông tin User + Role + Permissions
- `[ ]` Tạo `UserMapper` (MapStruct) — Convert Entity → Response DTO
- `[ ]` Test toàn bộ luồng Auth end-to-end bằng Postman
- `[ ]` Git commit: `"feat: add refresh-token & get-profile /me API"`

---

### PHASE 2: MODULE PRODUCT — DANH MỤC SẢN PHẨM & QUẢN LÝ GIÁ (3 NGÀY)

#### 🔖 DAY 7 — Entity Product, Category, Brand & Repository
- `[ ]` Tạo Entity: `Category`, `Brand`, `Product`, `ProductPriceHistory`
- `[ ]` Tạo Repository với custom query: tìm kiếm theo tên, lọc theo category/brand
- `[ ]` Seed data: 3 categories, 4 brands, 8 products mẫu
- `[ ]` Git commit: `"feat: add product catalog entities + seed data"`

#### 🔖 DAY 8 — Product CRUD APIs (Admin + Public)
- `[ ]` Tạo DTOs: `ProductRequest`, `ProductResponse`, `ProductMapper`
- `[ ]` Tạo `ProductService` + `ProductServiceImpl`
- `[ ]` Tạo `ProductController`:
  - `GET /api/v1/products` (Public, Search + Filter + Paging)
  - `GET /api/v1/products/{id}` (Public)
  - `POST /api/v1/admin/products` (Admin only)
  - `PUT /api/v1/admin/products/{id}` (Admin only)
  - `DELETE /api/v1/admin/products/{id}` (Admin - Soft delete)
- `[ ]` Test Postman với JWT Token Admin
- `[ ]` Git commit: `"feat: add product CRUD APIs with search, filter & paging"`

#### 🔖 DAY 9 — Điều Chỉnh Giá & Lịch Sử Biến Động Giá (CO-004)
- `[ ]` Tạo `PATCH /api/v1/admin/products/{id}/price` — Đổi giá + ghi log history
- `[ ]` Tạo `GET /api/v1/admin/products/{id}/price-histories` — Lịch sử giá + paging
- `[ ]` Business Logic: Đơn cũ giữ nguyên `unit_price` snapshot, giá mới chỉ áp dụng đơn mới
- `[ ]` Git commit: `"feat: add price change API with history tracking (CO-004)"`

---

### PHASE 3: MODULE SUPPLIER — NHÀ SẢN XUẤT & NHẬP KHO (3 NGÀY)

#### 🔖 DAY 10 — Entity Supplier, ImportReceipt & Repository
#### 🔖 DAY 11 — Import Receipt Multi-item API & Thanh toán nợ NSX
#### 🔖 DAY 12 — Đối lưu vỏ rỗng lấy bình đầy NSX (CI-002)

---

### PHASE 4: MODULE CUSTOMER & CART (3 NGÀY)

#### 🔖 DAY 13 — Entity Customer, CustomerVatInfo, CartItem
#### 🔖 DAY 14 — Customer Profile /me, VAT CRUD, Credit Check
#### 🔖 DAY 15 — Cart APIs (POST / PUT / DELETE cart items)

---

### PHASE 5: MODULE ORDER & DELIVERY (4 NGÀY)

#### 🔖 DAY 16 — Entity Order, OrderItem & Repository
#### 🔖 DAY 17 — Create Order API (CO-003) + Khóa giá snapshot (CO-004)
#### 🔖 DAY 18 — Driver Claim Order (SD-001/002) + Race Condition Prevention
#### 🔖 DAY 19 — Cancel Order + 50% Compensation + Pending Payment Proof

---

### PHASE 6: MODULE DRIVER & SHIFT (3 NGÀY)

#### 🔖 DAY 20 — Entity DriverProfile, DriverShift, WalletTransaction
#### 🔖 DAY 21 — Toggle Online/Offline + Transit Incident + Wallet Ledger
#### 🔖 DAY 22 — Reconcile COD + Reconcile Shells + Inspect Seal

---

### PHASE 7: MODULE WARRANTY (3 NGÀY)

#### 🔖 DAY 23 — Entity WarrantyTicket, PartsProposal, TemporaryStove
#### 🔖 DAY 24 — Warranty Ticket API + Propose Spare Parts + 3-Party Approval
#### 🔖 DAY 25 — Temporary Stove Loan & Reclaim APIs

---

### PHASE 8: SYSTEM SETTINGS, DASHBOARD & MEDIA (2 NGÀY)

#### 🔖 DAY 26 — System Settings CRUD + Media Upload API (S3/Local)
#### 🔖 DAY 27 — Dashboard Overview + Cashflow Chart + Top Products + Export Excel

---

### PHASE 9: KIỂM THỬ & HOÀN THIỆN (3 NGÀY)

#### 🔖 DAY 28 — Unit Test cho Auth & Order Service (JUnit 5 + Mockito)
#### 🔖 DAY 29 — Integration Test + Swagger UI Documentation
#### 🔖 DAY 30 — Code Review tổng thể + Refactor + Final Polish

---

## 📊 TIẾN ĐỘ TRACKING

| Ngày | Task | Trạng thái | Ghi chú Review |
|:---:|:---|:---:|:---|
| Day 1 | Cài đặt & Khởi tạo Project | `[ ]` | |
| Day 2 | Base Infrastructure | `[ ]` | |
| Day 3 | Auth Entities | `[ ]` | |
| Day 4 | JWT Security | `[ ]` | |
| Day 5 | Register & Login | `[ ]` | |
| Day 6 | Refresh Token & /me | `[ ]` | |
| Day 7 | Product Entities | `[ ]` | |
| Day 8 | Product CRUD | `[ ]` | |
| Day 9 | Price Change History | `[ ]` | |
| Day 10-30 | ... | `[ ]` | |
