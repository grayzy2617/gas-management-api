package vn.gaspro.api.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.gaspro.api.dto.request.OrderCreateRequest;
import vn.gaspro.api.dto.response.OrderResponse;
import vn.gaspro.api.entity.*;
import vn.gaspro.api.enums.DebtStatus;
import vn.gaspro.api.enums.ErrorCode;
import vn.gaspro.api.enums.OrderStatus;
import vn.gaspro.api.enums.PaymentMethod;
import vn.gaspro.api.enums.PaymentStatus;
import vn.gaspro.api.exception.AppException;
import vn.gaspro.api.mapper.OrderMapper;
import vn.gaspro.api.repository.*;
import org.springframework.security.core.context.SecurityContextHolder;
import vn.gaspro.api.service.OrderService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderMapper orderMapper;

    @Override
    @Transactional
    public OrderResponse createOrder(OrderCreateRequest request) {
        String phone = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByPhone(phone)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        Customer customer = customerRepository.findByUserId(user.getId()).orElse(null);
        if (customer == null) {
            // Operator could be creating this, but for simplicity we assume customer creates it or Operator acts on behalf.
            // If Operator acts on behalf, we need customerId in request. Let's assume customer is the current user.
            throw new AppException(ErrorCode.CUSTOMER_NOT_EXISTED);
        }

        if (customer.getIsSpamLocked()) {
            throw new AppException(ErrorCode.CUSTOMER_SPAM_LOCKED);
        }

        if (request.getPaymentMethod() == PaymentMethod.CREDIT_DEBT) {
            if (customer.getDebtStatus() != DebtStatus.ELIGIBLE) {
                throw new AppException(ErrorCode.DEBT_INELIGIBLE);
            }
            // Check limit
            // Note: In real app, we must check if currentDebt + grandTotal > creditLimit.
            // But we don't have grandTotal yet. We will check it after calculation.
        }

        Order order = Order.builder()
                .orderCode("DH-" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "-" + UUID.randomUUID().toString().substring(0, 4).toUpperCase())
                .customer(customer)
                .createdBy(user)
                .deliveryAddress(request.getDeliveryAddress())
                .distanceKm(request.getDistanceKm())
                .paymentMethod(request.getPaymentMethod())
                .paymentStatus(PaymentStatus.UNPAID)
                .orderStatus(OrderStatus.PENDING)
                .notes(request.getNotes())
                .shippingFee(request.getDistanceKm().multiply(BigDecimal.valueOf(5000))) // D_RATE = 5000
                .build();

        BigDecimal totalGoods = BigDecimal.ZERO;
        BigDecimal totalDeposit = BigDecimal.ZERO;

        List<OrderItem> items = request.getItems().stream().map(itemReq -> {
            Product product = productRepository.findById(itemReq.getProductId())
                    .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED));

            if (product.getStockQuantity() < itemReq.getQuantity()) {
                throw new AppException(ErrorCode.PRODUCT_OUT_OF_STOCK);
            }
            // Deduct stock temporarily
            product.setStockQuantity(product.getStockQuantity() - itemReq.getQuantity());
            productRepository.save(product);

            BigDecimal unitDeposit = itemReq.getHasExchangeShell() ? BigDecimal.ZERO : product.getDefaultDepositFee();
            BigDecimal subtotal = product.getPrice().multiply(BigDecimal.valueOf(itemReq.getQuantity()));

            return OrderItem.builder()
                    .order(order)
                    .product(product)
                    .productName(product.getName())
                    .quantity(itemReq.getQuantity())
                    .unitPrice(product.getPrice())
                    .hasExchangeShell(itemReq.getHasExchangeShell())
                    .unitDepositFee(unitDeposit.multiply(BigDecimal.valueOf(itemReq.getQuantity())))
                    .subtotal(subtotal)
                    .build();
        }).collect(Collectors.toList());

        for (OrderItem item : items) {
            totalGoods = totalGoods.add(item.getSubtotal());
            totalDeposit = totalDeposit.add(item.getUnitDepositFee());
        }

        order.setTotalGoodsAmount(totalGoods);
        order.setTotalDepositAmount(totalDeposit);
        BigDecimal grandTotal = totalGoods.add(totalDeposit).add(order.getShippingFee());
        order.setGrandTotal(grandTotal);
        order.setItems(items);

        if (request.getPaymentMethod() == PaymentMethod.CREDIT_DEBT) {
            if (customer.getCurrentDebt().add(grandTotal).compareTo(customer.getCreditLimit()) > 0) {
                throw new AppException(ErrorCode.DEBT_LIMIT_EXCEEDED);
            }
        }

        // Clear cart items if any
        cartItemRepository.deleteByCustomerId(customer.getId());

        orderRepository.save(order);

        // System randomly assigns driver if no one claims (can be triggered asynchronously here or via cron)
        // triggerAutoAssignEvent(order.getId());

        return orderMapper.toResponse(order);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderResponse> getOrders(String status, String paymentMethod, String paymentStatus, String query, LocalDate fromDate, LocalDate toDate, Pageable pageable) {
        Specification<Order> spec = (root, cq, cb) -> {
            Specification<Order> s = Specification.where(null);
            if (status != null) {
                s = s.and((r, q, c) -> c.equal(r.get("orderStatus"), OrderStatus.valueOf(status)));
            }
            if (paymentMethod != null) {
                s = s.and((r, q, c) -> c.equal(r.get("paymentMethod"), PaymentMethod.valueOf(paymentMethod)));
            }
            if (paymentStatus != null) {
                s = s.and((r, q, c) -> c.equal(r.get("paymentStatus"), PaymentStatus.valueOf(paymentStatus)));
            }
            if (query != null && !query.isBlank()) {
                String lq = "%" + query.toLowerCase() + "%";
                s = s.and((r, q, c) -> c.or(
                        c.like(cb.lower(r.get("orderCode")), lq)
                ));
            }
            return s.toPredicate(root, cq, cb);
        };

        return orderRepository.findAll(spec, pageable).map(orderMapper::toResponse);
    }

    @Override
    @Transactional
    public void assignRandomDriver(Long orderId) {
        // Mock auto assignment logic
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_EXISTED));
        if (order.getOrderStatus() == OrderStatus.PENDING) {
            // Find an active driver randomly and assign. 
            // In a real system, we query drivers with `is_online = true` and `active_orders_count < 3` 
            // order.setDriver(randomDriver);
            // order.setOrderStatus(OrderStatus.ASSIGNED);
            // orderRepository.save(order);
        }
    }
}
