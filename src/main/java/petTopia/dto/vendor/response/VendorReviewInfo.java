package petTopia.dto.vendor.response;

import lombok.*;
import petTopia.model.vendor.ReviewPhoto;
import petTopia.model.vendor.VendorReview;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VendorReviewInfo {
    private Integer id;
    private String content;
    private Date time;
    private Integer ratingEnv;
    private Integer ratingPrice;
    private Integer ratingService;
    private List<String> photoUrls;
    private MemberInfo member;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MemberInfo {
        private Integer id;
        private String name;
        private String photoUrl;
    }

    public static VendorReviewInfo fromEntity(VendorReview review) {
        Integer memberId = review.getMember().getId();
        VendorReviewInfo.MemberInfo memberInfo = MemberInfo.builder()
                .id(memberId)
                .name(review.getMember().getName())
                .photoUrl("/api/member/" + memberId + "/photo")
                .build();

        String endpoint = "/api/vendor/" + review.getVendor().getId() + "/review/" + review.getId() + "/photo/";
        List<String> photoUrls = review.getReviewPhotos()
                .stream()
                .map(photo -> endpoint + photo.getId())
                .toList();

        return VendorReviewInfo.builder()
                .id(review.getId())
                .time(review.getReviewTime())
                .content(review.getReviewContent())
                .ratingEnv(review.getRatingEnvironment())
                .ratingPrice(review.getRatingPrice())
                .ratingService(review.getRatingService())
                .photoUrls(photoUrls)
                .member(memberInfo)
                .build();
    }
}
