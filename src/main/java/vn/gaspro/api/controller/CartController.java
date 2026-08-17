package vn.gaspro.api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vn.gaspro.api.dto.response.ApiResponse;
import vn.gaspro.api.dto.request.CartItemRequest;
import vn.gaspro.api.dto.response.CartItemResponse;
import vn.gaspro.api.dto.response.CartSummaryResponse;
import vn.gaspro.api.service.CartService;

@RestController
@RequestMapping("/api/v1/cart/items")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<CartSummaryResponse>> getMyCart() {
        return ResponseEntity.ok(ApiResponse.success(cartService.getMyCart(), "Lấy thông tin giỏ hàng thành công"));
    }

    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<CartItemResponse>> addItem(@RequestBody @Valid CartItemRequest request) {
        CartItemResponse response = cartService.addItem(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Đã thêm sản phẩm vào giỏ hàng!"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<CartItemResponse>> updateItem(@PathVariable Long id, @RequestBody @Valid CartItemRequest request) {
        return ResponseEntity.ok(ApiResponse.success(cartService.updateItem(id, request), "Đã cập nhật giỏ hàng thành công!"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<Void>> removeItem(@PathVariable Long id) {
        cartService.removeItem(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Đã xóa sản phẩm khỏi giỏ hàng thành công!"));
    }
}
