package petTopia.dto.vendor;

import java.util.Date;
import java.util.List;

import lombok.*;
import petTopia.model.vendor.Vendor;

import static petTopia.constant.ImageUrl.LOGO_IMG_URL_PREFIX;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VendorDto {
    private Integer id;
    private String name;
    private String description;
    private String address;
    private String phone;
    private String contactEmail;
    private String contactPerson;
    private String taxIdNumber;
    private Boolean status;
    private Date registrationDate = new Date();
    private Date updatedDate = new Date();
    private Integer eventCount = 0;
    private Float totalRating = 0.0f;
    private Integer reviewCount = 0;
    private String vendorLevel = "普通";
    private Float avgRatingEnvironment = 0.0f;
    private Float avgRatingPrice = 0.0f;
    private Float avgRatingService = 0.0f;
    private List<ActivityDto> activityDtoList;

    private Integer categoryId;
    private String categoryName;
    private String logoImgUrl;

    public static VendorDto fromEntity(Vendor vendor) {
        String imgUrl = LOGO_IMG_URL_PREFIX.replace("{vendor}", String.valueOf(vendor.getId()));

        return VendorDto.builder()
                .id(vendor.getId())
                .name(vendor.getName())
                .description(vendor.getDescription())
                .address(vendor.getAddress())
                .phone(vendor.getPhone())
                .contactEmail(vendor.getContactEmail())
                .taxIdNumber(vendor.getTaxIdNumber())
                .status(vendor.isStatus())
                .registrationDate(vendor.getRegistrationDate())
                .updatedDate(vendor.getUpdatedDate())
                .eventCount(vendor.getEventCount())
                .totalRating(vendor.getTotalRating())
                .reviewCount(vendor.getReviewCount())
                .vendorLevel(vendor.getVendorLevel())
                .avgRatingEnvironment(vendor.getAvgRatingEnvironment())
                .avgRatingPrice(vendor.getAvgRatingPrice())
                .avgRatingService(vendor.getAvgRatingService())
                .categoryId(vendor.getVendorCategory().getId())
                .categoryName(vendor.getVendorCategory().getName())
                .logoImgUrl(imgUrl)
                .build();
    }
}
