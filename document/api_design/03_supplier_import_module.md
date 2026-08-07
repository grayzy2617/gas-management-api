# DOCUMENTATION THIẾT KẾ API - MODULE 03: NHÀ SẢN XUẤT & QUẢN LÝ NHẬP KHO MULTI-ITEM

> **Domain:** Supplier & Procurement Management  
> **Phiên bản API:** v1  
> **Base URL:** `/api/v1/admin/suppliers`  
> **Định dạng dữ liệu:** `application/json`

---

## 1. API Lấy Danh Sách Nhà Sản Xuất & Dư Nợ Gối Đầu (List Suppliers)

- **Domain:** Supplier Management
- **Function:** Xem danh sách NSX và dư nợ gối đầu hiện tại (CD-005)
- **Description:** Trả về danh sách NSX, tổng công nợ gối đầu và hạn thanh toán gần nhất, hỗ trợ tìm kiếm và phân trang.
- **URL/API:** `/api/v1/admin/suppliers`
- **Method:** `GET`
- **Authorization:** `Bearer Token` (`ADMIN`, `OPERATOR`)
- **Header:**
  ```http
  Authorization: Bearer <access_token>
  ```
- **RequestParam:**
  - `query` (string, optional): Từ khóa tìm kiếm tên/mã NSX.
  - `status` (string, optional, default: 'ACTIVE'): Trạng thái (`ACTIVE`, `INACTIVE`).
  - `page` (integer, optional, default: 1): Trang hiện tại.
  - `limit` (integer, optional, default: 10): Số NSX/trang.
- **RequestBody:** `None`
- **Response:**
  - **Success (200 OK):**
    ```json
    {
      "code": 200,
      "message": "Lấy danh sách nhà sản xuất thành công",
      "data": [
        {
          "supplier_id": 1,
          "code": "PETRO",
          "name": "Công ty Gas Petrolimex Sài Gòn",
          "phone": "02838221100",
          "current_debt": 45000000,
          "nearest_due_date": "2026-08-10",
          "status": "ACTIVE"
        }
      ],
      "paging": {
        "page": 1,
        "limit": 10,
        "total_items": 4,
        "total_pages": 1
      }
    }
    ```
- **Reference:** BRD v8 (CD-005), `suppliers`.

---

## 2. API Lập Phiếu Nhập Kho Multi-Item & Công Nợ Gối Đầu NSX (Create Import Receipt)

