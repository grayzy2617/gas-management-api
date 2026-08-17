package vn.gaspro.api.enums;

public enum PaymentStatus {
    PAID,     // Đã thanh toán 100%
    PARTIAL,  // Thanh toán một phần (còn nợ)
    DEBT,     // Chưa thanh toán (Nợ 100% - Dùng cho phiếu nhập)
    UNPAID,   // Chưa thanh toán (Dùng cho đơn hàng)
    PENDING_PAYMENT // Chờ thanh toán (Sự cố ngân hàng)
}
