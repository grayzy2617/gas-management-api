package vn.gaspro.api.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.gaspro.api.dto.request.VatInfoRequest;
import vn.gaspro.api.dto.response.CustomerProfileResponse;
import vn.gaspro.api.dto.response.CustomerSearchResponse;
import vn.gaspro.api.dto.response.VatInfoResponse;
import vn.gaspro.api.entity.Customer;
import vn.gaspro.api.entity.CustomerVatInfo;
import vn.gaspro.api.entity.User;
import vn.gaspro.api.enums.DebtStatus;
import vn.gaspro.api.enums.ErrorCode;
import vn.gaspro.api.exception.AppException;
import vn.gaspro.api.mapper.CustomerMapper;
import vn.gaspro.api.mapper.OrderMapper;
import vn.gaspro.api.repository.CustomerRepository;
import vn.gaspro.api.repository.CustomerVatInfoRepository;
import vn.gaspro.api.repository.OrderRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import vn.gaspro.api.service.CustomerService;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerVatInfoRepository customerVatInfoRepository;
    private final OrderRepository orderRepository;
    private final CustomerMapper customerMapper;
    private final OrderMapper orderMapper;

    @Override
    @Transactional(readOnly = true)
    public CustomerProfileResponse getMyProfile() {
        String phone = SecurityContextHolder.getContext().getAuthentication().getName();
        Customer customer = customerRepository.findByPhone(phone)
                .orElseThrow(() -> new AppException(ErrorCode.CUSTOMER_NOT_EXISTED));

        CustomerProfileResponse response = customerMapper.toProfileResponse(customer);
        response.setDebtStatusLabel(getDebtStatusLabel(customer.getDebtStatus(), customer.getCreditLimit()));
        return response;
    }

    @Override
    @Transactional
    public VatInfoResponse upsertMyVatInfo(VatInfoRequest request) {
        String phone = SecurityContextHolder.getContext().getAuthentication().getName();
        Customer customer = customerRepository.findByPhone(phone)
                .orElseThrow(() -> new AppException(ErrorCode.CUSTOMER_NOT_EXISTED));

        CustomerVatInfo vatInfo = customer.getVatInfo();
        if (vatInfo == null) {
            vatInfo = new CustomerVatInfo();
            vatInfo.setCustomer(customer);
            customer.setVatInfo(vatInfo);
        }

        vatInfo.setTaxCode(request.getTaxCode());
        vatInfo.setCompanyName(request.getCompanyName());
        vatInfo.setInvoiceAddress(request.getInvoiceAddress());

        customerVatInfoRepository.save(vatInfo);

        return customerMapper.toVatInfoResponse(vatInfo);
    }

    @Override
    @Transactional
    public void deleteMyVatInfo() {
        String phone = SecurityContextHolder.getContext().getAuthentication().getName();
        Customer customer = customerRepository.findByPhone(phone)
                .orElseThrow(() -> new AppException(ErrorCode.CUSTOMER_NOT_EXISTED));

        if (customer.getVatInfo() != null) {
            customerVatInfoRepository.delete(customer.getVatInfo());
            customer.setVatInfo(null);
            customerRepository.save(customer);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CustomerSearchResponse> searchCustomers(String query, Pageable pageable) {
        Specification<Customer> spec = (root, cq, cb) -> {
            if (query == null || query.isBlank()) return cb.conjunction();
            String likeQuery = "%" + query.toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("phone")), likeQuery),
                    cb.like(cb.lower(root.get("contactName")), likeQuery)
            );
        };

        return customerRepository.findAll(spec, pageable).map(customer -> {
            CustomerSearchResponse response = CustomerSearchResponse.builder()
                    .customerId(customer.getId())
                    .contactName(customer.getContactName())
                    .phone(customer.getPhone())
                    .customerType(customer.getCustomerType().name())
                    .deliveryAddress(customer.getDeliveryAddress())
                    .debtStatus(customer.getDebtStatus().name())
                    .build();
                    
            // Load order history
            var orders = orderRepository.findAll((root, cq, cb) -> cb.equal(root.get("customer"), customer));
            response.setOrderHistory(orders.stream().map(orderMapper::toResponse).collect(Collectors.toList()));
            
            return response;
        });
    }

    private String getDebtStatusLabel(DebtStatus status, java.math.BigDecimal creditLimit) {
        return switch (status) {
            case ELIGIBLE -> "Được phép ghi nợ (Hạn mức " + creditLimit + ")";
            case INELIGIBLE -> "Chưa đủ thâm niên hoặc sản lượng";
            case EXCEEDED_LIMIT -> "Vượt hạn mức nợ";
            case OVERDUE_LOCKED -> "Khóa nợ do nợ quá hạn";
            default -> "Không xác định";
        };
    }
}
