package petTopia.dto.vendor_admin.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;
import petTopia.model.vendor.ActivityType;

import java.util.Date;

@Getter
@Setter
public class AddActivityRequest {
    private Integer vendorId;
    private String activityName;
    private ActivityType typeId;
    private String description;
    private String address;
    private String isRegistrationRequired;
    private Integer maxParticipants;
    private MultipartFile[] files;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private Date startTime;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private Date endTime;
}
