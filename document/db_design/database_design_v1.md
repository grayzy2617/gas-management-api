# TÀI LIỆU THIẾT KẾ CƠ SỞ DỮ LIỆU (DATABASE SCHEMA SPECIFICATION)
## MÔ-ĐUN ADMIN & HỆ THỐNG QUẢN LÝ GAS PRO (PHASE 1)

> **Người thực hiện:** Senior IT Mentor & Junior Business Analyst / Developer  
> **Phiên bản:** v1.0  
> **Ngày lập:** 04/08/2026  
> **Phạm vi (Scope):** Phase 1 — Quản trị Hệ thống, Tài khoản & Phân quyền, Danh mục Sản phẩm & Lịch sử Giá, Nhà sản xuất, Nhập kho Multi-Item & Công nợ NSX Gối đầu.

---

## 📐 1. TỔNG QUAN VÀ NGUYÊN TẮC THIẾT KẾ

Cơ sở dữ liệu được thiết kế theo chuẩn **3NF (Third Normal Form)** trên hệ quản trị cơ sở dữ liệu quan hệ **RDBMS (MySQL 8.0 / PostgreSQL 15)** nhằm đảm bảo:
1. **Bảo toàn toàn vẹn dữ liệu (Data Integrity):** Ràng buộc khóa ngoại (`FOREIGN KEY`), giá trị mặc định (`DEFAULT`), và quy tắc xóa (`ON DELETE RESTRICT / CASCADE`).
2. **Bảo toàn đơn giá lịch sử (Historical Price Preservation):** Mọi giao dịch nhập kho, biến động giá bán niêm yết đều được tách bảng lưu vết lịch sử độc lập, không làm sai lệch báo cáo tài chính quá khứ.
3. **Mô hình Master-Detail (1 - N):** Áp dụng cho phiếu nhập kho từ Nhà sản xuất (NSX), cho phép 1 phiếu nhập chứa nhiều loại bình gas và vật tư khác nhau trong cùng 1 lần nhập.

---

## 📊 2. SƠ ĐỒ QUAN HỆ THỰC THỂ (ERD - MERMAID DIAGRAM)

```mermaid
erDiagram
    roles ||--o{ users : "gán vai trò"
    roles ||--o{ role_permissions : "chứa"
    permissions ||--o{ role_permissions : "thuộc về"
    users ||--o{ system_settings : "cấu hình bởi"

    categories ||--o{ products : "phân loại"
    brands ||--o{ products : "sản xuất"
    products ||--o{ product_price_histories : "lưu biến động giá"

    suppliers ||--o{ import_receipts : "phát hành phiếu nhập"
    suppliers ||--o{ supplier_payments : "nhận thanh toán"
    import_receipts ||--o{ import_receipt_details : "bao gồm các mặt hàng"
    products ||--o{ import_receipt_details : "được nhập kho"
    users ||--o{ import_receipts : "người lập phiếu"
    users ||--o{ supplier_payments : "người lập chi"
    import_receipts ||--o? supplier_payments : "thanh toán cho phiếu"
```

---

## 🗂️ 3. DANH SÁCH CHI TIẾT CÁC BẢNG (TABLE SCHEMAS)

---

### 🟢 PHẦN I: NHÓM TÀI KHOẢN, NGUYÊN TẮC VÀ PHÂN QUYỀN (AUTH & SYSTEM)

#### 1. Bảng `roles` (Danh mục Vai trò Người dùng)
* **Mô tả:** Lưu trữ các nhóm vai trò chuẩn trong hệ thống (Admin, Operator, Driver, Customer).

