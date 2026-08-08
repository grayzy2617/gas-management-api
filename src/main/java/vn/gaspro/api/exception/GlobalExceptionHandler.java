package vn.gaspro.api.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import vn.gaspro.api.dto.response.ApiResponse;
import vn.gaspro.api.enums.ErrorCode;

import java.util.Objects;

/**
 * Bộ xử lý ngoại lệ tập trung (Global Exception Handler) cho toàn bộ Controller.
 * Bắt các Exception và đóng gói thành JSON Response chuẩn theo cấu trúc ApiResponse.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 1. Bắt lỗi nghiệp vụ (AppException) do lập trình viên chủ động throw.
     * Trả về HTTP Status Code và Custom Code tương ứng định nghĩa trong ErrorCode Enum.
     */
    @ExceptionHandler(AppException.class)
    public ResponseEntity<ApiResponse<Void>> handlingAppException(AppException exception) {
        ErrorCode errorCode = exception.getErrorCode();
        log.warn("Business Exception Occurred: [Code: {}] - {}", errorCode.getCode(), errorCode.getMessage());

        ApiResponse<Void> apiResponse = ApiResponse.error(errorCode.getCode(), errorCode.getMessage());

        return ResponseEntity
                .status(errorCode.getHttpStatusCode())
                .body(apiResponse);
    }

    /**
     * 2. Bắt lỗi Validation (@Valid) từ Spring Boot khi Request Body vi phạm quy tắc (@NotBlank, @Size...).
     * Trả về HTTP 400 BAD_REQUEST và câu thông báo lỗi cụ thể của field bị vi phạm.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handlingValidation(MethodArgumentNotValidException exception) {
        String enumKey = Objects.requireNonNull(exception.getFieldError()).getDefaultMessage();

        ErrorCode errorCode = ErrorCode.INVALID_REQUEST_BODY;
        String errorMessage = enumKey;

        // Mẹo: Kiểm tra xem message trong Annotation có phải là một Key Enum không
        try {
            errorCode = ErrorCode.valueOf(enumKey);
            errorMessage = errorCode.getMessage();
        } catch (IllegalArgumentException e) {
            // Nếu không phải Key Enum thì giữ nguyên chuỗi message mặc định
        }

        log.warn("Validation Error: {}", errorMessage);

        ApiResponse<Void> apiResponse = ApiResponse.error(errorCode.getCode(), errorMessage);

        return ResponseEntity
                .status(errorCode.getHttpStatusCode())
                .body(apiResponse);
    }

    /**
     * 3. Catch-all: Bắt tất cả các lỗi hệ thống không lường trước được (NullPointerException, DB Error...).
     * Trả về ErrorCode.UNCATEGORIZED_EXCEPTION (Code 9999) và log full StackTrace để debug.
     */
    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<ApiResponse<Void>> handlingUncategorizedException(Exception exception) {
        log.error("Uncategorized Exception: ", exception);

        ErrorCode errorCode = ErrorCode.UNCATEGORIZED_EXCEPTION;
        ApiResponse<Void> apiResponse = ApiResponse.error(errorCode.getCode(), errorCode.getMessage());

        return ResponseEntity
                .status(errorCode.getHttpStatusCode())
                .body(apiResponse);
    }
}