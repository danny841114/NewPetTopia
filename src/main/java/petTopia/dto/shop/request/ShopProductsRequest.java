package petTopia.dto.shop.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ShopProductsRequest {
    private Integer start = 0;
    private Integer rows = 10;
    private String keyword;
    private String category;
    private String status;
    private String isProductDiscount;
    private String stockQuantityLessThan;
    private String startDate;
    private String endDate;
}
