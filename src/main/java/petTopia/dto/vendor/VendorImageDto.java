package petTopia.dto.vendor;

import lombok.*;
import petTopia.model.vendor.VendorImages;

import static petTopia.constant.ImageUrl.VENDOR_IMG_URL_PREFIX;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VendorImageDto {
    private Integer id;
    private String imgUrl;

    public static VendorImageDto fromEntity(VendorImages image) {
        String imgUrl = VENDOR_IMG_URL_PREFIX.replace("{id}", String.valueOf(image.getId()));

        return VendorImageDto.builder()
                .id(image.getId())
                .imgUrl(imgUrl)
                .build();
    }
}
