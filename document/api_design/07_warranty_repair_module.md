# DOCUMENTATION THIẾT KẾ API - MODULE 07: BẢO HÀNH, SỬA CHỮA & BẾP MƯỢN

> **Domain:** Warranty, Repair & Temporary Loan Operations  
> **Phiên bản API:** v1  
> **Base URL:** `/api/v1`  
> **Định dạng dữ liệu:** `application/json`

---

## 1. API Khách Hàng Gửi Yêu Cầu Bảo Hành & Đính Kèm Media (Create Warranty Ticket)

- **Domain:** Warranty Operations
- **Function:** Khách gửi yêu cầu bảo hành bếp gas / thiết bị (WR-001)
- **Description:** Cho phép khách hàng gửi ticket báo hỏng bếp trên App B2C. Quy định bắt buộc: Khách **phải nhập văn bản mô tả lỗi và đính kèm đường dẫn tệp Ảnh/Video (<10s) minh chứng thực tế**.
- **URL/API:** `/api/v1/customer/warranty-tickets`
- **Method:** `POST`
- **Authorization:** `Bearer Token` (`CUSTOMER`)
- **Header:**
  ```http
  Authorization: Bearer <access_token>
  Content-Type: application/json
  ```
- **RequestParam:** `None`
- **RequestBody:**
  ```json
  {
    "product_id": 5,
    "order_id": 12,
    "fault_description": "Bếp gas Rinnai RV-365 không đánh lửa được, đã thay pin mới nhưng vẫn không lên lửa",
    "media_proof_url": "https://storage.gaspro.vn/warranty/video-fault-bep-rinnai-01.mp4"
  }
  ```
- **Response:**
  - **Success (201 Created):**
    ```json
    {
      "code": 201,
      "message": "Đã gửi yêu cầu bảo hành thành công! Vui lòng chờ kỹ thuật viên liên hệ hỗ trợ.",
      "data": {
        "ticket_id": 15,
        "ticket_code": "BH-20260805-001",
        "product_name": "Bếp gas Rinnai RV-365",
        "fault_description": "Bếp gas Rinnai RV-365 không đánh lửa được, đã thay pin mới nhưng vẫn không lên lửa",
        "media_proof_url": "https://storage.gaspro.vn/warranty/video-fault-bep-rinnai-01.mp4",
        "status": "PENDING",
        "created_at": "2026-08-05T09:00:00Z"
      },
      "paging": null
    }
    ```
- **Reference:** BRD v8 (WR-001), `warranty_tickets`.

---

## 2. API Tra Cứu Danh Sách Ticket Bảo Hành (List Warranty Tickets)

- **Domain:** Warranty Operations
- **Function:** Xem danh sách ticket bảo hành có bộ lọc và phân trang
- **Description:** Trả về danh sách ticket bảo hành hỗ trợ lọc theo trạng thái, khoảng thời gian và phân trang.
- **URL/API:** `/api/v1/warranty-tickets`
- **Method:** `GET`
- **Authorization:** `Bearer Token` (`CUSTOMER`, `OPERATOR`, `DRIVER`, `ADMIN`)
- **Header:**
  ```http
  Authorization: Bearer <access_token>
  ```
- **RequestParam:**
  - `status` (string, optional): Trạng thái (`PENDING`, `ASSIGNED`, `IN_PROGRESS`, `WAITING_PARTS_APPROVAL`, `COMPLETED`, `CANCELLED`).
  - `from_date` (date, optional): Từ ngày (`YYYY-MM-DD`).
  - `to_date` (date, optional): Đến ngày (`YYYY-MM-DD`).
  - `page` (integer, optional, default: 1): Trang hiện tại.
  - `limit` (integer, optional, default: 10): Số ticket/trang.
- **RequestBody:** `None`
- **Response:**
  - **Success (200 OK):**
    ```json
    {
      "code": 200,
      "message": "Lấy danh sách ticket bảo hành thành công",
      "data": [
        {
          "ticket_id": 15,
          "ticket_code": "BH-20260805-001",
          "customer_name": "Nguyễn Thị Lan",
          "product_name": "Bếp gas Rinnai RV-365",
          "driver_name": "Lê Minh Tuấn",
          "status": "IN_PROGRESS",
          "created_at": "2026-08-05T09:00:00Z"
        }
      ],
      "paging": {
        "page": 1,
        "limit": 10,
        "total_items": 8,
        "total_pages": 1
      }
    }
    ```
- **Reference:** `warranty_tickets`.

---

## 3. API Đề Xuất Báo Giá Linh Kiện Thay Thế Tại Nhà Khách (Driver Propose Spare Parts)

- **Domain:** Repair & Spare Parts
- **Function:** Tài xế lập đề xuất thay linh kiện từ hòm đồ di động (WR-004)
- **Description:** Khi kiểm tra tại nhà khách, tài xế chọn linh kiện cần thay từ kho di động trên xe (`driver_mobile_inventories`) để tạo đề xuất báo giá, kích hoạt quy trình duyệt 3 bên (Tài xế ➔ Tổng đài ➔ Khách hàng).
- **URL/API:** `/api/v1/driver/warranty-proposals`
- **Method:** `POST`
- **Authorization:** `Bearer Token` (`DRIVER`)
- **Header:**
  ```http
  Authorization: Bearer <access_token>
  Content-Type: application/json
  ```
- **RequestParam:** `None`
- **RequestBody:**
  ```json
  {
    "ticket_id": 15,
    "product_id": 7,
    "quantity": 1,
    "unit_price": 150000
  }
  ```
