package petTopia.dto.shop.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import petTopia.model.shop.Product;
import petTopia.model.shop.ProductColor;
import petTopia.model.shop.ProductDetail;
import petTopia.model.shop.ProductSize;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductDetailResponse {
    private List<Product> productList;
    private List<ProductSize> sizeList;
    private List<ProductColor> colorList;
    private Product minPriceProduct;
    private Product maxPriceProduct;
    private ProductDetail productDetail;
    private Integer totalStockQuantity;
}
