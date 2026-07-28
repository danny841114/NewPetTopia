package petTopia.dto.user.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Oauth2LoginResponse {
    private Boolean success;
    private String token;
    private Integer userId;
    private String email;
    private String name;
    private String memberName;
    private String role;
    private String provider;
    private String picture;
    private String avatar;
    private Boolean hasVendorAccount;
    private String message;
}
