# DOCUMENTATION THIẾT KẾ API - MODULE 08: CẤU HÌNH HẰNG SỐ HỆ THỐNG

> **Domain:** System Configuration  
> **Phiên bản API:** v1  
> **Base URL:** `/api/v1/admin/settings`  
> **Định dạng dữ liệu:** `application/json`

---

## 1. API Lấy Danh Sách Tham Số Cấu Hình Hệ Thống (Get System Settings)

- **Domain:** System Settings
- **Function:** Xem toàn bộ tham số cấu hình vận hành
- **Description:** Trả về danh sách hằng số cấu hình hệ thống (`D_RATE`, `T_LOCK_OUT`, `T_ORDER_TIMEOUT`, `SAFETY_STOCK_THRESHOLD`...) phục vụ hiển thị form Cấu hình Admin (`admin_settings.html`).
- **URL/API:** `/api/v1/admin/settings`
- **Method:** `GET`
- **Authorization:** `Bearer Token` (`ADMIN`, `OPERATOR`)
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
      "message": "Lấy danh sách cấu hình hệ thống thành công",
      "data": [
        {
          "setting_key": "D_RATE",
          "setting_value": "5000",
          "data_type": "NUMBER",
          "description": "Đơn giá lương chuyến theo khoảng cách (VNĐ/km)"
        },
        {
          "setting_key": "T_LOCK_OUT",
          "setting_value": "30",
          "data_type": "NUMBER",
          "description": "Thời gian phạt khóa App khi từ chối đơn gán (Phút)"
        },
        {
          "setting_key": "DEBT_LIMIT_RETAIL",
          "setting_value": "1000000",
          "data_type": "NUMBER",
          "description": "Hạn mức nợ tối đa cho Khách lẻ B2C (VNĐ)"
        },
        {
          "setting_key": "DEBT_LIMIT_WHOLESALE",
          "setting_value": "10000000",
          "data_type": "NUMBER",
          "description": "Hạn mức nợ tối đa cho Khách sỉ B2B (VNĐ)"
        },
        {
          "setting_key": "CYLINDER_LOSS_FINE",
          "setting_value": "500000",
          "data_type": "NUMBER",
          "description": "Tiền phạt đền vỏ rỗng khi làm mất vỏ (VNĐ/vỏ)"
        }
      ],
      "paging": null
    }
    ```
- **Reference:** `system_settings`.

---

## 2. API Cập Nhật Hằng Số Cấu Hình Hệ Thống (Update System Settings)

- **Domain:** System Settings
- **Function:** Admin điều chỉnh tham số vận hành
- **Description:** Cho phép Admin cập nhật giá trị các tham số vận hành hệ thống. Hệ thống kiểm tra tính hợp lệ và áp dụng cho tất cả các đơn hàng / ca làm việc mới.
- **URL/API:** `/api/v1/admin/settings`
- **Method:** `PUT`
- **Authorization:** `Bearer Token` (`ADMIN`)
- **Header:**
  ```http
  Authorization: Bearer <access_token>
  Content-Type: application/json
  ```
- **RequestParam:** `None`
- **RequestBody:**
  ```json
  {
    "settings": [
      {
        "setting_key": "D_RATE",
        "setting_value": "5000"
      },
      {
        "setting_key": "T_LOCK_OUT",
        "setting_value": "30"
      },
      {
        "setting_key": "CYLINDER_LOSS_FINE",
        "setting_value": "500000"
      }
    ]
  }
  ```
- **Response:**
  - **Success (200 OK):**
    ```json
    {
      "code": 200,
      "message": "Cập nhật cấu hình hệ thống thành công! Thay đổi sẽ áp dụng cho tất cả các đơn hàng và ca làm việc mới.",
      "data": {
        "updated_count": 3,
        "updated_at": "2026-08-05T09:00:00Z"
      },
      "paging": null
    }
    ```
  - **Error - Invalid Parameter Value (400 Bad Request):**
    ```json
    {
      "code": 400,
      "message": "Giá trị tham số [D_RATE] không hợp lệ. Phải là số dương lớn hơn 0.",
      "data": {
        "field_errors": [
          {
            "field": "settings[0].setting_value",
            "error_code": "INVALID_NUMBER_RANGE",
            "rejected_value": "-100"
          }
        ]
      }
    }
    ```
- **Note:** Đơn hàng cũ giữ nguyên các tham số tại thời điểm tạo đơn.
- **Reference:** `system_settings`.
