package vn.gaspro.api.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.gaspro.api.dto.response.ApiResponse;
import vn.gaspro.api.enums.ErrorCode;
import vn.gaspro.api.exception.AppException;

/**
 * Controller kiểm tra trạng thái hoạt động của Service (Health Check)
 * và test luồng xử lý Exception tập trung.
 */
@RestController
@RequestMapping("/api/v1/health-check")
public class HealthCheckController {

    /**
     * Test trạng thái server xem API đã sẵn sàng nhận request chưa
     */
    @GetMapping
    public ApiResponse<String> healthCheck() {
        return ApiResponse.success("Gas Pro API Service is running smoothly!", null);
    }

    /**
     * Cố tình throw AppException để test xem GlobalExceptionHandler có bắt đúng lỗi không
     */
    @GetMapping("/test-error")
    public ApiResponse<Void> testError() {
        throw new AppException(ErrorCode.RESOURCE_NOT_FOUND);
    }
}