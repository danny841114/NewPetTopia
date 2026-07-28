package petTopia.dto.user.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BindAccountResponse {
    private Boolean success;
    private String message;
    private String token;
    private Integer userId;
    private String email;
    private String role;
    private String provider;
}