- **Domain:** Supplier & Inventory
- **Function:** Lập phiếu nhập kho gas nhiều mặt hàng từ NSX (CD-005)
- **Description:** Cho phép Admin nhập hàng từ NSX gồm nhiều loại bình gas (Dynamic Multi-item table). Tự động tính tổng tiền phiếu nhập, số tiền trả ngay, nợ gối đầu (+30 ngày) và cập nhật số lượng tồn kho gas đầy (`stock_quantity`).
- **URL/API:** `/api/v1/admin/import-receipts`
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
    "supplier_id": 1,
    "import_date": "2026-08-05",
    "invoice_code": "HD-PETRO-202608",
    "contract_code": "HD-GOIDAU-001",
    "paid_amount": 10000000,
    "note": "Nhập kho đợt 1 tháng 8/2026 từ Petrolimex",
    "items": [
      {
        "product_id": 1,
        "quantity": 100,
        "unit_price": 320000
      },
      {
        "product_id": 2,
        "quantity": 20,
        "unit_price": 950000
      }
    ]
  }
  ```
- **Response:**
  - **Success (201 Created):**
    ```json
    {
      "code": 201,
      "message": "Lập phiếu nhập kho thành công! Đã tự động cập nhật tồn kho gas đầy và ghi nhận nợ gối đầu NSX.",
      "data": {
        "import_receipt_id": 45,
        "receipt_code": "PN-20260805-001",
        "supplier_name": "Công ty Gas Petrolimex Sài Gòn",
        "total_amount": 51000000,
        "paid_amount": 10000000,
        "debt_amount": 41000000,
        "due_date": "2026-09-04",
        "payment_status": "PARTIAL_PAID"
      },
      "paging": null
    }
    ```
- **Note:** Hạn nợ gối đầu `due_date` tự động tính bằng `import_date + 30 ngày`.
- **Reference:** BRD v8 (CD-005), `suppliers`, `import_receipts`, `import_receipt_details`, `products`.

---

## 3. API Lấy Danh Sách Phiếu Nhập Kho NSX (List Import Receipts)

- **Domain:** Supplier & Inventory
- **Function:** Xem danh sách lịch sử phiếu nhập kho NSX
- **Description:** Trả về danh sách phiếu nhập kho có bộ lọc theo NSX, khoảng thời gian nhập, trạng thái thanh toán và phân trang.
- **URL/API:** `/api/v1/admin/import-receipts`
- **Method:** `GET`
- **Authorization:** `Bearer Token` (`ADMIN`, `OPERATOR`)
- **Header:**
  ```http
  Authorization: Bearer <access_token>
  ```
- **RequestParam:**
  - `supplier_id` (integer, optional): Lọc theo ID NSX.
  - `payment_status` (string, optional): Trạng thái thanh toán (`UNPAID`, `PARTIAL_PAID`, `PAID`).
  - `from_date` (date, optional): Từ ngày nhập (`YYYY-MM-DD`).
  - `to_date` (date, optional): Đến ngày nhập (`YYYY-MM-DD`).
  - `page` (integer, optional, default: 1): Trang hiện tại.
  - `limit` (integer, optional, default: 10): Số phiếu/trang.
- **RequestBody:** `None`
- **Response:**
  - **Success (200 OK):**
    ```json
    {
      "code": 200,
      "message": "Lấy danh sách phiếu nhập kho thành công",
      "data": [
        {
          "import_receipt_id": 45,
          "receipt_code": "PN-20260805-001",
          "supplier_name": "Petrolimex Sài Gòn",
          "import_date": "2026-08-05",
          "total_amount": 51000000,
          "paid_amount": 10000000,
          "debt_amount": 41000000,
          "due_date": "2026-09-04",
          "payment_status": "PARTIAL_PAID"
        }
      ],
      "paging": {
        "page": 1,
        "limit": 10,
        "total_items": 15,
        "total_pages": 2
      }
    }
    ```
- **Reference:** `import_receipts`.

---

## 4. API Thanh Toán Nợ Nhà Sản Xuất (Pay Supplier Debt)

- **Domain:** Supplier Debt
- **Function:** Lập lệnh chi thanh toán nợ cho NSX (CD-005)
- **Description:** Admin thực hiện thanh toán tiền nợ gối đầu cũ cho NSX. Tự động giảm dư nợ `suppliers.current_debt` và ghi nhận lịch sử đợt trả tiền.
- **URL/API:** `/api/v1/admin/supplier-payments`
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
    "supplier_id": 1,
    "import_receipt_id": 45,
    "payment_date": "2026-08-05",
    "amount": 20000000,
    "payment_method": "BANK_TRANSFER",
    "transaction_doc": "FT26080598765432",
    "note": "Thanh toán đợt 1 nợ gối đầu phiếu PN-20260805-001"
  }
  ```
- **Response:**
  - **Success (200 OK):**
    ```json
    {
      "code": 200,
      "message": "Thanh toán công nợ NSX thành công!",
      "data": {
        "payment_code": "TT-20260805-01",
        "supplier_name": "Công ty Gas Petrolimex Sài Gòn",
        "amount_paid": 20000000,
        "remaining_debt": 21000000,
        "payment_date": "2026-08-05"
      },
      "paging": null
    }
    ```
- **Reference:** BRD v8 (CD-005), `supplier_payments`, `suppliers`.

---

## 5. API Lập Phiếu Đối Lưu Vỏ Rỗng lấy Bình Gas Đầy với NSX (Supplier Cylinder Exchange)

- **Domain:** Supplier & Inventory
- **Function:** Thực hiện đối lưu vỏ rỗng nhận bình đầy với NSX (CI-002)
- **Description:** Cho phép Thủ kho/Operator lập phiếu xuất vỏ rỗng của hãng A để nhận bình gas đầy tương ứng từ NSX. Tự động kiểm tra ràng buộc `BRAND MISMATCH` (Không cho phép xuất vỏ Totalgaz để lấy bình Petrolimex).
- **URL/API:** `/api/v1/operator/supplier-cylinder-exchanges`
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
    "supplier_id": 1,
    "empty_shell_brand_id": 1,
    "empty_shell_count": 30,
    "full_cylinder_brand_id": 1,
    "full_cylinder_count": 30
  }
  ```
- **Response:**
  - **Success (200 OK):**
    ```json
    {
      "code": 200,
      "message": "Lập phiếu đối lưu vỏ rỗng với NSX thành công! Đã trừ 30 vỏ rỗng Petrolimex và cộng 30 bình gas đầy Petrolimex vào kho.",
      "data": {
        "exchange_code": "DL-20260805-001",
        "supplier_name": "Petrolimex",
        "empty_shell_brand": "Petrolimex",
        "empty_shell_count": 30,
        "full_cylinder_brand": "Petrolimex",
        "full_cylinder_count": 30
      },
      "paging": null
    }
    ```
- **Reference:** BRD v8 (CI-002), `supplier_cylinder_exchanges`, `products`, `brands`.
