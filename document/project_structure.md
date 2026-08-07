# TÀI LIỆU CẤU TRÚC DỰ ÁN & KIẾN TRÚC MÃ NGUỒN BACKEND
## DỰ ÁN HỆ THỐNG QUẢN LÝ GAS PRO (SPRING BOOT 3.x / JAVA 17+)

> **Người thực hiện:** Senior IT Mentor & Backend Architect  
> **Package Root:** `vn.gaspro.api`  
> **Phiên bản:** v1.0 Production Architecture  
> **Đường dẫn tệp:** `document/project_structure.md`

---

## 📐 1. CÂY CẤU TRÚC THƯ MỤC MÃ NGUỒN (PROJECT DIRECTORY TREE)

```text
gas_management/
├── document/                       # Tài liệu thiết kế hệ thống (BRD, DB, API Spec, Architecture)
│   ├── brd/                        # Requirement documents (BRD v8)
│   ├── user_story/                 # User stories (v5)
│   ├── user_flow/                  # User flow diagrams
│   ├── db_design/                  # Database specs (v5.0 - 31 tables)
│   ├── api_design/                 # API documentation (11 modules + 00_error_codes.md)
│   └── project_structure.md        # [FILE NÀY] Cấu trúc dự án Backend
│
├── UI_Prototype/                   # Giao diện HTML/CSS/JS mô phỏng 4 portal
│
└── src/
    ├── main/
    │   ├── java/
    │   │   └── vn/
    │   │       └── gaspro/
    │   │           └── api/
    │   │               ├── GasManagementApplication.java   # Spring Boot Main Class
    │   │               │
    │   │               ├── client/                         # Tích hợp dịch vụ bên thứ 3 (Third-party Clients)
    │   │               │   ├── S3StorageClient.java        # Upload tệp Ảnh/Video minh chứng (Module 09)
    │   │               │   ├── VietQRClient.java           # Tạo mã VietQR động cho đơn hàng
    │   │               │   ├── FCMNotificationClient.java  # Bắn Push Notification thời gian thực tới App Tài xế
    │   │               │   └── ZaloZnsClient.java          # Gửi tin nhắn ZNS/SMS Nhắc nợ khách hàng (CD-004)
    │   │               │
    │   │               ├── config/                         # Các lớp Cấu hình Hệ thống (Spring Configuration)
    │   │               │   ├── SecurityConfig.java         # Cấu hình Spring Security & OAuth2/JWT
    │   │               │   ├── CorsConfig.java             # Cấu hình CORS cho phép FE truy cập
    │   │               │   ├── SwaggerConfig.java          # Cấu hình OpenAPI / Swagger UI 3.0
    │   │               │   ├── RedisConfig.java            # Cấu hình Redis Cache & Distributed Lock
    │   │               │   └── AsyncConfig.java            # Cấu hình Thread Pool xử lý bất đồng bộ
    │   │               │
    │   │               ├── constant/                       # Hằng số Hệ thống (System Constants)
    │   │               │   └── AppConstants.java           # Định nghĩa hằng số phân trang, regex, header
    │   │               │
    │   │               ├── controller/                     # REST API Controllers (Tiếp nhận HTTP Request)
    │   │               │   ├── AuthController.java         # API Đăng ký, Đăng nhập, Refresh Token
    │   │               │   ├── ProductController.java      # API Danh mục SP & Giá bán niêm yết
    │   │               │   ├── SupplierController.java     # API NSX, Nhập kho Multi-item & Đối lưu vỏ
    │   │               │   ├── CustomerController.java     # API Khách hàng, VAT B2B & Giỏ hàng
    │   │               │   ├── OrderController.java        # API Đơn hàng, Chợ đơn, Giật đơn, Hủy đền 50%
    │   │               │   ├── DriverShiftController.java  # API Trạng thái Online, Báo hỏng xe & Đối soát ca
    │   │               │   ├── WarrantyController.java     # API Bảo hành media, Duyệt 3 bên & Bếp mượn
    │   │               │   ├── SystemSettingController.java# API Cấu hình hằng số D_RATE, T_LOCK_OUT
    │   │               │   ├── MediaController.java        # API Upload Ảnh/Video chuyên dụng
    │   │               │   └── DashboardController.java    # API Dashboard BI, Cảnh báo vỏ & Xuất Excel
    │   │               │
    │   │               ├── dto/                            # Request & Response Data Transfer Objects
    │   │               │   ├── request/                    # Payload nhận từ Frontend
    │   │               │   │   ├── LoginRequest.java
    │   │               │   │   ├── RegisterRequest.java
    │   │               │   │   ├── CreateOrderRequest.java
    │   │               │   │   ├── ImportReceiptRequest.java
    │   │               │   │   └── WarrantyTicketRequest.java
    │   │               │   │
    │   │               │   └── response/                   # Payload trả về cho Frontend
    │   │               │       ├── ApiResponse.java        # Structure [code, message, data, paging]
    │   │               │       ├── AuthResponse.java
    │   │               │       ├── OrderDetailResponse.java
    │   │               │       └── DashboardOverviewResponse.java
    │   │               │
    │   │               ├── entity/                         # JPA Entities (Ánh xạ 31 Bảng CSDL v5.0)
    │   │               │   ├── User.java
    │   │               │   ├── Role.java
    │   │               │   ├── Product.java
    │   │               │   ├── Customer.java
    │   │               │   ├── Order.java
    │   │               │   ├── OrderItem.java
    │   │               │   ├── DriverProfile.java
    │   │               │   ├── DriverShift.java
    │   │               │   ├── ShiftShellReconciliation.java
    │   │               │   ├── WarrantyTicket.java
    │   │               │   └── ... (31 Entities)
    │   │               │
    │   │               ├── enums/                          # Định nghĩa Các Tập Hằng Số (Enumerations)
    │   │               │   ├── ErrorCode.java              # Bảng Enum Mã lỗi hệ thống chuẩn (00_error_codes.md)
    │   │               │   ├── RoleCode.java               # ADMIN, OPERATOR, DRIVER, CUSTOMER
    │   │               │   ├── OrderStatus.java            # PENDING, ASSIGNED, ACCEPTED, DELIVERING, COMPLETED, CANCELLED
    │   │               │   ├── DebtStatus.java             # ELIGIBLE, INELIGIBLE, EXCEEDED_LIMIT, OVERDUE_LOCKED
    │   │               │   └── PaymentMethod.java          # COD, VIETQR, CREDIT_DEBT
    │   │               │
    │   │               ├── event/                          # Sự kiện Bất đồng bộ (Spring Application Events)
    │   │               │   ├── OrderCreatedEvent.java      # Event tạo đơn hàng mới
    │   │               │   └── listener/
    │   │               │       └── NotificationEventListener.java # Listener bắn FCM Push khi có đơn PENDING
    │   │               │
    │   │               ├── exception/                      # Custom Exceptions & Xử lý Lỗi Toàn cục
    │   │               │   ├── AppException.java           # Custom Runtime Exception truyền ErrorCode
    │   │               │   └── GlobalExceptionHandler.java # RestControllerAdvice xử lý lỗi tập trung
    │   │               │
    │   │               ├── mapper/                         # MapStruct Mappers (Convert Entity <-> DTO)
    │   │               │   ├── UserMapper.java
    │   │               │   ├── ProductMapper.java
    │   │               │   └── OrderMapper.java
    │   │               │
    │   │               ├── repository/                     # Spring Data JPA Repositories (Data Access)
    │   │               │   ├── UserRepository.java
    │   │               │   ├── ProductRepository.java
    │   │               │   ├── OrderRepository.java
    │   │               │   ├── DriverShiftRepository.java
    │   │               │   └── ... (31 Repositories)
    │   │               │
    │   │               ├── security/                       # Thành phần An ninh & Xác thực JWT
    │   │               │   ├── JwtTokenProvider.java       # Sinh và Verify JWT Access/Refresh Token
    │   │               │   ├── JwtAuthenticationFilter.java# Interceptor kiểm tra Token trên từng Request
    │   │               │   └── CustomUserDetailsService.java# Load user từ DB cho Spring Security
    │   │               │
    │   │               ├── service/                        # Business Logic Interfaces & Implementations
    │   │               │   ├── AuthService.java
    │   │               │   ├── ProductService.java
    │   │               │   ├── OrderService.java
    │   │               │   ├── DriverShiftService.java
    │   │               │   ├── WarrantyService.java
    │   │               │   └── impl/                       # Lớp hiện thực hóa nghiệp vụ
    │   │               │       ├── AuthServiceImpl.java
    │   │               │       ├── OrderServiceImpl.java
    │   │               │       └── DriverShiftServiceImpl.java
    │   │               │
    │   │               ├── util/                           # Các Hàm Tiện ích Chức năng (Utilities)
    │   │               │   ├── DateUtil.java               # Xử lý format ngày tháng
    │   │               │   ├── SecurityUtil.java           # Lấy user ID hiện tại từ SecurityContext
    │   │               │   └── DistanceCalculator.java     # Tính khoảng cách d km giữa 2 tọa độ
    │   │               │
    │   │               └── validator/                      # Custom Validation Annotations & Validators
    │   │                   ├── TaxCodeValidator.java       # Validator kiểm tra Mã số thuế 10/13 số
    │   │                   └── PhoneValidator.java         # Validator kiểm tra định dạng SĐT Việt Nam
    │   │
    │   └── resources/
    │       ├── application.yml                             # Configuration chính (DB, Port, JWT Secret)
    │       ├── application-dev.yml                         # Config môi trường Local/Development
    │       ├── application-prod.yml                        # Config môi trường Production
    │       └── db/migration/                               # Database Migration Scripts (Flyway/Liquibase)
    │           ├── V1__init_schema.sql                     # Script tạo 31 bảng CSDL v5.0
    │           └── V2__seed_initial_data.sql               # Script chèn dữ liệu khởi tạo (Roles, Admin account)
    │
    └── test/                                               # Automated Unit & Integration Tests (Test Coverage > 80%)
        └── java/
            └── vn/gaspro/api/
                ├── service/OrderServiceTest.java
                └── controller/AuthControllerTest.java
```

