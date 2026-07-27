package petTopia.dto.vendor.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddReviewStarRequest {
    private Integer memberId;
    private Integer ratingEnv;
    private Integer ratingPrice;
    private Integer ratingService;
}
