package vn.gaspro.api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import vn.gaspro.api.dto.response.CustomerProfileResponse;
import vn.gaspro.api.dto.response.VatInfoResponse;
import vn.gaspro.api.entity.Customer;
import vn.gaspro.api.entity.CustomerVatInfo;

@Mapper(componentModel = "spring")
public interface CustomerMapper {
    
    @Mapping(target = "customerId", source = "id")
    @Mapping(target = "debtStatusLabel", ignore = true)
    CustomerProfileResponse toProfileResponse(Customer customer);
    
    VatInfoResponse toVatInfoResponse(CustomerVatInfo vatInfo);
}