- **Response:**
  - **Success (201 Created):**
    ```json
    {
      "code": 201,
      "message": "Đã gửi đề xuất thay linh kiện! Vui lòng chờ Tổng đài và Khách hàng bấm duyệt trên ứng dụng.",
      "data": {
        "proposal_id": 28,
        "ticket_code": "BH-20260805-001",
        "spare_part_name": "IC đánh lửa Rinnai",
        "quantity": 1,
        "unit_price": 150000,
        "subtotal": 150000,
        "operator_approval_status": "PENDING",
        "customer_approval_status": "PENDING"
      },
      "paging": null
    }
    ```
- **Reference:** BRD v8 (WR-004), `warranty_parts_proposals`, `driver_mobile_inventories`.

---

## 4. API Phê Duyệt Đề Xuất Linh Kiện 3 Bên (Approve Spare Parts Proposal)

- **Domain:** Repair Approval
- **Function:** Tổng đài hoặc Khách hàng bấm duyệt đề xuất thay linh kiện (WR-004)
- **Description:**
  - Tổng đài duyệt (`PATCH /api/v1/operator/warranty-proposals/{id}/approve`): Chuyển `operator_approval_status = APPROVED`.
  - Khách hàng duyệt (`PATCH /api/v1/customer/warranty-proposals/{id}/approve`): Chuyển `customer_approval_status = APPROVED`. Khi cả 2 bên duyệt, tài xế mới được phép tiến hành thay đồ.
- **URL/API:** `/api/v1/customer/warranty-proposals/{id}/approve`
- **Method:** `PATCH`
- **Authorization:** `Bearer Token` (`CUSTOMER`, `OPERATOR`, `ADMIN`)
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
      "message": "Xác nhận đồng ý thay thế linh kiện thành công! Tài xế đã được cấp phép tiến hành sửa chữa.",
      "data": {
        "proposal_id": 28,
        "spare_part_name": "IC đánh lửa Rinnai",
        "subtotal": 150000,
        "operator_approval_status": "APPROVED",
        "customer_approval_status": "APPROVED",
        "is_fully_approved": true
      },
      "paging": null
    }
    ```
- **Reference:** BRD v8 (WR-004), `warranty_parts_proposals`.

---

## 5. API Bàn Giao Bếp Mượn Dùng Tạm Cho Khách (Lend Temporary Stove - Standard RESTful)

- **Domain:** Temporary Stove Loan
- **Function:** Tài xế bàn giao 1 bếp mượn dùng tạm cho khách (WR-005)
- **Description:** Tạo mới khoản mượn bếp tạm, chuyển `temporary_stoves.status = 'BORROWED'` và gửi SMS xác nhận cho khách hàng.
- **URL/API:** `/api/v1/temporary-stoves/loans`
- **Method:** `POST`
- **Authorization:** `Bearer Token` (`DRIVER`, `OPERATOR`, `ADMIN`)
- **Header:**
  ```http
  Authorization: Bearer <access_token>
  Content-Type: application/json
  ```
- **RequestParam:** `None`
- **RequestBody:**
  ```json
  {
    "temporary_stove_id": 1,
    "ticket_id": 15,
    "customer_id": 45,
    "notes": "Bàn giao bếp tạm Rinnai RV-150 cho khách mượn trong thời gian sửa bếp chính 3 ngày"
  }
  ```
- **Response:**
  - **Success (201 Created):**
    ```json
    {
      "code": 201,
      "message": "Bàn giao bếp mượn tạm thành công! Bếp đã chuyển sang trạng thái ĐANG CHO MƯỢN. Khách hàng đã nhận được SMS xác nhận.",
      "data": {
        "loan_id": 10,
        "stove_code": "BEP-TMP-001",
        "model_name": "Rinnai RV-150",
        "customer_name": "Nhà hàng Biển Đông",
        "loan_status": "ACTIVE",
        "borrowed_at": "2026-08-05T09:30:00Z"
      },
      "paging": null
    }
    ```
- **Reference:** BRD v8 (WR-005), `temporary_stoves`, `temporary_stove_loans`.

---

## 6. API Thu Hồi Bếp Mượn Dùng Tạm Khi Sửa Xong (Reclaim Temporary Stove - Standard RESTful)

- **Domain:** Temporary Stove Loan
- **Function:** Thu hồi bếp mượn quay về kho đại lý khi sửa xong bếp chính (WR-005)
- **Description:** Cập nhật lượt mượn `temporary_stove_loans.loan_status = 'RETURNED'`, chuyển `temporary_stoves.status = 'AVAILABLE'` để sẵn sàng cho lượt mượn tiếp theo.
- **URL/API:** `/api/v1/temporary-stoves/loans/{loan_id}/return`
- **Method:** `PATCH`
- **Authorization:** `Bearer Token` (`DRIVER`, `OPERATOR`, `ADMIN`)
- **Header:**
  ```http
  Authorization: Bearer <access_token>
  Content-Type: application/json
  ```
- **RequestParam:** `None`
- **RequestBody:**
  ```json
  {
    "notes": "Đã thu hồi bếp tạm nguyên vẹn, chuyển trả kho đại lý"
  }
  ```
- **Response:**
  - **Success (200 OK):**
    ```json
    {
      "code": 200,
      "message": "Đã thu hồi bếp dùng tạm thành công! Bếp đã chuyển về trạng thái RẢNH sẵn sàng bàn giao.",
      "data": {
        "loan_id": 10,
        "stove_code": "BEP-TMP-001",
        "loan_status": "RETURNED",
        "returned_at": "2026-08-06T14:00:00Z"
      },
      "paging": null
    }
    ```
- **Reference:** BRD v8 (WR-005), `temporary_stoves`, `temporary_stove_loans`.
