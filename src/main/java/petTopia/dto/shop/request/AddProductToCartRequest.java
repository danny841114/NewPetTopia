package petTopia.dto.shop.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddProductToCartRequest {
    private Integer memberId;
    private Integer productDetailId;
    private Integer productSizeId;
    private Integer productColorId;
    private Integer quantity;
}
