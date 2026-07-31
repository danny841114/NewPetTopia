package petTopia.dto.shop.response;

import lombok.*;
import petTopia.model.shop.Coupon;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CouponResponse {
    private List<Coupon> available;
    private List<Coupon> expired;
    private List<Coupon> notMeet;
}
