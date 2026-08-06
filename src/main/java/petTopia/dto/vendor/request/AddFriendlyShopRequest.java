package petTopia.dto.vendor.request;

import lombok.*;
import petTopia.model.vendor.Vendor;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddFriendlyShopRequest {
    private String name;
    private String address;
    private Integer categoryId;

    public static AddFriendlyShopRequest fromEntity(Vendor vendor) {
        return AddFriendlyShopRequest.builder()
                .name(vendor.getName())
                .address(vendor.getAddress())
                .categoryId(vendor.getVendorCategory() != null ? vendor.getVendorCategory().getId() : null)
                .build();
    }
}
