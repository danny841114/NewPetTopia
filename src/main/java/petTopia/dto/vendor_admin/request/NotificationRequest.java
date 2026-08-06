package petTopia.dto.vendor_admin.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NotificationRequest {
    private Integer activityId;
    private String title;
    private String content;
}
