# DOCUMENTATION THIẾT KẾ API - MODULE 02: DANH MỤC SẢN PHẨM & QUẢN LÝ GIÁ

> **Domain:** Product Catalog & Pricing  
> **Phiên bản API:** v1  
> **Base URL:** `/api/v1/products`  
> **Định dạng dữ liệu:** `application/json`

---

## 1. API Lấy Danh Sách Sản Phẩm (Search & Filter Product Catalog)

- **Domain:** Product Catalog
- **Function:** Tra cứu danh mục sản phẩm Shopee-like (CO-001)
- **Description:** Trả về danh sách sản phẩm hỗ trợ tìm kiếm từ khóa (Full-text Search), lọc theo Loại sản phẩm (Bình gas/Bếp gas/Phụ kiện), lọc theo Hãng sản xuất, và phân trang.
- **URL/API:** `/api/v1/products`
- **Method:** `GET`
- **Authorization:** `Public` (Khách xem không cần Token)
- **Header:** `None`
- **RequestParam:**
  - `query` (string, optional): Từ khóa tìm kiếm (Ví dụ: `PG 12kg`, `Bếp Rinnai`).
  - `category_id` (integer, optional): Lọc theo ID Loại sản phẩm.
  - `brand_id` (integer, optional): Lọc theo ID Hãng sản xuất.
  - `status` (string, optional, default: 'ACTIVE'): Lọc trạng thái (`ACTIVE`, `INACTIVE`).
  - `page` (integer, optional, default: 1): Trang hiện tại.
  - `limit` (integer, optional, default: 10): Số sản phẩm/trang.
- **RequestBody:** `None`
- **Response:**
  - **Success (200 OK):**
    ```json
    {
      "code": 200,
      "message": "Lấy danh sách sản phẩm thành công",
      "data": [
        {
          "product_id": 1,
          "sku": "PG-12KG-001",
          "name": "Bình gas Petrolimex 12kg",
          "category": { "id": 1, "name": "Bình gas" },
          "brand": { "id": 1, "name": "Petrolimex" },
          "specifications": "12 kg",
          "current_price": 420000,
          "default_deposit_fee": 500000,
          "stock_quantity": 150,
          "empty_shell_stock": 45,
          "safety_threshold": 10,
          "image_url": "/images/products/pg-12kg.jpg",
          "status": "ACTIVE"
        }
      ],
      "paging": {
        "page": 1,
        "limit": 10,
        "total_items": 24,
        "total_pages": 3
      }
    }
    ```
- **Note:** Nếu tìm kiếm từ khóa không có kết quả chính xác, API tự động trả về danh sách sản phẩm liên quan (Full-text fallback).
- **Reference:** BRD v8 (CO-001), `products`, `categories`, `brands`.

---

## 2. API Chi Tiết Sản Phẩm (Get Product Detail)

- **Domain:** Product Catalog
- **Function:** Xem chi tiết 1 sản phẩm
- **Description:** Trả về thông tin chi tiết của sản phẩm theo ID bao gồm mô tả kỹ thuật, tồn kho gas đầy và phí cọc vỏ.
- **URL/API:** `/api/v1/products/{id}`
- **Method:** `GET`
- **Authorization:** `Public`
- **Header:** `None`
- **RequestParam:** `None`
- **RequestBody:** `None`
- **Response:**
  - **Success (200 OK):**
    ```json
    {
      "code": 200,
      "message": "Lấy thông tin chi tiết sản phẩm thành công",
      "data": {
        "product_id": 1,
        "sku": "PG-12KG-001",
        "name": "Bình gas Petrolimex 12kg",
        "category_name": "Bình gas",
        "brand_name": "Petrolimex",
        "current_price": 420000,
        "default_deposit_fee": 500000,
        "stock_quantity": 150,
        "description": "Bình gas Petrolimex 12kg van ngang chính hãng, an toàn cháy nổ 100%.",
        "status": "ACTIVE"
      },
      "paging": null
    }
    ```
  - **Error - Not Found (404 Not Found):**
    ```json
    {
      "code": 404,
      "message": "Không tìm thấy sản phẩm với mã ID [999].",
      "data": null
    }
    ```
- **Reference:** `products`.

---

## 3. API Tạo Mới Sản Phẩm (Admin Create Product - NEW)

- **Domain:** Product Catalog / Admin
- **Function:** Admin khởi tạo sản phẩm mới vào danh mục
- **Description:** Cho phép Admin thêm mới 1 mặt hàng (Bình gas, Bếp gas, Phụ kiện) vào hệ thống.
- **URL/API:** `/api/v1/admin/products`
- **Method:** `POST`
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
    "sku": "PG-12KG-003",
    "name": "Bình gas PV Gas 12kg",
    "category_id": 1,
    "brand_id": 2,
    "specifications": "12 kg",
    "current_price": 410000,
    "default_deposit_fee": 500000,
    "stock_quantity": 100,
    "safety_threshold": 10,
    "description": "Bình gas PV Gas chính hãng 12kg",
    "image_url": "https://storage.gaspro.vn/products/pvgas-12kg.jpg"
  }
  ```
- **Response:**
  - **Success (201 Created):**
    ```json
    {
      "code": 201,
      "message": "Tạo mới sản phẩm thành công!",
      "data": {
        "product_id": 25,
        "sku": "PG-12KG-003",
        "name": "Bình gas PV Gas 12kg",
        "current_price": 410000,
        "status": "ACTIVE"
      },
      "paging": null
    }
    ```
  - **Error - Duplicate SKU (409 Conflict):**
    ```json
    {
      "code": 409,
      "message": "Mã SKU [PG-12KG-003] đã tồn tại trên hệ thống.",
      "data": null
    }
    ```
- **Reference:** UI `admin_products.html`, `products`.

---

## 4. API Sửa Thông Tin Sản Phẩm (Admin Update Full Product Info - NEW)

- **Domain:** Product Catalog / Admin
- **Function:** Admin cập nhật toàn bộ thông tin sản phẩm
- **Description:** Cho phép Admin sửa Tên, Loại, Hãng, Mô tả, Ảnh đại diện và Ngưỡng an toàn của sản phẩm.
- **URL/API:** `/api/v1/admin/products/{id}`
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
    "name": "Bình gas Petrolimex 12kg (Mẫu mới 2026)",
    "category_id": 1,
    "brand_id": 1,
    "specifications": "12 kg",
    "safety_threshold": 15,
    "description": "Bình gas Petrolimex 12kg van ngang mẫu mới chống giả",
    "image_url": "https://storage.gaspro.vn/products/pg-12kg-v2.jpg"
  }
  ```
