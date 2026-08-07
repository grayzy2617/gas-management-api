# BẢNG MÃ LỖI HỆ THỐNG (SYSTEM ERROR CODES CATALOG)

> **Dự án:** Hệ Thống Quản Lý Gas Pro  
> **Thư mục:** `document/api_design/00_error_codes.md`  
> **Mô tả:** Danh mục tổng hợp toàn bộ các Mã Lỗi (ErrorCode Enum) chuẩn hóa có thể xảy ra trong hệ thống, phục vụ Backend trả về và Frontend hiển thị thông báo/toast chính xác cho người dùng.

---

## 📐 CẤU TRÚC ENUM MÃ LỖI (JAVA / SPRING BOOT SPECIFICATION)

Cấu trúc Enum bao gồm 3 thuộc tính chính:
- **`code`** (`int`): Mã lỗi số nguyên độc nhất đại diện cho lỗi nghiệp vụ.
- **`message`** (`String`): Thông báo tiếng Việt rõ ràng, thân thiện cho Frontend hiển thị Toast/Alert.
- **`httpStatusCode`** (`HttpStatusCode`): HTTP Status Code chuẩn đi kèm.

```java
public enum ErrorCode {
    // 1. SYSTEM & GENERAL ERRORS (1000 - 1999)
    SUCCESS(200, "Thành công", HttpStatus.OK),
    CREATED(201, "Tạo mới tài nguyên thành công", HttpStatus.CREATED),
    UNCATEGORIZED_EXCEPTION(9999, "Lỗi hệ thống không xác định", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY(1001, "Mã lỗi Enum không hợp lệ", HttpStatus.BAD_REQUEST),
    UNAUTHENTICATED(1002, "Chưa xác thực hoặc Token không hợp lệ", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1003, "Bạn không có quyền thực hiện thao tác này", HttpStatus.FORBIDDEN),
    FORBIDDEN(403, "You do not have permission", HttpStatus.FORBIDDEN),
    INVALID_REQUEST_BODY(1004, "Dữ liệu Request Body không đúng định dạng JSON", HttpStatus.BAD_REQUEST),
    METHOD_NOT_ALLOWED(1005, "Phương thức HTTP không được hỗ trợ", HttpStatus.METHOD_NOT_ALLOWED),
    RESOURCE_NOT_FOUND(1006, "Không tìm thấy tài nguyên yêu cầu", HttpStatus.NOT_FOUND),
    RATE_LIMIT_EXCEEDED(1007, "Bạn đã gửi quá nhiều yêu cầu. Vui lòng thử lại sau", HttpStatus.TOO_MANY_REQUESTS),

    // 2. AUTH & USER ERRORS (2000 - 2999)
    PHONE_EXISTED(2001, "Số điện thoại đã được đăng ký trên hệ thống", HttpStatus.CONFLICT),
    USER_NOT_EXISTED(2002, "Tài khoản người dùng không tồn tại", HttpStatus.NOT_FOUND),
    WRONG_PASSWORD(2003, "Số điện thoại hoặc mật khẩu không chính xác", HttpStatus.UNAUTHORIZED),
    ACCOUNT_LOCKED(2004, "Tài khoản của bạn đã bị khóa do vi phạm chính sách", HttpStatus.FORBIDDEN),
    REFRESH_TOKEN_EXPIRED(2005, "Refresh Token đã hết hạn. Vui lòng đăng nhập lại", HttpStatus.UNAUTHORIZED),
    WEAK_PASSWORD(2006, "Mật khẩu phải chứa ít nhất 6 ký tự trở lên", HttpStatus.BAD_REQUEST),
    INVALID_PHONE_FORMAT(2007, "Số điện thoại không đúng định dạng (Phải từ 10-11 chữ số)", HttpStatus.BAD_REQUEST),

    // 3. PRODUCT & CATALOG ERRORS (3000 - 3999)
    PRODUCT_NOT_EXISTED(3001, "Sản phẩm không tồn tại trên hệ thống", HttpStatus.NOT_FOUND),
    PRODUCT_PRICE_INVALID(3002, "Giá bán sản phẩm phải lớn hơn 0 VNĐ", HttpStatus.BAD_REQUEST),
    CATEGORY_NOT_EXISTED(3003, "Loại sản phẩm không tồn tại", HttpStatus.NOT_FOUND),
    BRAND_NOT_EXISTED(3004, "Hãng sản xuất không tồn tại", HttpStatus.NOT_FOUND),
    PRODUCT_OUT_OF_STOCK(3005, "Sản phẩm đã hết hàng trong kho", HttpStatus.BAD_REQUEST),

    // 4. SUPPLIER & IMPORT ERRORS (4000 - 4999)
    SUPPLIER_NOT_EXISTED(4001, "Nhà sản xuất không tồn tại", HttpStatus.NOT_FOUND),
    IMPORT_RECEIPT_NOT_EXISTED(4002, "Phiếu nhập kho không tồn tại", HttpStatus.NOT_FOUND),
    PAYMENT_EXCEEDS_DEBT(4003, "Số tiền chi trả vượt quá dư nợ gối đầu hiện tại của NSX", HttpStatus.BAD_REQUEST),
    BRAND_MISMATCH_EXCHANGE(4004, "BRAND MISMATCH: Hãng vỏ rỗng xuất và bình gas đầy nhận phải khớp 100%", HttpStatus.UNPROCESSABLE_ENTITY),
    INSUFFICIENT_EMPTY_SHELL_STOCK(4005, "Số lượng vỏ rỗng xuất đối lưu vượt quá tồn kho vỏ rỗng hiện tại", HttpStatus.BAD_REQUEST),

    // 5. CUSTOMER & DEBT ERRORS (5000 - 5999)
    CUSTOMER_NOT_EXISTED(5001, "Hồ sơ khách hàng không tồn tại", HttpStatus.NOT_FOUND),
    INVALID_TAX_CODE(5002, "Mã số thuế không đúng định dạng (Phải bao gồm 10 hoặc 13 chữ số)", HttpStatus.BAD_REQUEST),
    DEBT_INELIGIBLE(5003, "Khách hàng không đủ điều kiện ghi nợ (Chưa đủ thâm niên 1 năm hoặc sản lượng tích lũy)", HttpStatus.BAD_REQUEST),
    DEBT_LIMIT_EXCEEDED(5004, "Tài khoản của bạn đã vượt quá hạn mức công nợ cho phép", HttpStatus.BAD_REQUEST),
    DEBT_OVERDUE_LOCKED(5005, "Tài khoản bị khóa nợ do có khoản nợ quá hạn 30 ngày", HttpStatus.FORBIDDEN),
    CUSTOMER_SPAM_LOCKED(5006, "Khách hàng bị khóa đặt COD/Nợ do hủy đơn quá 3 lần trong 24h", HttpStatus.FORBIDDEN),

    // 6. ORDER & DELIVERY ERRORS (6000 - 6999)
    ORDER_NOT_EXISTED(6001, "Đơn hàng không tồn tại", HttpStatus.NOT_FOUND),
    ORDER_ALREADY_CLAIMED(6002, "Đơn hàng đã được tài xế khác nhận trước", HttpStatus.CONFLICT),
    DRIVER_MAX_ACTIVE_ORDERS(6003, "Bạn đang giữ tối đa 3 đơn hàng đang giao, không thể nhận thêm", HttpStatus.BAD_REQUEST),
    DRIVER_APP_LOCKED_OUT(6004, "Ứng dụng tài xế đang bị phạt khóa nhận đơn do từ chối đơn gán", HttpStatus.FORBIDDEN),
    DRIVER_OFFLINE(6005, "Tài xế đang Ngoại tuyến. Bật Trực tuyến để nhận đơn", HttpStatus.BAD_REQUEST),
    ORDER_CANNOT_BE_CANCELLED(6006, "Không thể hủy đơn hàng đã hoàn thành", HttpStatus.BAD_REQUEST),
    PENDING_PAYMENT_PROOF_MISSING(6007, "Bắt buộc phải tải lên ảnh chụp màn hình ngân hàng báo lỗi", HttpStatus.BAD_REQUEST),

    // 7. SHIFT & RECONCILE ERRORS (7000 - 7999)
    SHIFT_NOT_EXISTED(7001, "Ca làm việc của tài xế không tồn tại", HttpStatus.NOT_FOUND),
    SHIFT_ALREADY_CLOSED(7002, "Ca làm việc đã được đóng và quyết toán trước đó", HttpStatus.BAD_REQUEST),
    SEAL_VIOLATION_NOTE_MISSING(7003, "Bắt buộc nhập nội dung biên bản vi phạm khi niêm phong bị rách", HttpStatus.BAD_REQUEST),
    BANK_MAINTENANCE_PROOF_MISSING(7004, "Bắt buộc tải ảnh bằng chứng ngân hàng bảo trì", HttpStatus.BAD_REQUEST),

    // 8. WARRANTY & LOAN ERRORS (8000 - 8999)
    WARRANTY_TICKET_NOT_EXISTED(8001, "Ticket bảo hành không tồn tại", HttpStatus.NOT_FOUND),
    WARRANTY_MEDIA_PROOF_MISSING(8002, "Bắt buộc đính kèm Ảnh hoặc Video <10s minh chứng tình trạng lỗi", HttpStatus.BAD_REQUEST),
    SPARE_PART_NOT_IN_MOBILE_INVENTORY(8003, "Linh kiện không có sẵn trong hòm đồ di động trên xe tài xế", HttpStatus.BAD_REQUEST),
    PROPOSAL_NOT_FULLY_APPROVED(8004, "Đề xuất thay linh kiện chưa được cả Tổng đài và Khách hàng bấm duyệt", HttpStatus.BAD_REQUEST),
    TEMPORARY_STOVE_NOT_AVAILABLE(8005, "Bếp dùng tạm hiện đang được khách hàng khác mượn", HttpStatus.BAD_REQUEST);

    private final int code;
    private final String message;
    private final HttpStatusCode httpStatusCode;

    ErrorCode(int code, String message, HttpStatusCode httpStatusCode) {
        this.code = code;
        this.message = message;
        this.httpStatusCode = httpStatusCode;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public HttpStatusCode getHttpStatusCode() {
        return httpStatusCode;
    }
}
```

