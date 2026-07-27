package petTopia.dto.vendor.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddActivityReviewRequest {
    private Integer memberId;
    private String content;
}
