package vn.gaspro.api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vn.gaspro.api.dto.response.ApiResponse;
import vn.gaspro.api.dto.request.VatInfoRequest;
import vn.gaspro.api.dto.response.CustomerProfileResponse;
import vn.gaspro.api.dto.response.CustomerSearchResponse;
import vn.gaspro.api.dto.response.VatInfoResponse;
import vn.gaspro.api.service.CustomerService;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping("/me/profile")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<CustomerProfileResponse>> getMyProfile() {
        return ResponseEntity.ok(ApiResponse.success(customerService.getMyProfile(), "Lấy thông tin hồ sơ khách hàng thành công"));
    }

    @PutMapping("/me/vat-info")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<VatInfoResponse>> upsertMyVatInfo(@RequestBody @Valid VatInfoRequest request) {
        return ResponseEntity.ok(ApiResponse.success(customerService.upsertMyVatInfo(request), "Cập nhật thông tin VAT thành công!"));
    }

    @DeleteMapping("/me/vat-info")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<Void>> deleteMyVatInfo() {
        customerService.deleteMyVatInfo();
        return ResponseEntity.ok(ApiResponse.success(null, "Đã xóa thông tin đăng ký VAT thành công!"));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('OPERATOR', 'ADMIN')")
    public ResponseEntity<ApiResponse<Page<CustomerSearchResponse>>> searchCustomers(
            @RequestParam(required = false) String query,
            Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(customerService.searchCustomers(query, pageable), "Tìm kiếm khách hàng thành công"));
    }
}
