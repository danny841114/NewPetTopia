package petTopia.dto.vendor_admin.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class UpdateCalendarEventRequest {
    private String eventTitle;
    private String color;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private Date startTime;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private Date endTime;
}
