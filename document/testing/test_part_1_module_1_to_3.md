# 🧪 Test Plan: Part 1 (Module 1 to 3)
*Tài liệu này tổng hợp các kịch bản kiểm thử (Test Cases), bao gồm cả Happy Path và Edge Cases (Trường hợp ngoại lệ) cho toàn bộ các API từ Phase 1 đến Phase 3 để đảm bảo hệ thống vững chắc trước khi bước sang Phase 4.*

---

## 🔒 Module 1: Authentication (Xác thực & Phân quyền)
**Mục tiêu:** Đảm bảo chỉ người dùng hợp lệ mới lấy được Token và truy cập đúng quyền hạn.

| ID | API | Kịch bản (Test Case) | Dữ liệu đầu vào (Input) | Kết quả mong đợi (Expected Output) |
|---|---|---|---|---|
| AUTH-01 | `POST /login` | Đăng nhập thành công (Happy path) | SĐT đúng, Pass đúng | HTTP 200, Trả về `accessToken` |
| AUTH-02 | `POST /login` | Sai mật khẩu (Edge case) | SĐT đúng, Pass: "sai123" | HTTP 401, `BAD_CREDENTIALS` |
| AUTH-03 | `POST /login` | SĐT không tồn tại (Edge case) | SĐT: "0999999999" | HTTP 404, `USER_NOT_FOUND` |
| AUTH-04 | `POST /login` | Thiếu payload (Edge case) | Gửi `{}` | HTTP 400, `INVALID_REQUEST_BODY` |
| AUTH-05 | `GET /admin/...` | Truy cập API không có Token | Headers không có Authorization | HTTP 401 Unauthorized |
| AUTH-06 | `POST /api/v1/admin/imports` | Tài xế (Driver) thử nhập kho | Dùng token của Driver gọi API Nhập kho | HTTP 403 Forbidden |
| AUTH-07 | `POST /api/v1/admin/products` | Điều phối viên (Operator) thử đổi giá | Dùng token của Operator gọi API Sửa giá | HTTP 403 Forbidden (Vì chỉ Admin mới có quyền thao tác bảng Giá/Sản phẩm) |
| AUTH-08 | `GET /api/v1/driver/...` | Admin/Operator thử gọi API dành riêng cho Tài xế | Dùng token Admin gọi API của Driver | HTTP 403 Forbidden (Ngoại trừ có cấu hình cho phép) |
| AUTH-09 | `POST /login` | Tấn công chéo Token (Token giả mạo) | Sửa 1 vài ký tự trong chuỗi Token hợp lệ | HTTP 401 Unauthorized (Lỗi Invalid Signature) |

---

## 📦 Module 2: Products & Master Data (Sản phẩm, Hãng, Danh mục)
**Mục tiêu:** Kiểm tra CRUD sản phẩm và các ràng buộc dữ liệu (Validation).

| ID | API | Kịch bản (Test Case) | Dữ liệu đầu vào (Input) | Kết quả mong đợi (Expected Output) |
|---|---|---|---|---|
| PROD-01 | `GET /products` | Lấy danh sách thành công | Params: rỗng | HTTP 200, List Products |
| PROD-02 | `POST /products` | Thêm sản phẩm hợp lệ | Đủ các trường, giá: 500000 | HTTP 200, Product ID mới tạo |
| PROD-03 | `POST /products` | Giá sản phẩm âm (Edge case) | `price`: -1000 | HTTP 400, Báo lỗi Validation giá phải > 0 |
| PROD-04 | `POST /products` | Brand/Category không tồn tại | `brandId`: 999 | HTTP 404, `BRAND_NOT_FOUND` |
| PROD-05 | `PUT /products/{id}` | Cập nhật giá sản phẩm | `price`: 600000 | HTTP 200, Giá thay đổi |
| PROD-06 | `DELETE /products/{id}`| Xóa sản phẩm đang có tồn kho | Xóa ID đang có `stock > 0` | HTTP 400, Không cho xóa sản phẩm còn hàng |

---

## 🏭 Module 3: Suppliers, Import & Shell Exchange (NSX, Nhập hàng, Đối lưu)
**Mục tiêu:** Kiểm tra độ chính xác của thuật toán tính tiền, trừ nợ, và trừ tồn kho vỏ.

### 3.1. Nhập hàng & Thanh toán Nợ
| ID | API | Kịch bản (Test Case) | Dữ liệu đầu vào (Input) | Kết quả mong đợi (Expected Output) |
|---|---|---|---|---|
| IMP-01 | `POST /imports` | Nhập hàng trả thiếu tiền (Ghi nợ) | Tổng: 10M, `amountPaid`: 2M | HTTP 200, `paymentStatus`: PARTIAL, `debtAmount`: 8M |
| IMP-02 | `POST /imports` | Nhập hàng với Product sai | `productId`: 999 | HTTP 404, `PRODUCT_NOT_FOUND` |
| IMP-03 | `POST /imports` | Trả lố tiền khi nhập hàng (Edge) | Tổng: 5M, `amountPaid`: 6M | HTTP 400, Báo lỗi `AMOUNT_PAID_EXCEEDS_TOTAL` (hoặc trừ dư nợ) |
| PAY-01 | `POST /suppliers/{id}/pay` | Trả nợ hợp lệ | `amount`: 1M | HTTP 200, Dư nợ giảm 1M |
| PAY-02 | `POST /suppliers/{id}/pay` | Trả lố dư nợ hiện tại (Edge) | `debtBalance`: 8M, trả: 10M | HTTP 400, `PAYMENT_AMOUNT_EXCEEDS_DEBT` |
| PAY-03 | `POST /suppliers/{id}/pay` | Trả số tiền âm/bằng 0 (Edge) | `amount`: -500 | HTTP 400, Lỗi Validation amount > 0 |

### 3.2. Đối lưu Vỏ rỗng
| ID | API | Kịch bản (Test Case) | Dữ liệu đầu vào (Input) | Kết quả mong đợi (Expected Output) |
|---|---|---|---|---|
| EXC-01 | `POST /exchange` | Đối lưu hợp lệ 1-1 | Xuất 30 vỏ PG, Nhập 30 bình PG | HTTP 200, Vỏ -30, Bình đầy +30, Phiếu loại `SHELL_EXCHANGE` |
| EXC-02 | `POST /exchange` | Lệch hãng (Brand Mismatch) | Xuất vỏ: Total, Nhập bình: PG | HTTP 400, Lỗi `BRAND_MISMATCH` |
| EXC-03 | `POST /exchange` | Vượt tồn kho vỏ rỗng (Edge) | Tồn kho có: 10, Xuất: 50 | HTTP 400, Lỗi `INSUFFICIENT_SHELL_STOCK` |
| EXC-04 | `POST /exchange` | Số lượng đối lưu âm (Edge) | `exportedShellQuantity`: -5 | HTTP 400, Lỗi Validation (phải > 0) |

---
**Hướng dẫn thực thi:**
Em có thể dựa vào danh sách này và bổ sung thêm các Request vào thư mục tương ứng trên Postman để tạo thành 1 bộ Test Suite hoàn chỉnh. Khi nào em test xong các Case này mà hệ thống đều phản hồi đúng (hoặc chặn đúng lỗi), chúng ta có thể kê cao gối ngủ và bắt tay vào Module mới!
