package vn.gaspro.api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.gaspro.api.dto.request.SupplierPayRequest;
import vn.gaspro.api.dto.response.ApiResponse;
import vn.gaspro.api.dto.response.SupplierDebtHistoryResponse;
import vn.gaspro.api.service.SupplierService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/suppliers")
@RequiredArgsConstructor
public class SupplierController {

    private final SupplierService supplierService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<vn.gaspro.api.dto.response.SupplierResponse>>> getAllSuppliers() {
        return ResponseEntity.ok(ApiResponse.success(supplierService.getAllSuppliers()));
    }

    @PostMapping("/{id}/pay")
    public ResponseEntity<ApiResponse<SupplierDebtHistoryResponse>> payDebt(
            @PathVariable Long id,
            @Valid @RequestBody SupplierPayRequest request) {
        return ResponseEntity.ok(ApiResponse.success(supplierService.payDebt(id, request)));
    }
}
