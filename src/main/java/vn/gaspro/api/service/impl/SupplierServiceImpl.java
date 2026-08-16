package vn.gaspro.api.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.gaspro.api.dto.request.SupplierPayRequest;
import vn.gaspro.api.dto.response.SupplierDebtHistoryResponse;
import vn.gaspro.api.dto.response.SupplierResponse;
import vn.gaspro.api.entity.Supplier;
import vn.gaspro.api.entity.SupplierDebtHistory;
import vn.gaspro.api.mapper.SupplierMapper;
import vn.gaspro.api.repository.SupplierDebtHistoryRepository;
import vn.gaspro.api.repository.SupplierRepository;
import vn.gaspro.api.service.SupplierService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SupplierServiceImpl implements SupplierService {

    private final SupplierRepository supplierRepository;
    private final SupplierDebtHistoryRepository supplierDebtHistoryRepository;
    private final SupplierMapper supplierMapper;

    @Override
    public List<SupplierResponse> getAllSuppliers() {
        return supplierRepository.findAll().stream()
                .map(supplierMapper::toSupplierResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public SupplierDebtHistoryResponse payDebt(Long supplierId, SupplierPayRequest request) {
        Supplier supplier = supplierRepository.findById(supplierId)
                .orElseThrow(() -> new vn.gaspro.api.exception.AppException(vn.gaspro.api.enums.ErrorCode.SUPPLIER_NOT_EXISTED));

        if (request.getAmount().compareTo(supplier.getDebtBalance()) > 0) {
            throw new vn.gaspro.api.exception.AppException(vn.gaspro.api.enums.ErrorCode.PAYMENT_AMOUNT_EXCEEDS_DEBT);
        }

        // Trừ công nợ
        supplier.setDebtBalance(supplier.getDebtBalance().subtract(request.getAmount()));
        supplier = supplierRepository.save(supplier);

        // Lưu lịch sử thanh toán
        String createdBy = SecurityContextHolder.getContext().getAuthentication().getName();

        SupplierDebtHistory history = SupplierDebtHistory.builder()
                .supplier(supplier)
                .amountPaid(request.getAmount())
                .remainingDebt(supplier.getDebtBalance())
                .note(request.getNote())
                .createdBy(createdBy)
                .build();

        history = supplierDebtHistoryRepository.save(history);

        return supplierMapper.toSupplierDebtHistoryResponse(history);
    }
}
