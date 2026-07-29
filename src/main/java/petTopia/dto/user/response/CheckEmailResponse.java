package petTopia.dto.user.response;

import lombok.*;
import petTopia.model.user.User;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckEmailResponse {
    private Boolean exists;
    private String message;
    private User.UserRole userRole;
    private User. Provider provider;
}
