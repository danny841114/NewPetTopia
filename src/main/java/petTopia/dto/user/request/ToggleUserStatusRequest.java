package petTopia.dto.user.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ToggleUserStatusRequest {
    private Boolean isActive;
}
