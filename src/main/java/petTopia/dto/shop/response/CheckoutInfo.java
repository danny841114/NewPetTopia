package petTopia.dto.shop.response;

import lombok.*;
import petTopia.model.shop.Cart;
import petTopia.model.shop.PaymentCategory;
import petTopia.model.shop.ShippingCategory;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckoutInfo {
    private List<Cart> cartItems;
    private BigDecimal subtotal;
    private List<ShippingCategory> shippingCategories;
    private List<PaymentCategory> paymentCategories;
}
