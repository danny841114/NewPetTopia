package petTopia.dto.vendor_admin.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class BulkVendorStatusRequest {
    private List<Integer> vendorIds;
    private boolean status;
}
