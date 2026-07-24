package petTopia.dto.vendor_admin.response;

import lombok.*;
import petTopia.model.vendor.Vendor;
import petTopia.model.vendor.VendorCategory;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VendorProfile {
    private Vendor vendor;
    private List<VendorCategory> allcategory; // need to modify
    private String vendorLogoImgBase64;
    private Integer activityCount;
}
