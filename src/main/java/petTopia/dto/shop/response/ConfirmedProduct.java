package petTopia.dto.shop.response;

import lombok.*;
import petTopia.model.shop.Product;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfirmedProduct {
    private Integer productQuantityInCart;
    private Product product;
}
