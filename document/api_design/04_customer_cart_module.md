# DOCUMENTATION THIẾT KẾ API - MODULE 04: KHÁCH HÀNG, GIỎ HÀNG & CÔNG NỢ

> **Domain:** Customer & Cart Management  
> **Phiên bản API:** v1  
> **Base URL:** `/api/v1/customers`  
> **Định dạng dữ liệu:** `application/json`

---

## 1. API Lấy Thông Tin Hồ Sơ & Trạng Thái Đủ Điều Kiện Ghi Nợ (Get Customer Profile & Credit Eligibility)

- **Domain:** Customer Management
- **Function:** Tra cứu thông tin hồ sơ và hạn mức nợ của khách hàng hiện tại (CD-001)
- **Description:** Trả về hồ sơ khách hàng đang đăng nhập, thâm niên giao dịch (`first_order_date`), số bình gas đã mua tích lũy (`total_cylinders_purchased`), hạn mức nợ (`credit_limit`), dư nợ hiện tại (`current_debt`) và trạng thái duyệt ghi nợ tự động (`debt_status`).
- **URL/API:** `/api/v1/customers/me/profile`
- **Method:** `GET`
- **Authorization:** `Bearer Token` (`CUSTOMER`)
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
      "message": "Lấy thông tin hồ sơ khách hàng thành công",
      "data": {
        "customer_id": 45,
        "contact_name": "Nhà hàng Biển Đông (B2B)",
        "phone": "0912345678",
        "customer_type": "WHOLESALE_B2B",
        "delivery_address": "456 Lê Lợi, Quận 1, TP.HCM",
        "first_order_date": "2024-05-10",
        "total_cylinders_purchased": 45,
        "credit_limit": 10000000,
        "current_debt": 2500000,
        "debt_status": "ELIGIBLE",
        "debt_status_label": "Được phép ghi nợ (Hạn mức 10M)",
        "vat_info": {
          "tax_code": "0312345678",
          "company_name": "Công ty TNHH Nhahang Bien Dong",
          "invoice_address": "456 Lê Lợi, Q.1, TP.HCM"
        }
      },
      "paging": null
    }
    ```
- **Note:** 
  - Trạng thái `debt_status`:
    - `ELIGIBLE`: Đủ ĐK mua nợ (B2B: >1 năm & >20 bình; B2C: >1 năm & >10 bình).
    - `INELIGIBLE`: Chưa đủ thâm niên hoặc sản lượng.
    - `EXCEEDED_LIMIT`: Vượt hạn mức nợ.
    - `OVERDUE_LOCKED`: Khóa nợ do nợ quá hạn >30 ngày.
- **Reference:** BRD v8 (CD-001), `customers`, `customer_vat_infos`.

---

## 2. API Cập Nhật Thông Tin Hóa Đơn VAT Khách Sỉ B2B (Upsert B2B VAT Info)

- **Domain:** Customer Management
- **Function:** Đăng ký / Cập nhật thông tin xuất hóa đơn VAT (UM-003)
- **Description:** Cho phép khách hàng B2B khai báo hoặc sửa Mã số thuế (10 hoặc 13 số), Tên công ty và Địa chỉ thuế để phục vụ xuất hóa đơn GTGT.
- **URL/API:** `/api/v1/customers/me/vat-info`
- **Method:** `PUT`
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
    "tax_code": "0312345678",
    "company_name": "Công ty TNHH Nhà hàng Biển Đông",
    "invoice_address": "456 Lê Lợi, Phường Bến Nghé, Quận 1, TP.HCM"
  }
  ```
- **Response:**
  - **Success (200 OK):**
    ```json
    {
      "code": 200,
      "message": "Cập nhật thông tin VAT thành công!",
      "data": {
        "customer_id": 45,
        "tax_code": "0312345678",
        "company_name": "Công ty TNHH Nhà hàng Biển Đông",
        "invoice_address": "456 Lê Lợi, Phường Bến Nghé, Quận 1, TP.HCM"
      },
      "paging": null
    }
    ```
- **Reference:** BRD v8 (UM-003), `customer_vat_infos`.

---

## 3. API Xóa Thông Tin Hóa Đơn VAT (Delete B2B VAT Info - NEW)