- **Response:**
  - **Success (200 OK):**
    ```json
    {
      "code": 200,
      "message": "Cập nhật thông tin sản phẩm thành công!",
      "data": {
        "product_id": 1,
        "sku": "PG-12KG-001",
        "name": "Bình gas Petrolimex 12kg (Mẫu mới 2026)",
        "updated_at": "2026-08-06T14:00:00Z"
      },
      "paging": null
    }
    ```
- **Reference:** UI `admin_products.html`, `products`.

---

## 5. API Ngừng Kinh Doanh / Xóa Mềm Sản Phẩm (Admin Deactivate Product - NEW)

- **Domain:** Product Catalog / Admin
- **Function:** Admin ngừng kinh doanh hoặc xóa mềm sản phẩm
- **Description:** Chuyển trạng thái sản phẩm sang `INACTIVE` để ẩn khỏi App Khách hàng mà vẫn bảo toàn dữ liệu lịch sử đơn hàng cũ.
- **URL/API:** `/api/v1/admin/products/{id}`
- **Method:** `DELETE`
- **Authorization:** `Bearer Token` (`ADMIN`)
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
      "message": "Đã chuyển sản phẩm sang trạng thái NGỪNG KINH DOANH (INACTIVE) thành công!",
      "data": {
        "product_id": 1,
        "status": "INACTIVE"
      },
      "paging": null
    }
    ```
- **Reference:** UI `admin_products.html`, `products`.

---

## 6. API Điều Chỉnh Giá Bán Niêm Yết (Admin Change Product Price)

- **Domain:** Product Catalog / Admin
- **Function:** Cập nhật giá niêm yết và lưu lịch sử (CO-004)
- **Description:** Cho phép Admin điều chỉnh giá bán niêm yết mới của sản phẩm. Hệ thống kiểm tra giá `> 0`, cập nhật `products.current_price` và ghi log vào `product_price_histories`.
- **URL/API:** `/api/v1/admin/products/{id}/price`
- **Method:** `PATCH`
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
    "new_price": 435000,
    "new_deposit_fee": 500000,
    "note": "Điều chỉnh tăng giá do biến động giá thế giới tháng 8/2026"
  }
  ```
- **Response:**
  - **Success (200 OK):**
    ```json
    {
      "code": 200,
      "message": "Cập nhật giá bán thành công! Đơn hàng cũ giữ nguyên đơn giá tại thời điểm đặt.",
      "data": {
        "product_id": 1,
        "sku": "PG-12KG-001",
        "old_price": 420000,
        "new_price": 435000,
        "effective_date": "2026-08-05T09:00:00Z"
      },
      "paging": null
    }
    ```
- **Reference:** BRD v8 (CO-004), `products`, `product_price_histories`.

---

## 7. API Lấy Lịch Sử Biến Động Giá Sản Phẩm (Get Price History)

- **Domain:** Product Catalog / Admin
- **Function:** Xem lịch sử thay đổi giá niêm yết sản phẩm
- **Description:** Trả về danh sách tất cả các đợt đổi giá của 1 sản phẩm có hỗ trợ lọc thời gian và phân trang.
- **URL/API:** `/api/v1/admin/products/{id}/price-histories`
- **Method:** `GET`
- **Authorization:** `Bearer Token` (`ADMIN`, `OPERATOR`)
- **Header:**
  ```http
  Authorization: Bearer <access_token>
  ```
- **RequestParam:**
  - `from_date` (date, optional): Từ ngày (`YYYY-MM-DD`).
  - `to_date` (date, optional): Đến ngày (`YYYY-MM-DD`).
  - `page` (integer, optional, default: 1): Trang hiện tại.
  - `limit` (integer, optional, default: 10): Số bản ghi/trang.
- **RequestBody:** `None`
- **Response:**
  - **Success (200 OK):**
    ```json
    {
      "code": 200,
      "message": "Lấy lịch sử biến động giá thành công",
      "data": [
        {
          "history_id": 10,
          "old_price": 420000,
          "new_price": 435000,
          "effective_date": "2026-08-05T09:00:00Z",
          "changed_by_name": "Lê Quản Lý (Admin)",
          "note": "Điều chỉnh tăng giá do biến động giá thế giới tháng 8/2026"
        }
      ],
      "paging": {
        "page": 1,
        "limit": 10,
        "total_items": 5,
        "total_pages": 1
      }
    }
    ```
- **Reference:** `product_price_histories`, `users`.
