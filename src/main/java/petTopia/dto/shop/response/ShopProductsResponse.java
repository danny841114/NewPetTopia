package petTopia.dto.shop.response;

import lombok.*;
import petTopia.model.shop.Product;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShopProductsResponse {
    private Long count;
    private List<Product> productList;
}