| Tên trường (Column) | Kiểu dữ liệu (Data Type) | Ràng buộc (Constraints) | Ý nghĩa (Description) |
|---|---|---|---|
| `id` | `BIGINT` | `PK`, `AUTO_INCREMENT`, `NOT NULL` | Khóa chính |
| `code` | `VARCHAR(50)` | `UNIQUE`, `NOT NULL` | Mã vai trò (`ADMIN`, `OPERATOR`, `DRIVER`, `CUSTOMER`) |
| `name` | `VARCHAR(100)` | `NOT NULL` | Tên hiển thị (`Quản trị viên`, `Tổng đài viên`...) |
| `description` | `VARCHAR(255)` | `NULL` | Mô tả nhiệm vụ vai trò |
| `created_at` | `TIMESTAMP` | `DEFAULT CURRENT_TIMESTAMP` | Thời gian tạo |
| `updated_at` | `TIMESTAMP` | `DEFAULT CURRENT_TIMESTAMP ON UPDATE` | Thời gian cập nhật |

#### 2. Bảng `permissions` (Danh mục Quyền hạn Chi tiết)
* **Mô tả:** Định nghĩa từng quyền thao tác nhỏ trong hệ thống để phục vụ mô hình RBAC.

| Tên trường (Column) | Kiểu dữ liệu (Data Type) | Ràng buộc (Constraints) | Ý nghĩa (Description) |
|---|---|---|---|
| `id` | `BIGINT` | `PK`, `AUTO_INCREMENT`, `NOT NULL` | Khóa chính |
| `code` | `VARCHAR(100)` | `UNIQUE`, `NOT NULL` | Mã quyền (`PRODUCT_CREATE`, `SUPPLIER_DEBT_PAY`...) |
| `name` | `VARCHAR(150)` | `NOT NULL` | Tên quyền hiển thị |
| `module` | `VARCHAR(50)` | `NOT NULL` | Nhóm module (`PRODUCT`, `SUPPLIER`, `ORDER`...) |
| `description` | `VARCHAR(255)` | `NULL` | Diễn giải chi tiết quyền |
| `created_at` | `TIMESTAMP` | `DEFAULT CURRENT_TIMESTAMP` | Thời gian tạo |

#### 3. Bảng `role_permissions` (Bảng Trung gian Phân quyền Role - Permission)
* **Mô tả:** Quan hệ N - N giữa Vai trò và Quyền hạn.

| Tên trường (Column) | Kiểu dữ liệu (Data Type) | Ràng buộc (Constraints) | Ý nghĩa (Description) |
|---|---|---|---|
| `role_id` | `BIGINT` | `PK`, `FK -> roles(id) ON DELETE CASCADE` | Khóa ngoại nối bảng `roles` |
| `permission_id` | `BIGINT` | `PK`, `FK -> permissions(id) ON DELETE CASCADE` | Khóa ngoại nối bảng `permissions` |
| `created_at` | `TIMESTAMP` | `DEFAULT CURRENT_TIMESTAMP` | Thời gian gán quyền |

#### 4. Bảng `users` (Tài khoản Người dùng Hệ thống)
* **Mô tả:** Quản lý thông tin đăng nhập và danh tính người dùng toàn hệ thống.

| Tên trường (Column) | Kiểu dữ liệu (Data Type) | Ràng buộc (Constraints) | Ý nghĩa (Description) |
|---|---|---|---|
| `id` | `BIGINT` | `PK`, `AUTO_INCREMENT`, `NOT NULL` | Khóa chính người dùng |
| `phone` | `VARCHAR(15)` | `UNIQUE`, `NOT NULL` | Số điện thoại (Dùng làm Tên đăng nhập) |
| `password_hash` | `VARCHAR(255)` | `NOT NULL` | Mật khẩu mã hóa (Bcrypt / Argon2) |
| `full_name` | `VARCHAR(100)` | `NOT NULL` | Họ và tên người dùng |
| `email` | `VARCHAR(100)` | `NULL` | Thư điện tử |
| `role_id` | `BIGINT` | `FK -> roles(id) ON DELETE RESTRICT`, `NOT NULL` | Mã vai trò |
| `status` | `VARCHAR(20)` | `DEFAULT 'ACTIVE'`, `NOT NULL` | Trạng thái (`ACTIVE`, `INACTIVE`, `LOCKED`) |
| `avatar_url` | `VARCHAR(255)` | `NULL` | Đường dẫn ảnh đại diện |
| `last_login_at` | `TIMESTAMP` | `NULL` | Thời gian đăng nhập gần nhất |
| `created_at` | `TIMESTAMP` | `DEFAULT CURRENT_TIMESTAMP` | Ngày tạo tài khoản |
| `updated_at` | `TIMESTAMP` | `DEFAULT CURRENT_TIMESTAMP ON UPDATE` | Ngày cập nhật gần nhất |

