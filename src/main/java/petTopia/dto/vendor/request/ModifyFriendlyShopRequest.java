package petTopia.dto.vendor.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ModifyFriendlyShopRequest {
    private String name;
    private String address;
    private Integer categoryId;
}
