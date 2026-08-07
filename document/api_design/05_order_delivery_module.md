# DOCUMENTATION THIẾT KẾ API - MODULE 05: ĐƠN BÁN HÀNG & GIAO HÀNG

> **Domain:** Order & Delivery Operations  
> **Phiên bản API:** v1  
> **Base URL:** `/api/v1/orders`  
> **Định dạng dữ liệu:** `application/json`

---

## 1. API Đặt Hàng Bán Gas & Thiết Bị (Create Order)

- **Domain:** Order Operations
- **Function:** Tạo đơn hàng mới từ Web/App Khách hoặc Tổng Đài (CO-003, OP-001)
- **Description:** Tạo đơn bán hàng gồm kiểm tra giỏ hàng/danh sách món, tính phí giao hàng dựa theo khoảng cách $d$ km, kiểm tra điều kiện thanh toán Ghi nợ (`CREDIT_DEBT`), khóa giá bán niêm yết tại thời điểm checkout (CO-004) và khởi tạo đơn ở trạng thái `PENDING` (Chờ nhận đơn).
- **URL/API:** `/api/v1/orders`
- **Method:** `POST`
- **Authorization:** `Bearer Token` (`CUSTOMER`, `OPERATOR`, `ADMIN`)
- **Header:**
  ```http
  Authorization: Bearer <access_token>
  Content-Type: application/json
  ```
- **RequestParam:** `None`
- **RequestBody:**
  ```json
  {
    "delivery_address": "123 Nguyễn Trãi, Phường Bến Thành, Quận 1, TP.HCM",
    "distance_km": 3.5,
    "payment_method": "CREDIT_DEBT",
    "notes": "Giao gấp trước 11h trưa",
    "items": [
      {
        "product_id": 1,
        "quantity": 2,
        "has_exchange_shell": true
      },
      {
        "product_id": 2,
        "quantity": 1,
        "has_exchange_shell": false
      }
    ]
  }
  ```
- **Response:**
  - **Success (201 Created):**
    ```json
    {
      "code": 201,
      "message": "Đặt hàng thành công! Đơn hàng đã được đưa lên Chợ đơn hàng.",
      "data": {
        "order_id": 99,
        "order_code": "DH-20260805-099",
        "customer_name": "Nhà hàng Biển Đông",
        "delivery_address": "123 Nguyễn Trãi, Q.1, TP.HCM",
        "distance_km": 3.5,
        "payment_method": "CREDIT_DEBT",
        "payment_status": "UNPAID",
        "order_status": "PENDING",
        "total_goods_amount": 1990000,
        "total_deposit_amount": 500000,
        "shipping_fee": 17500,
        "grand_total": 2507500,
        "created_at": "2026-08-05T09:00:00Z"
      },
      "paging": null
    }
    ```
- **Note:** Khi đơn hàng chuyển sang `PENDING`, hệ thống tự động bắn **WebSocket / FCM Event `NEW_ORDER_AVAILABLE`** tới tất cả App Tài xế đang Online.
- **Reference:** BRD v8 (CO-003, CD-001, CO-004), `orders`, `order_items`, `customers`.

---

## 2. API Tra Cứu Danh Sách Đơn Hàng (List & Filter Orders)

- **Domain:** Order Operations
- **Function:** Xem danh sách đơn hàng cho Khách, Tổng đài và Admin (OP-001, CO-005)
- **Description:** Trả về danh sách đơn hàng có hỗ trợ bộ lọc đa tiêu chí (Trạng thái đơn, PTTT, khoảng thời gian, từ khóa tìm kiếm) và phân trang.
- **URL/API:** `/api/v1/orders`
- **Method:** `GET`
- **Authorization:** `Bearer Token` (`CUSTOMER`, `OPERATOR`, `ADMIN`)
- **Header:**
  ```http
  Authorization: Bearer <access_token>
  ```
- **RequestParam:**
  - `status` (string, optional): Trạng thái đơn (`PENDING`, `ASSIGNED`, `ACCEPTED`, `DELIVERING`, `COMPLETED`, `CANCELLED`).
  - `payment_method` (string, optional): PTTT (`COD`, `VIETQR`, `CREDIT_DEBT`).
  - `payment_status` (string, optional): Trạng thái thanh toán (`UNPAID`, `PAID`, `PENDING_PAYMENT`).
  - `query` (string, optional): Từ khóa mã đơn/SĐT/Tên khách.
  - `from_date` (date, optional): Từ ngày đặt (`YYYY-MM-DD`).
  - `to_date` (date, optional): Đến ngày đặt (`YYYY-MM-DD`).
  - `page` (integer, optional, default: 1): Trang hiện tại.
  - `limit` (integer, optional, default: 10): Số đơn/trang.
- **RequestBody:** `None`
- **Response:**
  - **Success (200 OK):**
    ```json
    {
      "code": 200,
      "message": "Lấy danh sách đơn hàng thành công",
      "data": [
        {
          "order_id": 99,
          "order_code": "DH-20260805-099",
          "customer_name": "Nhà hàng Biển Đông",
          "driver_name": "Lê Minh Tuấn",
          "grand_total": 2507500,
          "payment_method": "CREDIT_DEBT",
          "payment_status": "UNPAID",
          "order_status": "DELIVERING",
          "created_at": "2026-08-05T09:00:00Z"
        }
      ],
      "paging": {
        "page": 1,
        "limit": 10,
        "total_items": 42,
        "total_pages": 5
      }
    }
    ```
- **Reference:** BRD v8 (OP-001, CO-005), `orders`.

---

## 3. API Tài Xế Giật Đơn Hàng Từ Chợ Đơn (Driver Claim Order)

