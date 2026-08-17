package vn.gaspro.api.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.gaspro.api.dto.request.OrderCreateRequest;
import vn.gaspro.api.dto.response.OrderResponse;

import java.time.LocalDate;

public interface OrderService {
    OrderResponse createOrder(OrderCreateRequest request);
    Page<OrderResponse> getOrders(String status, String paymentMethod, String paymentStatus, String query, LocalDate fromDate, LocalDate toDate, Pageable pageable);
    void assignRandomDriver(Long orderId); // Helper for auto-assignment if no one claims
}
