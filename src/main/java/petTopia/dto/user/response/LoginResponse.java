package petTopia.dto.user.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import petTopia.model.user.User;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LoginResponse {
    private String message;
    private String token;
    private Integer adminId;
    private String email;
    private String role;
    private User user;
    private User loggedInUser;
    private String provider;
    private User.UserRole userRole;
    private Integer userId;
    private Integer memberId;
    private String memberName;
    private String name;
    private Boolean success;
    private String error;
    private Boolean isAuthenticated;
    private Boolean isThirdPartyAccount;
    private Boolean needVerification;
}
