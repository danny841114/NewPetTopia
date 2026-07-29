package petTopia.dto.admin;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetCouponsRequest {
    private Integer page = 0;
    private Integer size = 10;
    private String keyword;
    private String status;
}
