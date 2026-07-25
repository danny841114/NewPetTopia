package petTopia.dto.user.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private String message;
    private String token;
    private Integer adminId;
    private String email;
    private String role;
    private Boolean isAuthenticated;
}
