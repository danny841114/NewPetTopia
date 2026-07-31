package petTopia.dto.shop.response;

import lombok.*;
import petTopia.dto.shop.ProductDetailDto;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShopProductResponse {
    private Long count;
    private List<ProductDetailDto> productDetailDtoList;
}
