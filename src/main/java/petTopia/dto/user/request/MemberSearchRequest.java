package petTopia.dto.user.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberSearchRequest {
    private int page = 0;
    private int size = 10;
    private String keyword;
    private String status;
    private String email;
}