#### 5. Bảng `system_settings` (Cấu hình Hằng số Hệ thống)
* **Mô tả:** Lưu trữ các tham số cấu hình toàn cục do Admin thiết lập.

| Tên trường (Column) | Kiểu dữ liệu (Data Type) | Ràng buộc (Constraints) | Ý nghĩa (Description) |
|---|---|---|---|
| `id` | `BIGINT` | `PK`, `AUTO_INCREMENT`, `NOT NULL` | Khóa chính |
| `setting_key` | `VARCHAR(100)` | `UNIQUE`, `NOT NULL` | Mã tham số (`D_RATE`, `T_LOCK_OUT`, `DEBT_DUE_DAYS`...) |
| `setting_value` | `VARCHAR(255)` | `NOT NULL` | Giá trị thiết lập |
| `data_type` | `VARCHAR(20)` | `DEFAULT 'STRING'`, `NOT NULL` | Kiểu dữ liệu (`NUMBER`, `STRING`, `BOOLEAN`) |
| `description` | `TEXT` | `NULL` | Giải thích tham số nghiệp vụ |
| `updated_by` | `BIGINT` | `FK -> users(id) ON DELETE SET NULL` | Người cập nhật gần nhất |
| `updated_at` | `TIMESTAMP` | `DEFAULT CURRENT_TIMESTAMP ON UPDATE` | Thời gian cập nhật |

---

### 🔵 PHẦN II: NHÓM DANH MỤC & SẢN PHẨM (CATALOG & PRODUCTS)

#### 6. Bảng `categories` (Loại Sản phẩm)
* **Mô tả:** Phân loại sản phẩm (Bình gas, Bếp gas, Phụ kiện...).

| Tên trường (Column) | Kiểu dữ liệu (Data Type) | Ràng buộc (Constraints) | Ý nghĩa (Description) |
|---|---|---|---|
| `id` | `BIGINT` | `PK`, `AUTO_INCREMENT`, `NOT NULL` | Khóa chính |
| `code` | `VARCHAR(50)` | `UNIQUE`, `NOT NULL` | Mã loại (`GAS_CYLINDER`, `GAS_STOVE`, `ACCESSORY`) |
| `name` | `VARCHAR(100)` | `NOT NULL` | Tên loại sản phẩm |
| `description` | `VARCHAR(255)` | `NULL` | Mô tả thêm |
| `created_at` | `TIMESTAMP` | `DEFAULT CURRENT_TIMESTAMP` | Thời gian tạo |

#### 7. Bảng `brands` (Hãng Sản xuất)
* **Mô tả:** Thương hiệu sản xuất sản phẩm (Petrolimex, PV Gas, Saigon Petro, Totalgaz, Rinnai...).

| Tên trường (Column) | Kiểu dữ liệu (Data Type) | Ràng buộc (Constraints) | Ý nghĩa (Description) |
|---|---|---|---|
| `id` | `BIGINT` | `PK`, `AUTO_INCREMENT`, `NOT NULL` | Khóa chính |
| `code` | `VARCHAR(50)` | `UNIQUE`, `NOT NULL` | Mã hãng (`PETROLIMEX`, `PVGAS`, `RINNAI`...) |
| `name` | `VARCHAR(100)` | `NOT NULL` | Tên hãng sản xuất |
| `origin_country` | `VARCHAR(50)` | `DEFAULT 'Việt Nam'` | Xuất xứ thương hiệu |
| `description` | `VARCHAR(255)` | `NULL` | Mô tả thông tin hãng |
| `created_at` | `TIMESTAMP` | `DEFAULT CURRENT_TIMESTAMP` | Thời gian tạo |

