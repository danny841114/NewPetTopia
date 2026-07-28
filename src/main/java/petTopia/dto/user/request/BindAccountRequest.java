package petTopia.dto.user.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BindAccountRequest {
    private Integer localUserId;
    private String provider;
    private String email;
}
