package petTopia.dto.user.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VendorEligibilityResponse {
    private Boolean eligible;
    private Boolean hasExistingAccount;
    private String message;
}
