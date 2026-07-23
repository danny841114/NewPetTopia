package petTopia.dto.vendor_admin.response;

import lombok.*;
import petTopia.model.vendor.ActivityPeopleNumber;
import petTopia.model.vendor.ActivityType;
import petTopia.model.vendor.VendorActivity;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityDetailResponse {
    private VendorActivity activity;
    private List<Integer> activityImageIds;
    private ActivityPeopleNumber activityPeopleNumber;
    private List<ActivityType> activityTypes;
    private List<Map<String, Object>> registrationOptions;
}
