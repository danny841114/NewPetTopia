package petTopia.dto.vendor_admin.request;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class AddReviewRequest {
    private Integer id;
    private Integer vendorId;
    private Integer memberId;
    private Date reviewTime;
    private String reviewContent;
    private Integer ratingEnvironment;
    private Integer ratingPrice;
    private Integer ratingService;
}
