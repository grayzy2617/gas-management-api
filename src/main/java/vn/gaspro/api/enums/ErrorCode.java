package vn.gaspro.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

/**
 * Enum quản lý tập trung toàn bộ Mã Lỗi (Business Error Codes) trong hệ thống Gas Pro.
 * Phục vụ Global Exception Handler trả về JSON Response chuẩn cho Frontend.
 */
@Getter
@AllArgsConstructor
public enum ErrorCode {

    // =========================================================================
    // 1. SYSTEM & GENERAL ERRORS (1000 - 1999)
    // =========================================================================
    SUCCESS(200, "Thành công", HttpStatus.OK),
    CREATED(201, "Tạo mới tài nguyên thành công", HttpStatus.CREATED),
    UNCATEGORIZED_EXCEPTION(9999, "Lỗi hệ thống không xác định", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY(1001, "Mã lỗi Enum không hợp lệ", HttpStatus.BAD_REQUEST),
    UNAUTHENTICATED(1002, "Chưa xác thực hoặc Token không hợp lệ", HttpStatus.UNAUTHORIZED),
    TOKEN_EXPIRED(1008, "Token đã hết hạn, vui lòng đăng nhập lại", HttpStatus.UNAUTHORIZED),
    INVALID_TOKEN(1009, "Token không hợp lệ", HttpStatus.UNAUTHORIZED),
    TOKEN_BLACKLISTED(1010, "Token đã bị vô hiệu hóa (Blacklisted)", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1003, "Bạn không có quyền thực hiện thao tác này", HttpStatus.FORBIDDEN),
    FORBIDDEN(403, "You do not have permission", HttpStatus.FORBIDDEN),
    INVALID_REQUEST_BODY(1004, "Dữ liệu Request Body không đúng định dạng JSON", HttpStatus.BAD_REQUEST),
    METHOD_NOT_ALLOWED(1005, "Phương thức HTTP không được hỗ trợ", HttpStatus.METHOD_NOT_ALLOWED),
    RESOURCE_NOT_FOUND(1006, "Không tìm thấy tài nguyên yêu cầu", HttpStatus.NOT_FOUND),
    RATE_LIMIT_EXCEEDED(1007, "Bạn đã gửi quá nhiều yêu cầu. Vui lòng thử lại sau", HttpStatus.TOO_MANY_REQUESTS),

    // =========================================================================
    // 2. AUTH & USER ERRORS (2000 - 2999)
    // =========================================================================
    PHONE_EXISTED(2001, "Số điện thoại đã được đăng ký trên hệ thống", HttpStatus.CONFLICT),
    USER_NOT_EXISTED(2002, "Tài khoản người dùng không tồn tại", HttpStatus.NOT_FOUND),
    WRONG_PASSWORD(2003, "Số điện thoại hoặc mật khẩu không chính xác", HttpStatus.UNAUTHORIZED),
    ACCOUNT_LOCKED(2004, "Tài khoản của bạn đã bị khóa do vi phạm chính sách", HttpStatus.FORBIDDEN),
    REFRESH_TOKEN_EXPIRED(2005, "Refresh Token đã hết hạn. Vui lòng đăng nhập lại", HttpStatus.UNAUTHORIZED),
    WEAK_PASSWORD(2006, "Mật khẩu phải chứa ít nhất 6 ký tự trở lên", HttpStatus.BAD_REQUEST),
    INVALID_PHONE_FORMAT(2007, "Số điện thoại không đúng định dạng (Phải từ 10-11 chữ số)", HttpStatus.BAD_REQUEST),

    // =========================================================================
    // 3. PRODUCT & CATALOG ERRORS (3000 - 3999)
    // =========================================================================
    PRODUCT_NOT_EXISTED(3001, "Sản phẩm không tồn tại trên hệ thống", HttpStatus.NOT_FOUND),
    PRODUCT_PRICE_INVALID(3002, "Giá bán sản phẩm phải lớn hơn 0 VNĐ", HttpStatus.BAD_REQUEST),
    CATEGORY_NOT_EXISTED(3003, "Loại sản phẩm không tồn tại", HttpStatus.NOT_FOUND),
    BRAND_NOT_EXISTED(3004, "Hãng sản xuất không tồn tại", HttpStatus.NOT_FOUND),
    PRODUCT_OUT_OF_STOCK(3005, "Sản phẩm đã hết hàng trong kho", HttpStatus.BAD_REQUEST),

    // =========================================================================
    // 4. SUPPLIER & IMPORT ERRORS (4000 - 4999)
    // =========================================================================
    SUPPLIER_NOT_EXISTED(4001, "Nhà sản xuất không tồn tại", HttpStatus.NOT_FOUND),
    IMPORT_RECEIPT_NOT_EXISTED(4002, "Phiếu nhập kho không tồn tại", HttpStatus.NOT_FOUND),
    PAYMENT_EXCEEDS_DEBT(4003, "Số tiền chi trả vượt quá dư nợ gối đầu hiện tại của NSX", HttpStatus.BAD_REQUEST),
    BRAND_MISMATCH_EXCHANGE(4004, "BRAND MISMATCH: Hãng vỏ rỗng xuất và bình gas đầy nhận phải khớp 100%", HttpStatus.UNPROCESSABLE_ENTITY),
    INSUFFICIENT_EMPTY_SHELL_STOCK(4005, "Số lượng vỏ rỗng xuất đối lưu vượt quá tồn kho vỏ rỗng hiện tại", HttpStatus.BAD_REQUEST),

    // =========================================================================
    // 5. CUSTOMER & DEBT ERRORS (5000 - 5999)
    // =========================================================================
    CUSTOMER_NOT_EXISTED(5001, "Hồ sơ khách hàng không tồn tại", HttpStatus.NOT_FOUND),
    INVALID_TAX_CODE(5002, "Mã số thuế không đúng định dạng (Phải bao gồm 10 hoặc 13 chữ số)", HttpStatus.BAD_REQUEST),
    DEBT_INELIGIBLE(5003, "Khách hàng không đủ điều kiện ghi nợ (Chưa đủ thâm niên 1 năm hoặc sản lượng tích lũy)", HttpStatus.BAD_REQUEST),
    DEBT_LIMIT_EXCEEDED(5004, "Tài khoản của bạn đã vượt quá hạn mức công nợ cho phép", HttpStatus.BAD_REQUEST),
    DEBT_OVERDUE_LOCKED(5005, "Tài khoản bị khóa nợ do có khoản nợ quá hạn 30 ngày", HttpStatus.FORBIDDEN),
    CUSTOMER_SPAM_LOCKED(5006, "Khách hàng bị khóa đặt COD/Nợ do hủy đơn quá 3 lần trong 24h", HttpStatus.FORBIDDEN),

    // =========================================================================
    // 6. ORDER & DELIVERY ERRORS (6000 - 6999)
    // =========================================================================
    ORDER_NOT_EXISTED(6001, "Đơn hàng không tồn tại", HttpStatus.NOT_FOUND),
    ORDER_ALREADY_CLAIMED(6002, "Đơn hàng đã được tài xế khác nhận trước", HttpStatus.CONFLICT),
    DRIVER_MAX_ACTIVE_ORDERS(6003, "Bạn đang giữ tối đa 3 đơn hàng đang giao, không thể nhận thêm", HttpStatus.BAD_REQUEST),
    DRIVER_APP_LOCKED_OUT(6004, "Ứng dụng tài xế đang bị phạt khóa nhận đơn do từ chối đơn gán", HttpStatus.FORBIDDEN),
    DRIVER_OFFLINE(6005, "Tài xế đang Ngoại tuyến. Bật Trực tuyến để nhận đơn", HttpStatus.BAD_REQUEST),
    ORDER_CANNOT_BE_CANCELLED(6006, "Không thể hủy đơn hàng đã hoàn thành", HttpStatus.BAD_REQUEST),
    PENDING_PAYMENT_PROOF_MISSING(6007, "Bắt buộc phải tải lên ảnh chụp màn hình ngân hàng báo lỗi", HttpStatus.BAD_REQUEST),

    // =========================================================================
    // 7. SHIFT & RECONCILE ERRORS (7000 - 7999)
    // =========================================================================
    SHIFT_NOT_EXISTED(7001, "Ca làm việc của tài xế không tồn tại", HttpStatus.NOT_FOUND),
    SHIFT_ALREADY_CLOSED(7002, "Ca làm việc đã được đóng và quyết toán trước đó", HttpStatus.BAD_REQUEST),
    SEAL_VIOLATION_NOTE_MISSING(7003, "Bắt buộc nhập nội dung biên bản vi phạm khi niêm phong bị rách", HttpStatus.BAD_REQUEST),
    BANK_MAINTENANCE_PROOF_MISSING(7004, "Bắt buộc tải ảnh bằng chứng ngân hàng bảo trì", HttpStatus.BAD_REQUEST),

    // =========================================================================
    // 8. WARRANTY & LOAN ERRORS (8000 - 8999)
    // =========================================================================
    WARRANTY_TICKET_NOT_EXISTED(8001, "Ticket bảo hành không tồn tại", HttpStatus.NOT_FOUND),
    WARRANTY_MEDIA_PROOF_MISSING(8002, "Bắt buộc đính kèm Ảnh hoặc Video <10s minh chứng tình trạng lỗi", HttpStatus.BAD_REQUEST),
    SPARE_PART_NOT_IN_MOBILE_INVENTORY(8003, "Linh kiện không có sẵn trong hòm đồ di động trên xe tài xế", HttpStatus.BAD_REQUEST),
    PROPOSAL_NOT_FULLY_APPROVED(8004, "Đề xuất thay linh kiện chưa được cả Tổng đài và Khách hàng bấm duyệt", HttpStatus.BAD_REQUEST),
    TEMPORARY_STOVE_NOT_AVAILABLE(8005, "Bếp dùng tạm hiện đang được khách hàng khác mượn", HttpStatus.BAD_REQUEST);

    private final int code;
    private final String message;
    private final HttpStatusCode httpStatusCode;
}