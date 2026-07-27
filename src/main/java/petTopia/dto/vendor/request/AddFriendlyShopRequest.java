package petTopia.dto.vendor.request;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddFriendlyShopRequest {
    private String name;
    private String address;
    private Integer categoryId;
}
