package petTopia.dto.user.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginStatus {
    private Boolean isLoggedIn;
    private Integer userId;
    private String email;
    private String name;
    private String memberName;
    private String role;
    private String provider;
}
