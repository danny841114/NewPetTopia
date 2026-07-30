package petTopia.dto.shop.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConfirmProductRequest {
    private Integer memberId;
    private Integer productDetailId;
    private Integer productSizeId;
    private Integer productColorId;
}
