package petTopia.dto.vendor;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityDto {
    private Integer activityId;
    private String activityName;
    private String activityDescription;
}
