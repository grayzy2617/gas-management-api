package vn.gaspro.api.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.gaspro.api.dto.request.CartItemRequest;
import vn.gaspro.api.dto.response.CartItemResponse;
import vn.gaspro.api.dto.response.CartSummaryResponse;
import vn.gaspro.api.entity.CartItem;
import vn.gaspro.api.entity.Customer;
import vn.gaspro.api.entity.Product;
import vn.gaspro.api.enums.ErrorCode;
import vn.gaspro.api.exception.AppException;
import vn.gaspro.api.mapper.CartMapper;
import vn.gaspro.api.repository.CartItemRepository;
import vn.gaspro.api.repository.CustomerRepository;
import vn.gaspro.api.repository.ProductRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import vn.gaspro.api.service.CartService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartItemRepository cartItemRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final CartMapper cartMapper;

    private Customer getCurrentCustomer() {
        String phone = SecurityContextHolder.getContext().getAuthentication().getName();
        return customerRepository.findByPhone(phone)
                .orElseThrow(() -> new AppException(ErrorCode.CUSTOMER_NOT_EXISTED));
    }

    @Override
    @Transactional(readOnly = true)
    public CartSummaryResponse getMyCart() {
        Customer customer = getCurrentCustomer();
        List<CartItem> items = cartItemRepository.findByCustomerId(customer.getId());

        List<CartItemResponse> itemResponses = items.stream().map(item -> {
            CartItemResponse response = cartMapper.toResponse(item);
            calculateItemAmounts(response, item.getProduct().getDefaultDepositFee(), item.getHasExchangeShell(), item.getQuantity());
            return response;
        }).collect(Collectors.toList());

        BigDecimal totalGoods = itemResponses.stream().map(CartItemResponse::getSubtotal).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalDeposit = itemResponses.stream().map(CartItemResponse::getUnitDepositFee).reduce(BigDecimal.ZERO, BigDecimal::add);

        CartSummaryResponse.CartSummary summary = CartSummaryResponse.CartSummary.builder()
                .totalGoodsAmount(totalGoods)
                .totalDepositAmount(totalDeposit)
                .grandTotal(totalGoods.add(totalDeposit))
                .build();

        return CartSummaryResponse.builder()
                .cartId(customer.getId()) // use customerId as mock cartId
                .items(itemResponses)
                .summary(summary)
                .build();
    }

    @Override
    @Transactional
    public CartItemResponse addItem(CartItemRequest request) {
        Customer customer = getCurrentCustomer();
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED));

        Optional<CartItem> existingItemOpt = cartItemRepository.findByCustomerIdAndProductId(customer.getId(), product.getId());
        CartItem cartItem;
        
        if (existingItemOpt.isPresent()) {
            cartItem = existingItemOpt.get();
            cartItem.setQuantity(cartItem.getQuantity() + request.getQuantity());
            cartItem.setHasExchangeShell(request.getHasExchangeShell());
        } else {
            cartItem = CartItem.builder()
                    .customer(customer)
                    .product(product)
                    .quantity(request.getQuantity())
                    .hasExchangeShell(request.getHasExchangeShell())
                    .build();
        }

        cartItem = cartItemRepository.save(cartItem);

        CartItemResponse response = cartMapper.toResponse(cartItem);
        calculateItemAmounts(response, product.getDefaultDepositFee(), cartItem.getHasExchangeShell(), cartItem.getQuantity());
        return response;
    }

    @Override
    @Transactional
    public CartItemResponse updateItem(Long cartItemId, CartItemRequest request) {
        Customer customer = getCurrentCustomer();
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new AppException(ErrorCode.CART_ITEM_NOT_EXISTED));

        if (!cartItem.getCustomer().getId().equals(customer.getId())) {
            throw new AppException(ErrorCode.FORBIDDEN);
        }

        cartItem.setQuantity(request.getQuantity());
        cartItem.setHasExchangeShell(request.getHasExchangeShell());

        cartItem = cartItemRepository.save(cartItem);

        CartItemResponse response = cartMapper.toResponse(cartItem);
        calculateItemAmounts(response, cartItem.getProduct().getDefaultDepositFee(), cartItem.getHasExchangeShell(), cartItem.getQuantity());
        return response;
    }

    @Override
    @Transactional
    public void removeItem(Long cartItemId) {
        Customer customer = getCurrentCustomer();
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new AppException(ErrorCode.CART_ITEM_NOT_EXISTED));

        if (!cartItem.getCustomer().getId().equals(customer.getId())) {
            throw new AppException(ErrorCode.FORBIDDEN);
        }

        cartItemRepository.delete(cartItem);
    }

    private void calculateItemAmounts(CartItemResponse response, BigDecimal defaultDeposit, boolean hasExchangeShell, int quantity) {
        BigDecimal unitDeposit = hasExchangeShell ? BigDecimal.ZERO : defaultDeposit;
        response.setUnitDepositFee(unitDeposit.multiply(BigDecimal.valueOf(quantity)));
        response.setSubtotal(response.getUnitPrice().multiply(BigDecimal.valueOf(quantity)));
    }
}
