package vn.gaspro.api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import vn.gaspro.api.dto.response.CartItemResponse;
import vn.gaspro.api.entity.CartItem;

@Mapper(componentModel = "spring")
public interface CartMapper {

    @Mapping(target = "cartItemId", source = "id")
    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productName", source = "product.name")
    @Mapping(target = "unitPrice", source = "product.price")
    @Mapping(target = "unitDepositFee", ignore = true)
    @Mapping(target = "subtotal", ignore = true)
    CartItemResponse toResponse(CartItem cartItem);
}
