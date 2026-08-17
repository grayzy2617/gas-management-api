package vn.gaspro.api.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.gaspro.api.dto.request.VatInfoRequest;
import vn.gaspro.api.dto.response.CustomerProfileResponse;
import vn.gaspro.api.dto.response.CustomerSearchResponse;
import vn.gaspro.api.dto.response.VatInfoResponse;

public interface CustomerService {
    CustomerProfileResponse getMyProfile();
    VatInfoResponse upsertMyVatInfo(VatInfoRequest request);
    void deleteMyVatInfo();
    Page<CustomerSearchResponse> searchCustomers(String query, Pageable pageable);
}
