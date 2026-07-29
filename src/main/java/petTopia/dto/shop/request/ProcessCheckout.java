package petTopia.dto.shop.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ProcessCheckout {
    private Integer couponId;
    private Integer shippingCategoryId;
    private Integer paymentCategoryId;
    private List<CartItem> cartItems;
    private String receiverName;
    private String receiverPhone;
    private String street;
    private String city;
    private String paymentAmount;

    @Getter
    @Setter
    public static class CartItem {
        private Integer productId;
    }
}