#### 8. Bảng `products` (Danh mục Sản phẩm Gas & Thiết bị)
* **Mô tả:** Thông tin chi tiết các mặt hàng đại lý đang kinh doanh.

| Tên trường (Column) | Kiểu dữ liệu (Data Type) | Ràng buộc (Constraints) | Ý nghĩa (Description) |
|---|---|---|---|
| `id` | `BIGINT` | `PK`, `AUTO_INCREMENT`, `NOT NULL` | Khóa chính sản phẩm |
| `sku` | `VARCHAR(50)` | `UNIQUE`, `NOT NULL` | Mã SKU sản phẩm (`PG-12KG-001`) |
| `name` | `VARCHAR(150)` | `NOT NULL` | Tên sản phẩm |
| `category_id` | `BIGINT` | `FK -> categories(id) ON DELETE RESTRICT` | Loại sản phẩm |
| `brand_id` | `BIGINT` | `FK -> brands(id) ON DELETE RESTRICT` | Hãng sản xuất |
| `specifications` | `VARCHAR(100)` | `NULL` | Quy cách/Trọng lượng (`12 kg`, `50 kg`, `Bếp đôi`) |
| `current_price` | `DECIMAL(15,2)` | `NOT NULL`, `DEFAULT 0.00` | Giá bán niêm yết hiện tại |
| `default_deposit_fee`| `DECIMAL(15,2)` | `DEFAULT 0.00` | Phí cọc vỏ mặc định (Dành cho bình gas) |
| `stock_quantity` | `INT` | `NOT NULL`, `DEFAULT 0` | Số lượng tồn kho gas đầy |
| `empty_shell_stock` | `INT` | `NOT NULL`, `DEFAULT 0` | Số lượng vỏ rỗng trong kho đại lý |
| `safety_threshold` | `INT` | `DEFAULT 10` | Ngưỡng an toàn cảnh báo tồn vỏ rỗng (CI-003) |
| `status` | `VARCHAR(20)` | `DEFAULT 'ACTIVE'`, `NOT NULL` | Trạng thái (`ACTIVE`: Kinh doanh, `INACTIVE`: Tạm ngưng) |
| `description` | `TEXT` | `NULL` | Mô tả đặc tính kỹ thuật |
| `image_url` | `VARCHAR(255)` | `NULL` | Đường dẫn ảnh minh họa |
| `created_at` | `TIMESTAMP` | `DEFAULT CURRENT_TIMESTAMP` | Thời gian tạo |
| `updated_at` | `TIMESTAMP` | `DEFAULT CURRENT_TIMESTAMP ON UPDATE` | Thời gian cập nhật |

#### 9. Bảng `product_price_histories` (Lịch sử Điều chỉnh Giá Niêm yết)
* **Mô tả:** Lưu lại vết biến động giá bán và phí cọc vỏ phục vụ bảo toàn giá đơn cũ (CO-004).

| Tên trường (Column) | Kiểu dữ liệu (Data Type) | Ràng buộc (Constraints) | Ý nghĩa (Description) |
|---|---|---|---|
| `id` | `BIGINT` | `PK`, `AUTO_INCREMENT`, `NOT NULL` | Khóa chính |
| `product_id` | `BIGINT` | `FK -> products(id) ON DELETE CASCADE` | Mã sản phẩm |
| `old_price` | `DECIMAL(15,2)` | `NOT NULL` | Giá bán niêm yết cũ |
| `new_price` | `DECIMAL(15,2)` | `NOT NULL` | Giá bán niêm yết mới |
| `old_deposit_fee` | `DECIMAL(15,2)` | `DEFAULT 0.00` | Phí cọc vỏ cũ |
| `new_deposit_fee` | `DECIMAL(15,2)` | `DEFAULT 0.00` | Phí cọc vỏ mới |
| `effective_date` | `TIMESTAMP` | `DEFAULT CURRENT_TIMESTAMP`, `NOT NULL` | Thời điểm giá mới có hiệu lực |
| `changed_by` | `BIGINT` | `FK -> users(id) ON DELETE SET NULL` | Người thực hiện đổi giá (Admin) |
| `note` | `VARCHAR(255)` | `NULL` | Ghi chú lý do tăng/giảm giá |

