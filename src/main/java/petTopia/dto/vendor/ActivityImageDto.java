package petTopia.dto.vendor;

import lombok.*;
import petTopia.model.vendor.VendorActivityImages;

import static petTopia.constant.ImageUrl.ACTIVITY_IMG_URL_PREFIX;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityImageDto {
    private Integer id;
    private String imgUrl;

    public static ActivityImageDto fromEntity(VendorActivityImages image) {
        String imgUrl = ACTIVITY_IMG_URL_PREFIX.replace("{id}", String.valueOf(image.getId()));

        return ActivityImageDto.builder()
                .id(image.getId())
                .imgUrl(imgUrl)
                .build();
    }
}
