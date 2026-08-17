package vn.gaspro.api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vn.gaspro.api.dto.request.OrderCreateRequest;
import vn.gaspro.api.dto.response.ApiResponse;
import vn.gaspro.api.dto.response.OrderResponse;
import vn.gaspro.api.service.OrderService;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @PreAuthorize("hasAnyRole('CUSTOMER', 'OPERATOR', 'ADMIN')")
    public ResponseEntity<ApiResponse<OrderResponse>> createOrder(@RequestBody @Valid OrderCreateRequest request) {
        OrderResponse response = orderService.createOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Đặt hàng thành công! Đơn hàng đã được đưa lên Chợ đơn hàng."));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('CUSTOMER', 'OPERATOR', 'ADMIN')")
    public ResponseEntity<ApiResponse<Page<OrderResponse>>> getOrders(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String paymentMethod,
            @RequestParam(required = false) String paymentStatus,
            @RequestParam(required = false) String query,
            @RequestParam(required = false) LocalDate fromDate,
            @RequestParam(required = false) LocalDate toDate,
            Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(orderService.getOrders(status, paymentMethod, paymentStatus, query, fromDate, toDate, pageable), "Lấy danh sách đơn hàng thành công"));
    }
}