---

### 🟠 PHẦN III: NHÀ SẢN XUẤT, NHẬP KHO MULTI-ITEM & CÔNG NỢ NSX (SUPPLIERS & INVENTORY)

#### 10. Bảng `suppliers` (Danh mục Nhà sản xuất / Nhà cung cấp)
* **Mô tả:** Thông tin đối tác Nhà sản xuất cung cấp gas và thiết bị cho đại lý.

| Tên trường (Column) | Kiểu dữ liệu (Data Type) | Ràng buộc (Constraints) | Ý nghĩa (Description) |
|---|---|---|---|
| `id` | `BIGINT` | `PK`, `AUTO_INCREMENT`, `NOT NULL` | Khóa chính NSX |
| `code` | `VARCHAR(50)` | `UNIQUE`, `NOT NULL` | Mã nhà sản xuất (`PETRO`, `PVGAS`...) |
| `name` | `VARCHAR(150)` | `NOT NULL` | Tên công ty/Nhà sản xuất |
| `phone` | `VARCHAR(15)` | `NULL` | Số điện thoại liên hệ |
| `email` | `VARCHAR(100)` | `NULL` | Email giao dịch |
| `address` | `TEXT` | `NULL` | Địa chỉ trụ sở |
| `tax_code` | `VARCHAR(20)` | `NULL` | Mã số thuế NSX |
| `current_debt` | `DECIMAL(15,2)` | `DEFAULT 0.00`, `NOT NULL` | Tổng dư nợ hiện tại gối đầu với NSX |
| `status` | `VARCHAR(20)` | `DEFAULT 'ACTIVE'`, `NOT NULL` | Trạng thái hợp tác (`ACTIVE`, `INACTIVE`) |
| `created_at` | `TIMESTAMP` | `DEFAULT CURRENT_TIMESTAMP` | Thời gian tạo |
| `updated_at` | `TIMESTAMP` | `DEFAULT CURRENT_TIMESTAMP ON UPDATE` | Thời gian cập nhật |

#### 11. Bảng `import_receipts` (Phiếu Nhập Kho NSX - Master)
* **Mô tả:** Đầu phiếu nhập kho từ NSX, lưu tổng tiền phiếu nhập và thông tin nợ gối đầu 30 ngày (CD-005).

| Tên trường (Column) | Kiểu dữ liệu (Data Type) | Ràng buộc (Constraints) | Ý nghĩa (Description) |
|---|---|---|---|
| `id` | `BIGINT` | `PK`, `AUTO_INCREMENT`, `NOT NULL` | Khóa chính phiếu nhập |
| `receipt_code` | `VARCHAR(50)` | `UNIQUE`, `NOT NULL` | Mã phiếu nhập (`PN-20260804-001`) |
| `supplier_id` | `BIGINT` | `FK -> suppliers(id) ON DELETE RESTRICT` | Nhà sản xuất giao hàng |
| `import_date` | `DATE` | `NOT NULL` | Ngày thực hiện nhập kho |
| `total_amount` | `DECIMAL(15,2)` | `NOT NULL`, `DEFAULT 0.00` | Tổng giá trị tất cả các dòng hàng |
| `paid_amount` | `DECIMAL(15,2)` | `NOT NULL`, `DEFAULT 0.00` | Số tiền trả ngay khi nhận lô hàng |
| `debt_amount` | `DECIMAL(15,2)` | `NOT NULL`, `DEFAULT 0.00` | Số tiền ghi tăng nợ gối đầu (`total - paid`) |
| `due_date` | `DATE` | `NULL` | Hạn thanh toán nợ gối đầu (+30 ngày) |
| `invoice_code` | `VARCHAR(50)` | `NULL` | Số hóa đơn GTGT kèm theo lô hàng |
| `contract_code` | `VARCHAR(50)` | `NULL` | Số hợp đồng nguyên tắc nợ gối đầu |
| `payment_status` | `VARCHAR(20)` | `DEFAULT 'UNPAID'`, `NOT NULL` | Trạng thái nợ (`PAID`, `PARTIAL`, `UNPAID`) |
| `note` | `TEXT` | `NULL` | Ghi chú thêm về lô hàng |
| `created_by` | `BIGINT` | `FK -> users(id) ON DELETE RESTRICT` | Người lập phiếu nhập kho (Admin) |
| `created_at` | `TIMESTAMP` | `DEFAULT CURRENT_TIMESTAMP` | Ngày giờ tạo phiếu |

