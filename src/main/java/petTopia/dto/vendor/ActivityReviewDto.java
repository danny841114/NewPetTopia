package petTopia.dto.vendor;

import lombok.*;
import petTopia.model.vendor.VendorActivityReview;

import java.util.Date;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityReviewDto {
    private Integer id;
    private Integer memberId;
    private Date reviewTime;
    private String reviewContent;
    private VendorDetail vendor;
    private ActivityDetail activity;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VendorDetail {
        private Integer id;
        private String name;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ActivityDetail {
        private Integer id;
        private String name;
    }

    public static ActivityReviewDto fromEntity(VendorActivityReview review) {
        VendorDetail vendorDetail = VendorDetail.builder()
                .id(review.getVendor().getId())
                .name(review.getVendor().getName())
                .build();

        ActivityDetail activityDetail = ActivityDetail.builder()
                .id(review.getVendorActivity().getId())
                .name(review.getVendorActivity().getName())
                .build();

        return ActivityReviewDto.builder()
                .id(review.getId())
                .memberId(review.getMemberId())
                .reviewContent(review.getReviewContent())
                .reviewTime(review.getReviewTime())
                .reviewContent(review.getReviewContent())
                .vendor(vendorDetail)
                .activity(activityDetail)
                .build();
    }
}
