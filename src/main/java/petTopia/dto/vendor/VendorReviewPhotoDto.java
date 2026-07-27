package petTopia.dto.vendor;

import lombok.*;
import petTopia.model.vendor.ReviewPhoto;

import static petTopia.constant.ImageUrl.VENDOR_REVIEW_IMG_URL_PREFIX;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VendorReviewPhotoDto {
    private Integer id;
    private String imgUrl;

    public static VendorReviewPhotoDto fromEntity(ReviewPhoto photo) {
        String imgUrl = VENDOR_REVIEW_IMG_URL_PREFIX.replace("{id}", String.valueOf(photo.getId()));

        return VendorReviewPhotoDto.builder()
                .id(photo.getId())
                .imgUrl(imgUrl)
                .build();
    }
}
