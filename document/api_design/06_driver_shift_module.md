# DOCUMENTATION THIẾT KẾ API - MODULE 06: QUẢN LÝ TÀI XẾ, SỰ CỐ & QUYẾT TOÁN CA

> **Domain:** Driver & Shift Operations  
> **Phiên bản API:** v1  
> **Base URL:** `/api/v1/driver`  
> **Định dạng dữ liệu:** `application/json`

---

## 1. API Bật / Tắt Trạng Thái Trực Tuyến Tài Xế (Toggle Online/Offline Status)

- **Domain:** Driver Operations
- **Function:** Tài xế bật/tắt chế độ nhận đơn (SD-001)
- **Description:** Cho phép tài xế chuyển trạng thái Trực tuyến (Online) / Ngoại tuyến (Offline). Hệ thống lấy thông tin tài xế từ JWT Token (`/me/status`) và ghi log vào `driver_availability_logs` để theo dõi thời gian trực ca.
- **URL/API:** `/api/v1/driver/me/status`
- **Method:** `PATCH`
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
    "is_online": true,
    "location_lat_long": "10.776889, 106.700806"
  }
  ```
- **Response:**
  - **Success (200 OK):**
    ```json
    {
      "code": 200,
      "message": "Đã chuyển trạng thái: TRỰC TUYẾN (Sẵn sàng nhận đơn)",
      "data": {
        "driver_id": 105,
        "is_online": true,
        "active_orders_count": 0,
        "updated_at": "2026-08-05T07:00:00Z"
      },
      "paging": null
    }
    ```
- **Reference:** BRD v8 (SD-001), `driver_profiles`, `driver_availability_logs`.

---

## 2. API Báo Sự Cố Hỏng Xe Dọc Đường (Report Vehicle Breakdown Incident)

- **Domain:** Driver Incident
- **Function:** Báo sự cố hỏng xe/tai nạn để giải phóng đơn về Chợ đơn (SD-003)
- **Description:** Khi đang đi giao mà bị hỏng xe hoặc tai nạn, tài xế chọn lý do và **bắt buộc tải lên 1 ảnh minh chứng thực địa**. Hệ thống tạo dòng trong `transit_incidents` và tự động giải phóng đơn hàng quay về Chợ đơn cho tài xế khác nhận.
- **URL/API:** `/api/v1/driver/incidents`
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
    "order_id": 99,
    "incident_type": "VEHICLE_BREAKDOWN",
    "description": "Bị nổ lốp xe máy tại đường Nguyễn Trãi",
    "proof_image_url": "https://storage.gaspro.vn/incidents/broken-tire-99.jpg"
  }
  ```
- **Response:**
  - **Success (201 Created):**
    ```json
    {
      "code": 201,
      "message": "Đã ghi nhận báo sự cố! Đơn hàng #DH-20260805-099 đã được giải phóng về Chợ đơn hàng cho tài xế khác. Vui lòng mang vỏ/bình gas cũ về kho sau khi sửa xe.",
      "data": {
        "incident_id": 12,
        "order_code": "DH-20260805-099",
        "incident_type": "VEHICLE_BREAKDOWN",
        "order_status": "PENDING"
      },
      "paging": null
    }
    ```
- **Reference:** BRD v8 (SD-003), `transit_incidents`, `orders`.

---

## 3. API Quyết Toán Tiền COD & Khóa Ca Làm Việc Cuối Ngày (Reconcile Shift COD)

- **Domain:** Operator Shift Settlement
- **Function:** Thủ kho đếm tiền COD nộp và chốt đóng ca (RC-001)
- **Description:** Thủ kho nhập số tiền COD thực nhận từ tài xế nộp tại quầy. Hệ thống so sánh với `expected_cod`, tính chênh lệch thiếu/đủ (`cod_deficit`) và ghi nợ trừ lương ca nếu nộp thiếu.
- **URL/API:** `/api/v1/operator/shifts/reconcile-cod`
- **Method:** `POST`
- **Authorization:** `Bearer Token` (`ADMIN`, `OPERATOR`)
- **Header:**
  ```http
  Authorization: Bearer <access_token>
  Content-Type: application/json
  ```
- **RequestParam:** `None`
- **RequestBody:**
  ```json
  {
    "shift_id": 88,
    "actual_cod_cash": 2300000,
    "notes": "Tài xế nộp thiếu 200k, đồng ý trừ vào lương ca"
  }
  ```