---

## 🎯 2. DIỄN GIẢI QUY TRÌNH LUỒNG DỮ LIỆU (DATA FLOW ARCHITECTURE)

Một HTTP Request từ Frontend gửi đến Backend sẽ đi qua lần lượt các tầng kiến trúc như sau:

```mermaid
sequenceDiagram
    autonumber
    actor FE as Frontend Client
    participant SEC as Security Filter (JWT)
    participant CTL as Controller Layer
    participant VAL as Validator / DTO
    participant SVC as Service Layer (Business Logic)
    participant MAP as MapStruct Mapper
    participant REP as Repository (Spring Data JPA)
    database DB as Database (MySQL/PostgreSQL)

    FE->>SEC: Gửi HTTP Request (Header: Authorization Bearer JWT)
    SEC->>SEC: Kiểm tra Token & Phân quyền RBAC
    SEC->>CTL: Chuyển Request tới REST Controller tương ứng
    CTL->>VAL: Validate dữ liệu RequestBody / RequestParam
    CTL->>SVC: Gọi hàm xử lý nghiệp vụ tại Service Layer
    SVC->>REP: Query / Mutation dữ liệu từ CSDL
    REP->>DB: Thực thi SQL Query / Transaction
    DB-->>REP: Trả về kết quả Raw Record
    REP-->>SVC: Trả về JPA Entity
    SVC->>MAP: Convert JPA Entity sang DTO Response
    MAP-->>SVC: Trả về Response DTO
    SVC-->>CTL: Trả về ApiResponse [code, message, data, paging]
    CTL-->>FE: Trả về JSON Response (HTTP Status 200/201/400...)
```

---

## 🏆 3. NGUYÊN TẮC LẬP TRÌNH & NORM NGUYÊN TẮC CLEAN CODE

1. **Không Expose JPA Entity ra bên ngoài API**: 100% Controllers chỉ nhận `Request DTO` và trả về `Response DTO` bọc trong `ApiResponse<T>`.
2. **Xử lý Lỗi Tập Trung (Global Exception Handling)**: Không viết `try-catch` tràn lan tại Controller. Khi xảy ra lỗi nghiệp vụ, ném `AppException(ErrorCode.XYZ)` để `GlobalExceptionHandler` tự động đóng gói lỗi chuẩn trả về cho FE.
3. **Bảo mật Transaction (ACID)**: Tất cả các thao tác ghi/sửa dữ liệu nhiều bảng (như Đặt hàng, Nhập kho Multi-item, Quyết toán ca) phải được gắn annotation `@Transactional(rollbackFor = Exception.class)`.
4. **Xử lý Tác vụ Nặng Bất đồng bộ (Async Processing)**: Việc gửi tin nhắn SMS, bắn Notification FCM hoặc gửi mail nhắc nợ được xử lý bất đồng bộ qua `@Async` hoặc Spring Events để giữ Response Time dưới 100ms.
