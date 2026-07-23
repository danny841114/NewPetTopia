package petTopia.dto.vendor_admin;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopActivityDTO {
    private Integer activityId;
    private String activityName;
    private Long registrationCount;
    private String description;
}