---

## 📊 BẢNG TRA CỨU CHI TIẾT CÁC MÃ LỖI (ERROR CODES DIRECTORY)

| Mã Lỗi (`code`) | Enum Constant | Thông báo mặc định (`message`) | HTTP Status Code | Module liên quan |
|:---:|:---|:---|:---:|:---|
| **200** | `SUCCESS` | Thành công | `200 OK` | System |
| **201** | `CREATED` | Tạo mới tài nguyên thành công | `201 CREATED` | System |
| **403** | `FORBIDDEN` | You do not have permission | `403 FORBIDDEN` | Security / Auth |
| **1001** | `INVALID_KEY` | Mã lỗi Enum không hợp lệ | `400 BAD_REQUEST` | System |
| **1002** | `UNAUTHENTICATED` | Chưa xác thực hoặc Token không hợp lệ | `401 UNAUTHORIZED` | Auth |
| **1003** | `UNAUTHORIZED` | Bạn không có quyền thực hiện thao tác này | `403 FORBIDDEN` | RBAC / Authz |
| **1006** | `RESOURCE_NOT_FOUND` | Không tìm thấy tài nguyên yêu cầu | `404 NOT_FOUND` | System |
| **1007** | `RATE_LIMIT_EXCEEDED` | Bạn đã gửi quá nhiều yêu cầu | `429 TOO_MANY_REQUESTS` | Security |
| **2001** | `PHONE_EXISTED` | Số điện thoại đã được đăng ký | `409 CONFLICT` | Auth (UM-001) |
| **2003** | `WRONG_PASSWORD` | Số điện thoại hoặc mật khẩu không chính xác | `401 UNAUTHORIZED` | Auth (UM-002) |
| **2004** | `ACCOUNT_LOCKED` | Tài khoản của bạn đã bị khóa | `403 FORBIDDEN` | Auth |
| **3002** | `PRODUCT_PRICE_INVALID` | Giá bán sản phẩm phải lớn hơn 0 VNĐ | `400 BAD_REQUEST` | Product (CO-004) |
| **4003** | `PAYMENT_EXCEEDS_DEBT` | Số tiền trả vượt quá dư nợ NSX | `400 BAD_REQUEST` | Supplier (CD-005) |
| **4004** | `BRAND_MISMATCH_EXCHANGE` | Hãng vỏ rỗng và bình gas đầy phải khớp 100% | `422 UNPROCESSABLE` | Inventory (CI-002) |
| **5003** | `DEBT_INELIGIBLE` | Khách không đủ điều kiện ghi nợ | `400 BAD_REQUEST` | Customer (CD-001) |
| **5004** | `DEBT_LIMIT_EXCEEDED` | Vượt quá hạn mức công nợ cho phép | `400 BAD_REQUEST` | Customer (CD-001) |
| **6002** | `ORDER_ALREADY_CLAIMED` | Đơn hàng đã được tài xế khác nhận trước | `409 CONFLICT` | Order (SD-001) |
| **6003** | `DRIVER_MAX_ACTIVE_ORDERS`| Đang giữ tối đa 3 đơn hàng đang giao | `400 BAD_REQUEST` | Driver (SD-002) |
| **6004** | `DRIVER_APP_LOCKED_OUT` | Bị phạt khóa nhận đơn T_LOCK_OUT | `403 FORBIDDEN` | Driver (SD-004) |
| **6007** | `PENDING_PAYMENT_PROOF_MISSING` | Bắt buộc tải ảnh minh chứng lỗi ngân hàng | `400 BAD_REQUEST` | Payment (CD-003) |
| **7003** | `SEAL_VIOLATION_NOTE_MISSING` | Bắt buộc nhập biên bản khi niêm phong bị rách | `400 BAD_REQUEST` | Reconcile (RC-004) |
| **8002** | `WARRANTY_MEDIA_PROOF_MISSING` | Bắt buộc đính kèm Ảnh/Video <10s | `400 BAD_REQUEST` | Warranty (WR-001) |
| **8004** | `PROPOSAL_NOT_FULLY_APPROVED` | Đề xuất linh kiện chưa được cả 2 bên duyệt | `400 BAD_REQUEST` | Repair (WR-004) |
| **8005** | `TEMPORARY_STOVE_NOT_AVAILABLE` | Bếp tạm đang được khách khác mượn | `400 BAD_REQUEST` | Stove Loan (WR-005) |

---

## 🛠️ CÁCH SỬ DỤNG TRONG RESPONSE CONTROLLER (GLOBAL EXCEPTION HANDLER)

Khi xảy ra lỗi trong quá trình xử lý API, Backend Service sẽ Ném (Throw) Exception theo cú pháp:

```java
throw new AppException(ErrorCode.DEBT_LIMIT_EXCEEDED);
```

Hệ thống Global Exception Handler sẽ tự động bắt (Catch) và đóng gói thành định dạng Response chuẩn trả về cho Frontend:

```json
{
  "code": 5004,
  "message": "Tài khoản của bạn đã vượt quá hạn mức công nợ cho phép",
  "data": null
}
```

Frontend chỉ cần đọc trường `code` (ví dụ: `5004`) hoặc `message` để hiển thị Toast thông báo màu đỏ cho người dùng một cách chính xác 100%!
