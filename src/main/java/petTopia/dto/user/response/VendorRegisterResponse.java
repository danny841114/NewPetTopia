package petTopia.dto.user.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VendorRegisterResponse {
    private Boolean success;
    private String message;
    private Integer userId;
    private Integer vendorId;
}
