package petTopia.dto.vendor;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VendorImageDto {
    private Integer id;
    private String imgUrl;
}