- **Response:**
  - **Success (200 OK):**
    ```json
    {
      "code": 200,
      "message": "Đã chốt đối soát tiền COD ca thành công!",
      "data": {
        "shift_id": 88,
        "driver_name": "Lê Minh Tuấn",
        "expected_cod": 2500000,
        "actual_cod_cash": 2300000,
        "actual_cod_qr": 0,
        "cod_deficit": 200000,
        "total_trip_salary": 450000,
        "net_payout": 250000,
        "shift_status": "CLOSED"
      },
      "paging": null
    }
    ```
- **Reference:** BRD v8 (RC-001), `driver_shifts`, `driver_wallet_transactions`.

---

## 4. API Đối Soát Vỏ Rỗng Chéo Hãng Cuối Ca (Reconcile Shift Empty Shells)

- **Domain:** Operator Shift Settlement
- **Function:** Thủ kho đếm số vỏ rỗng tài xế mang về theo từng hãng (RC-003)
- **Description:** Thủ kho kiểm đếm số vỏ rỗng thực tế tài xế trả về theo từng dòng hãng (`PG_RONG`, `TOTAL_RONG`, `PMG_RONG`). Hệ thống tự động phát hiện đổi chéo hãng và tính tiền phạt đền vỏ (`shell_difference * 500,000đ/vỏ`) trừ vào lương tài xế nếu làm mất vỏ.
- **URL/API:** `/api/v1/operator/shifts/reconcile-shells`
- **Method:** `POST`
- **Authorization:** `Bearer Token` (`ADMIN`, `OPERATOR`)
- **Header:**
  ```http
  Authorization: Bearer <access_token>
  Content-Type: application/json
  ```
- **RequestParam:** `None`
- **RequestBody:**
  ```json
  {
    "shift_id": 88,
    "shell_items": [
      {
        "brand_id": 1,
        "expected_shell_count": 5,
        "actual_shell_count": 4,
        "cross_exchange_shells": 0
      },
      {
        "brand_id": 2,
        "expected_shell_count": 0,
        "actual_shell_count": 1,
        "cross_exchange_shells": 1
      }
    ]
  }
  ```
- **Response:**
  - **Success (200 OK):**
    ```json
    {
      "code": 200,
      "message": "Đã chốt đối soát vỏ rỗng ca thành công!",
      "data": {
        "shift_id": 88,
        "shell_reconciliations": [
          {
            "brand_name": "Petrolimex",
            "expected": 5,
            "actual": 4,
            "difference": 1,
            "penalty_amount": 500000,
            "status": "THIẾU 1 VỎ (Phạt 500k)"
          },
          {
            "brand_name": "Totalgaz",
            "expected": 0,
            "actual": 1,
            "difference": -1,
            "penalty_amount": 0,
            "status": "ĐỔI CHÉO HÃNG (+1 vỏ Total)"
          }
        ],
        "total_loss_penalty": 500000
      },
      "paging": null
    }
    ```
- **Reference:** BRD v8 (RC-003), `shift_shell_reconciliations`, `driver_shifts`.

---

## 5. API Kiểm Tra Niêm Phong & Lập Biên Bản Vi Phạm Đơn Hủy (Inspect Seal & Violation Protocol)

- **Domain:** Operator Shift Settlement
- **Function:** Thủ kho kiểm tra vỏ niêm phong bình gas hoàn kho khi hủy đơn (RC-004)
- **Description:** Kiểm tra tình trạng niêm phong bình gas khi đơn bị hủy mang về kho. Nếu niêm phong "Nguyên vẹn" ➔ Hoàn kho gas đầy. Nếu "Bị rách" ➔ Bắt buộc lập biên bản vi phạm tài xế.
- **URL/API:** `/api/v1/operator/orders/{id}/inspect-seal`
- **Method:** `POST`
- **Authorization:** `Bearer Token` (`ADMIN`, `OPERATOR`)
- **Header:**
  ```http
  Authorization: Bearer <access_token>
  Content-Type: application/json
  ```
- **RequestParam:** `None`
- **RequestBody:**
  ```json
  {
    "seal_status": "BROKEN",
    "seal_violation_note": "Tài xế xé niêm phong màng co khi khách chưa đồng ý nhận hàng"
  }
  ```
- **Response:**
  - **Success (200 OK):**
    ```json
    {
      "code": 200,
      "message": "Đã lập biên bản vi phạm niêm phong tài xế thành công!",
      "data": {
        "order_id": 99,
        "order_code": "DH-20260805-099",
        "seal_status": "BROKEN",
        "seal_violation_note": "Tài xế xé niêm phong màng co khi khách chưa đồng ý nhận hàng",
        "action": "Lập biên bản vi phạm — Không cộng hoàn kho gas đầy"
      },
      "paging": null
    }
    ```
- **Reference:** BRD v8 (RC-004), `orders`.
