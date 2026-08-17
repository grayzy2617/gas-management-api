package vn.gaspro.api.service;

import vn.gaspro.api.dto.request.CartItemRequest;
import vn.gaspro.api.dto.response.CartItemResponse;
import vn.gaspro.api.dto.response.CartSummaryResponse;

public interface CartService {
    CartSummaryResponse getMyCart();
    CartItemResponse addItem(CartItemRequest request);
    CartItemResponse updateItem(Long cartItemId, CartItemRequest request);
    void removeItem(Long cartItemId);
}
