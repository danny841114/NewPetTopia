package petTopia.dto.vendor;

import lombok.*;
import petTopia.model.vendor.FriendlyShop;
import petTopia.model.vendor.VendorCategory;

import java.math.BigDecimal;

import static petTopia.constant.ImageUrl.LOGO_IMG_URL_PREFIX;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FriendlyShopDto {
    private Integer id;
    private String name;
    private String address;
    private BigDecimal longitude;
    private BigDecimal latitude;
    private VendorCategory vendorCategory;
    private String logoImgUrl;


    public static FriendlyShopDto fromEntity(FriendlyShop shop) {
        if (shop.getVendor() == null) return null;

        String imgUrl = LOGO_IMG_URL_PREFIX.replace("{vendorId}", String.valueOf(shop.getVendor().getId()));

        return FriendlyShopDto.builder()
                .id(shop.getId())
                .name(shop.getName())
                .address(shop.getAddress())
                .longitude(shop.getLongitude())
                .latitude(shop.getLatitude())
                .vendorCategory(shop.getVendorCategory())
                .logoImgUrl(imgUrl)
                .build();
    }
}
