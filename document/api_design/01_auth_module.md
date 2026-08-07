# DOCUMENTATION THIẾT KẾ API - MODULE 01: AUTHENTICATION & AUTHORIZATION

> **Domain:** Authentication & Identity Management  
> **Phiên bản API:** v1  
> **Base URL:** `/api/v1/auth`  
> **Định dạng dữ liệu:** `application/json`

---

## 1. API Đăng Ký Tài Khoản Khách Hàng (Customer Registration)

- **Domain:** Authentication
- **Function:** Đăng ký tài khoản khách hàng mới (UM-001)
- **Description:** Cho phép khách hàng đăng ký tài khoản bằng Số điện thoại và Mật khẩu. Kiểm tra tính trùng lặp SĐT, mã hóa mật khẩu và tự động khởi tạo hồ sơ Khách hàng (`customers`).
- **URL/API:** `/api/v1/auth/register`
- **Method:** `POST`
- **Authorization:** `Public` (Không yêu cầu Token)
- **Header:**
  ```http
  Content-Type: application/json
  ```
- **RequestParam:** `None`
- **RequestBody:**
  ```json
  {
    "phone": "0901234567",
    "password": "Password123!",
    "full_name": "Phạm Hoàng Anh",
    "email": "hoanganh@gmail.com",
    "delivery_address": "123 Nguyễn Trãi, Phường Bến Thành, Quận 1, TP.HCM"
  }
  ```
- **Response:**
  - **Success (201 Created):**
    ```json
    {
      "code": 201,
      "message": "Đăng ký tài khoản thành công! Tự động tạo hồ sơ khách hàng.",
      "data": {
        "user_id": 101,
        "phone": "0901234567",
        "full_name": "Phạm Hoàng Anh",
        "role_code": "CUSTOMER",
        "access_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
        "token_type": "Bearer",
        "expires_in": 86400
      },
      "paging": null
    }
    ```
  - **Error - Conflict (409 Conflict):**
    ```json
    {
      "code": 409,
      "message": "Số điện thoại [0901234567] đã được đăng ký trên hệ thống. Vui lòng đăng nhập hoặc sử dụng tính năng Quên mật khẩu.",
      "data": {
        "field_errors": [
          {
            "field": "phone",
            "error_code": "DUPLICATE_PHONE",
            "rejected_value": "0901234567"
          }
        ]
      }
    }
    ```
  - **Error - Bad Request (400 Bad Request):**
    ```json
    {
      "code": 400,
      "message": "Dữ liệu đăng ký không hợp lệ.",
      "data": {
        "field_errors": [
          {
            "field": "password",
            "error_code": "WEAK_PASSWORD",
            "message": "Mật khẩu phải từ 6 ký tự trở lên."
          }
        ]
      }
    }
    ```
- **Note:** Khi đăng ký thành công, hệ thống tự động gán `role_code = 'CUSTOMER'` và tạo 1 dòng tương ứng trong bảng `customers` với trạng thái nợ mặc định `debt_status = 'INELIGIBLE'`.
- **Reference:** BRD v8 (Mục 3.1.A - UM-001), `users`, `customers`.

---

## 2. API Đăng Nhập Hệ Thống (Universal Login)

- **Domain:** Authentication
- **Function:** Đăng nhập hệ thống đa vai trò (UM-002)
- **Description:** Đăng nhập cho tất cả các vai trò (Admin, Operator, Driver, Customer) bằng Số điện thoại & Mật khẩu. Trả về JWT Access Token kèm quyền hạn tương ứng.
- **URL/API:** `/api/v1/auth/login`
- **Method:** `POST`
- **Authorization:** `Public`
- **Header:**
  ```http
  Content-Type: application/json
  ```
- **RequestParam:** `None`
- **RequestBody:**
  ```json
  {
    "phone": "0901234567",
    "password": "Password123!"
  }
  ```
