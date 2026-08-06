package petTopia.dto.vendor;

import lombok.*;
import petTopia.model.vendor.VendorActivity;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityDetail {
    private Integer activityId;
    private String activityName;
    private String activityDescription;

    public static ActivityDetail fromEntity(VendorActivity activity) {
        return ActivityDetail.builder()
                .activityId(activity.getId())
                .activityName(activity.getName())
                .activityDescription(activity.getDescription())
                .build();
    }
}