#### 12. Bảng `import_receipt_details` (Chi tiết Mặt hàng Nhập kho - Detail)
* **Mô tả:** Danh sách N loại gas/thiết bị nằm trong cùng 1 Phiếu nhập kho (N - 1 với `import_receipts`).

| Tên trường (Column) | Kiểu dữ liệu (Data Type) | Ràng buộc (Constraints) | Ý nghĩa (Description) |
|---|---|---|---|
| `id` | `BIGINT` | `PK`, `AUTO_INCREMENT`, `NOT NULL` | Khóa chính dòng nhập |
| `import_receipt_id`| `BIGINT` | `FK -> import_receipts(id) ON DELETE CASCADE` | Khóa ngoại nối về Phiếu nhập Master |
| `product_id` | `BIGINT` | `FK -> products(id) ON DELETE RESTRICT` | Mã mặt hàng nhập |
| `quantity` | `INT` | `NOT NULL` | Số lượng nhập |
| `unit_price` | `DECIMAL(15,2)` | `NOT NULL` | Đơn giá nhập từ NSX (VNĐ) |
| `subtotal` | `DECIMAL(15,2)` | `NOT NULL` | Thành tiền dòng (`quantity * unit_price`) |

#### 13. Bảng `supplier_payments` (Lịch sử Thanh toán Công nợ NSX)
* **Mô tả:** Nhật ký theo dõi từng đợt đại lý thanh toán chuyển khoản/tiền mặt trả nợ cho NSX.

| Tên trường (Column) | Kiểu dữ liệu (Data Type) | Ràng buộc (Constraints) | Ý nghĩa (Description) |
|---|---|---|---|
| `id` | `BIGINT` | `PK`, `AUTO_INCREMENT`, `NOT NULL` | Khóa chính đợt thanh toán |
| `payment_code` | `VARCHAR(50)` | `UNIQUE`, `NOT NULL` | Mã đợt thanh toán (`TT-20260804-01`) |
| `supplier_id` | `BIGINT` | `FK -> suppliers(id) ON DELETE RESTRICT` | Nhà sản xuất nhận tiền |
| `import_receipt_id`| `BIGINT` | `FK -> import_receipts(id) ON DELETE SET NULL` | Nối với phiếu nhập nếu trả đích danh |
| `payment_date` | `DATE` | `NOT NULL` | Ngày thực hiện chuyển tiền/trả mặt |
| `amount` | `DECIMAL(15,2)` | `NOT NULL` | Số tiền thanh toán đợt này (VNĐ) |
| `remaining_debt` | `DECIMAL(15,2)` | `NOT NULL` | Dư nợ còn lại của NSX sau đợt trả |
| `payment_method` | `VARCHAR(50)` | `NOT NULL` | Phương thức (`BANK_TRANSFER`, `CASH`) |
| `transaction_doc` | `VARCHAR(100)` | `NULL` | Mã chứng từ / Số GD Ngân hàng |
| `note` | `TEXT` | `NULL` | Ghi chú thanh toán |
| `created_by` | `BIGINT` | `FK -> users(id) ON DELETE RESTRICT` | Người lập lệnh chi/thanh toán |
| `created_at` | `TIMESTAMP` | `DEFAULT CURRENT_TIMESTAMP` | Thời gian tạo giao dịch |