- **Response:**
  - **Success (200 OK):**
    ```json
    {
      "code": 200,
      "message": "Đăng nhập thành công!",
      "data": {
        "user_id": 101,
        "phone": "0901234567",
        "full_name": "Phạm Hoàng Anh",
        "role_code": "CUSTOMER",
        "access_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMDEiLCJyb2xlIjoiQ1VTVE9NRVIiLCJpYXQiOjE3NTk2NDQ4MDB9...",
        "refresh_token": "def567890abcdef...",
        "token_type": "Bearer",
        "expires_in": 86400
      },
      "paging": null
    }
    ```
  - **Error - Unauthorized (401 Unauthorized):**
    ```json
    {
      "code": 401,
      "message": "Số điện thoại hoặc mật khẩu không chính xác. Vui lòng kiểm tra lại.",
      "data": null
    }
    ```
  - **Error - Account Locked (403 Forbidden):**
    ```json
    {
      "code": 403,
      "message": "Tài khoản của bạn đã bị khóa do vi phạm chính sách hệ thống. Vui lòng liên hệ Tổng đài 1900-1234.",
      "data": {
        "status": "LOCKED"
      }
    }
    ```
- **Note:** Frontend cần lưu `access_token` vào LocalStorage/Cookie và gắn vào Header `Authorization: Bearer <access_token>` trong tất cả các request sau.
- **Reference:** BRD v8 (UM-002), `users`, `roles`.

---

## 3. API Làm Mới Token (Refresh Access Token)

- **Domain:** Authentication
- **Function:** Làm mới JWT Token hết hạn
- **Description:** Đổi `refresh_token` lấy `access_token` mới mà không yêu cầu người dùng đăng nhập lại.
- **URL/API:** `/api/v1/auth/refresh-token`
- **Method:** `POST`
- **Authorization:** `Public`
- **Header:**
  ```http
  Content-Type: application/json
  ```
- **RequestParam:** `None`
- **RequestBody:**
  ```json
  {
    "refresh_token": "def567890abcdef..."
  }
  ```
- **Response:**
  - **Success (200 OK):**
    ```json
    {
      "code": 200,
      "message": "Làm mới Token thành công!",
      "data": {
        "access_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
        "expires_in": 86400
      },
      "paging": null
    }
    ```
  - **Error - Invalid Token (401 Unauthorized):**
    ```json
    {
      "code": 401,
      "message": "Refresh Token đã hết hạn hoặc không hợp lệ. Vui lòng đăng nhập lại.",
      "data": null
    }
    ```
- **Note:** Tránh bắt người dùng đăng nhập lại khi Access Token hết hạn sau 24h.
- **Reference:** `users`.

---

## 4. API Lấy Thông Tin Người Dùng Hiện Tại (Get Current Profile)

- **Domain:** Authentication
- **Function:** Lấy thông tin tài khoản và danh sách quyền hạn
- **Description:** Trả về chi tiết người dùng đang đăng nhập dựa theo JWT Token.
- **URL/API:** `/api/v1/auth/me`
- **Method:** `GET`
- **Authorization:** `Bearer Token` (`ADMIN`, `OPERATOR`, `DRIVER`, `CUSTOMER`)
- **Header:**
  ```http
  Authorization: Bearer <access_token>
  ```
- **RequestParam:** `None`
- **RequestBody:** `None`
- **Response:**
  - **Success (200 OK):**
    ```json
    {
      "code": 200,
      "message": "Lấy thông tin người dùng thành công",
      "data": {
        "user_id": 101,
        "phone": "0901234567",
        "full_name": "Phạm Hoàng Anh",
        "email": "hoanganh@gmail.com",
        "role": {
          "code": "CUSTOMER",
          "name": "Khách hàng"
        },
        "permissions": [
          "ORDER_CREATE",
          "ORDER_VIEW_MY",
          "WARRANTY_CREATE"
        ]
      },
      "paging": null
    }
    ```
- **Note:** Giúp Frontend kiểm tra quyền (RBAC) để ẩn/hiện nút bấm tương ứng trên UI.
- **Reference:** `users`, `roles`, `permissions`.
