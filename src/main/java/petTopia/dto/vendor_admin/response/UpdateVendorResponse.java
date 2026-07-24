package petTopia.dto.vendor_admin.response;

import lombok.*;
import petTopia.model.vendor.Vendor;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateVendorResponse {
    private Boolean success;
    private Vendor vendor;
}
