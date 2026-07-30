package petTopia.dto.shop.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderOptions {
    private List<String> paymentStatusList;
    private List<String> orderStatusList;
    private List<String> paymentCategoryList;
    private List<String> shippingCategoryList;
}