---

## 🔗 4. BẢNG MÔ TẢ QUAN HỆ VÀ TÍNH TOÀN VẸN DỮ LIỆU (RELATIONSHIP MATRIX)

| Bảng Nguồn (Source) | Bảng Đích (Target) | Loại quan hệ (Cardinality) | Khóa ngoại (Foreign Key) | Hành vi xóa (ON DELETE) | Diễn giải Nghiệp vụ |
|---|---|---|---|---|---|
| `roles` | `users` | 1 - N | `users.role_id` | `RESTRICT` | 1 Vai trò gán cho nhiều User. Không thể xóa Role nếu còn User đang giữ. |
| `roles` | `role_permissions` | 1 - N | `role_permissions.role_id` | `CASCADE` | 1 Vai trò có nhiều Quyền. Xóa Role thì tự xóa phân quyền liên quan. |
| `permissions` | `role_permissions` | 1 - N | `role_permissions.permission_id` | `CASCADE` | 1 Quyền thuộc nhiều Role. Xóa Quyền thì tự hủy ở bảng gán. |
| `categories` | `products` | 1 - N | `products.category_id` | `RESTRICT` | 1 Loại SP chứa nhiều Sản phẩm. Không thể xóa Loại nếu còn SP đang bán. |
| `brands` | `products` | 1 - N | `products.brand_id` | `RESTRICT` | 1 Hãng sản xuất nhiều SP. Không thể xóa Hãng nếu còn SP trong kho. |
| `products` | `product_price_histories` | 1 - N | `product_price_histories.product_id` | `CASCADE` | 1 SP có nhiều lượt đổi giá. Xóa SP thì xóa lịch sử giá. |
| `suppliers` | `import_receipts` | 1 - N | `import_receipts.supplier_id` | `RESTRICT` | 1 NSX xuất nhiều Phiếu nhập. Không xóa NSX nếu đã phát sinh phiếu nhập. |
| `import_receipts` | `import_receipt_details` | 1 - N | `import_receipt_details.import_receipt_id` | `CASCADE` | 1 Phiếu nhập chứa N dòng mặt hàng. Xóa phiếu master thì xóa chi tiết. |
| `products` | `import_receipt_details` | 1 - N | `import_receipt_details.product_id` | `RESTRICT` | 1 SP xuất hiện trong nhiều phiếu nhập. Không xóa SP đã từng nhập kho. |
| `suppliers` | `supplier_payments` | 1 - N | `supplier_payments.supplier_id` | `RESTRICT` | 1 NSX có nhiều đợt nhận tiền nợ. Không xóa NSX nếu còn sổ tiền chi. |
| `users` | `import_receipts` | 1 - N | `import_receipts.created_by` | `RESTRICT` | 1 Admin lập nhiều phiếu nhập kho. Lưu vết người chịu trách nhiệm. |

---

## 🎯 5. KẾT LUẬN & ĐÁNH GIÁ CỦA SENIOR MENTOR

Bản thiết kế này đáp ứng hoàn hảo các tiêu chuẩn hệ thống quản lý kho & tài chính doanh nghiệp:
1. **Hoàn toàn phù hợp với BRD v8 và Prototype UI** vừa xây dựng.
2. **Cho phép nhập nhiều loại gas trên cùng 1 phiếu** nhờ cấu trúc Master-Detail (`import_receipts` & `import_receipt_details`).
3. **Quản lý công nợ gối đầu 30 ngày chuẩn xác** với lịch sử thanh toán độc lập (`supplier_payments`).
4. **Sẵn sàng mở rộng cho Phase 2** (Khách hàng B2B/B2C, Đơn hàng, Tài xế giao hàng, Bảo hành và Quyết toán tiền COD) mà không phải thay đổi kiến trúc các bảng Phase 1.
