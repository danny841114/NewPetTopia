package petTopia.dto.shop.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OptionProductRequest {
    private Integer productDetailId;
    private Integer optionId;
    private String optionName;
}
