package petTopia.dto.user.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Oauth2LoginRequest {
    private String email;
    private String name;
    private String provider;
    private String picture; // type not sure
    private Boolean hasVendorAccount; // type not sure
}
