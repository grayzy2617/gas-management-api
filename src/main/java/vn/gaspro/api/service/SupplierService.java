package vn.gaspro.api.service;

import vn.gaspro.api.dto.request.SupplierPayRequest;
import vn.gaspro.api.dto.response.SupplierDebtHistoryResponse;
import vn.gaspro.api.dto.response.SupplierResponse;

import java.util.List;

public interface SupplierService {
    List<SupplierResponse> getAllSuppliers();
    SupplierDebtHistoryResponse payDebt(Long supplierId, SupplierPayRequest request);
}