- **Domain:** Delivery Operations
- **Function:** Tài xế chủ động nhận/giật đơn hàng trên Chợ (SD-001, SD-002)
- **Description:** Cho phép Tài xế Online bấm Giật đơn. Hệ thống kiểm tra ràng buộc: Tài xế không bị khóa App (`locked_until`), chưa giữ quá 3 đơn đang giao (`active_orders_count < 3`), và bảo đảm cơ chế chống tranh chấp giật đơn cùng lúc (Race Condition Prevention).
- **URL/API:** `/api/v1/driver/orders/{id}/claim`
- **Method:** `POST`
- **Authorization:** `Bearer Token` (`DRIVER`)
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
      "message": "Giật đơn thành công! Đơn hàng #DH-20260805-099 đã chuyển sang trạng thái Đang giao.",
      "data": {
        "order_id": 99,
        "order_code": "DH-20260805-099",
        "driver_name": "Lê Minh Tuấn",
        "order_status": "ACCEPTED",
        "active_orders_count": 2,
        "accepted_at": "2026-08-05T09:05:00Z"
      },
      "paging": null
    }
    ```
- **Note Kỹ thuật (Real-time Notification Architecture):**
  > Hệ thống sử dụng **WebSocket / Firebase Cloud Messaging (FCM)** để push sự kiện `NEW_ORDER_AVAILABLE` về App Tài xế ngay khi đơn hàng khởi tạo thành công ở trạng thái `PENDING`. Tài xế nhận thông báo đẩy (Push Notification) sẽ mở màn hình Chợ đơn và gọi API Claim này để giật đơn.
- **Reference:** BRD v8 (SD-001, SD-002, SD-004), `orders`, `driver_profiles`.

---

## 4. API Báo Lỗi Ngân Hàng - Chuyển Nợ Tạm Thời 24h (Driver Submit Pending Payment Proof)

- **Domain:** Delivery Operations / Payment
- **Function:** Chuyển trạng thái đơn sang Đã Giao - Nợ Tạm Thời 24h (CD-003)
- **Description:** Khi giao hàng xong, nếu ngân hàng bảo trì làm QR VietQR thất bại và khách không có tiền mặt, tài xế chụp ảnh minh chứng màn hình ngân hàng báo lỗi để chuyển đơn sang `PENDING_PAYMENT` (Nợ tạm thời 24h). Khách có 24h để thanh toán mà không bị phạt nợ quá hạn.
- **URL/API:** `/api/v1/driver/orders/{id}/pending-payment`
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
    "proof_image_url": "https://storage.gaspro.vn/proofs/bank-error-99.jpg",
    "note": "Ngân hàng Vietcombank báo bảo trì hệ thống lúc 10h15"
  }
  ```
- **Response:**
  - **Success (200 OK):**
    ```json
    {
      "code": 200,
      "message": "Đã ghi nhận ảnh minh chứng lỗi ngân hàng! Đơn hàng đã chuyển sang trạng thái Đã giao — Chờ thanh toán. Khách hàng có 24h để thanh toán bổ sung.",
      "data": {
        "order_id": 99,
        "order_code": "DH-20260805-099",
        "payment_status": "PENDING_PAYMENT",
        "order_status": "DELIVERING",
        "pending_payment_proof": "https://storage.gaspro.vn/proofs/bank-error-99.jpg"
      },
      "paging": null
    }
    ```
- **Reference:** BRD v8 (CD-003), `orders`.

---

## 5. API Hủy Đơn Hàng & Tính Đền Bù 50% Cho Tài Xế (Cancel Order)

- **Domain:** Order Operations
- **Function:** Hủy đơn hàng và phân định trách nhiệm đền bù (OP-002, RC-005)
- **Description:** Cho phép Khách hàng, Operator hoặc Tài xế bấm hủy đơn. Nếu đơn bị hủy do lỗi của Khách hàng (`CUSTOMER_FAULT`) khi tài xế đang đi giao, hệ thống tự động ghi nhận đền bù 50% công giao ($0.5 \times d \times \text{D\_RATE}$) vào Ví lương tích lũy của tài xế (`driver_wallet_transactions`).
- **URL/API:** `/api/v1/orders/{id}/cancel`
- **Method:** `POST`
- **Authorization:** `Bearer Token` (`CUSTOMER`, `OPERATOR`, `DRIVER`, `ADMIN`)
- **Header:**
  ```http
  Authorization: Bearer <access_token>
  Content-Type: application/json
  ```
- **RequestParam:** `None`
- **RequestBody:**
  ```json
  {
    "cancellation_reason": "Khách đổi ý không mua nữa khi tài xế đã đi được 3.5km",
    "fault_party": "CUSTOMER_FAULT"
  }
  ```
- **Response:**
  - **Success (200 OK):**
    ```json
    {
      "code": 200,
      "message": "Đã hủy đơn hàng thành công! Tài xế được tự động đền bù 50% công giao vào Ví lương.",
      "data": {
        "order_id": 99,
        "order_code": "DH-20260805-099",
        "order_status": "CANCELLED",
        "fault_party": "CUSTOMER_FAULT",
        "is_driver_compensated": true,
        "compensation_amount": 8750,
        "cancelled_at": "2026-08-05T09:20:00Z"
      },
      "paging": null
    }
    ```
- **Note:** Cơ chế chống tính đền bù 2 lần (Double Payout Prevention): Trường `is_driver_compensated` bảo đảm chỉ đền bù duy nhất 1 lần cho 1 đơn hàng.
- **Reference:** BRD v8 (OP-002, RC-005), `orders`, `driver_wallet_transactions`.