- **Domain:** Customer Management
- **Function:** Khách hàng bấm xóa/hủy thông tin đăng ký VAT cũ
- **Description:** Xóa hồ sơ thông tin VAT công ty của khách hàng hiện tại.
- **URL/API:** `/api/v1/customers/me/vat-info`
- **Method:** `DELETE`
- **Authorization:** `Bearer Token` (`CUSTOMER`)
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
      "message": "Đã xóa thông tin đăng ký VAT thành công!",
      "data": null,
      "paging": null
    }
    ```
- **Reference:** `customer_vat_infos`.

---

## 4. API Lấy Danh Sách Sản Phẩm Trong Giỏ Hàng (Get Cart Items)

- **Domain:** Cart Management
- **Function:** Xem giỏ hàng và tính tạm tiền cọc vỏ (CO-002)
- **Description:** Trả về danh sách các sản phẩm đang có trong giỏ hàng tạm, tự động tính tổng tiền hàng, phí cọc vỏ (500,000đ/bình nếu `has_exchange_shell = false`), và tổng tiền thanh toán tạm tính.
- **URL/API:** `/api/v1/cart/items`
- **Method:** `GET`
- **Authorization:** `Bearer Token` (`CUSTOMER`)
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
      "message": "Lấy thông tin giỏ hàng thành công",
      "data": {
        "cart_id": 88,
        "items": [
          {
            "cart_item_id": 1,
            "product_id": 1,
            "product_name": "Bình gas Petrolimex 12kg",
            "quantity": 2,
            "unit_price": 420000,
            "has_exchange_shell": true,
            "unit_deposit_fee": 0,
            "subtotal": 840000
          },
          {
            "cart_item_id": 2,
            "product_id": 2,
            "product_name": "Bình gas Petrolimex 50kg",
            "quantity": 1,
            "unit_price": 1150000,
            "has_exchange_shell": false,
            "unit_deposit_fee": 500000,
            "subtotal": 1650000
          }
        ],
        "summary": {
          "total_goods_amount": 1990000,
          "total_deposit_amount": 500000,
          "grand_total": 2490000
        }
      },
      "paging": null
    }
    ```
- **Reference:** BRD v8 (CO-002), `cart_items`, `products`.

---

## 5. API Thêm Món Mới Vào Giỏ Hàng (Add Item to Cart)

- **Domain:** Cart Management
- **Function:** Khách bấm nút "Thêm vào giỏ" ở trang danh mục sản phẩm (CO-001, CO-002)
- **Description:** Thêm mới 1 sản phẩm vào giỏ hàng. Nếu sản phẩm đã tồn tại trong giỏ thì tự động cộng dồn số lượng.
- **URL/API:** `/api/v1/cart/items`
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
    "product_id": 1,
    "quantity": 1,
    "has_exchange_shell": true
  }
  ```
- **Response:**
  - **Success (201 Created):**
    ```json
    {
      "code": 201,
      "message": "Đã thêm Bình gas Petrolimex 12kg vào giỏ hàng!",
      "data": {
        "cart_item_id": 1,
        "product_id": 1,
        "quantity": 2,
        "has_exchange_shell": true,
        "subtotal": 840000
      },
      "paging": null
    }
    ```
- **Reference:** `cart_items`.

---

## 6. API Sửa Số Lượng / Tùy Chọn Vỏ Đổi Trong Giỏ Hàng (Update Cart Item - NEW)

- **Domain:** Cart Management
- **Function:** Khách bấm nút + / - số lượng hoặc tích chọn "Có vỏ đổi" (CO-002)
- **Description:** Cho phép sửa số lượng mua hoặc thay đổi cờ `has_exchange_shell` của 1 món trong giỏ hàng.
- **URL/API:** `/api/v1/cart/items/{cart_item_id}`
- **Method:** `PUT`
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
    "quantity": 3,
    "has_exchange_shell": false
  }
  ```
- **Response:**
  - **Success (200 OK):**
    ```json
    {
      "code": 200,
      "message": "Đã cập nhật giỏ hàng thành công!",
      "data": {
        "cart_item_id": 1,
        "quantity": 3,
        "has_exchange_shell": false,
        "unit_deposit_fee": 500000,
        "subtotal": 2760000
      },
      "paging": null
    }
    ```
- **Reference:** UI `customer_cart.html`, `cart_items`.

---

## 7. API Xóa Món Khỏi Giỏ Hàng (Remove Cart Item - NEW)

- **Domain:** Cart Management
- **Function:** Khách bấm nút thùng rác xóa 1 món khỏi giỏ hàng
- **Description:** Xóa bỏ 1 sản phẩm ra khỏi giỏ hàng tạm.
- **URL/API:** `/api/v1/cart/items/{cart_item_id}`
- **Method:** `DELETE`
- **Authorization:** `Bearer Token` (`CUSTOMER`)
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
      "message": "Đã xóa sản phẩm khỏi giỏ hàng thành công!",
      "data": null,
      "paging": null
    }
    ```
- **Reference:** UI `customer_cart.html`, `cart_items`.
