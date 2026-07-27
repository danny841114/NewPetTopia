package petTopia.dto.vendor;

import lombok.*;
import petTopia.model.vendor.ActivityType;
import petTopia.model.vendor.VendorActivity;

import java.util.Date;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityDto {
    private Integer id;
    private String name;
    private String description;
    private String address;
    private Date startTime;
    private Date endTime;
    private Date registrationDate;
    private Integer numberVisitor;
    private Boolean registrationRequired;
    private ActivityType activityType;
    private VendorDetail vendor;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VendorDetail{
        public Integer id;
        public String name;
    }

    public static ActivityDto fromEntity(VendorActivity activity) {
        if (activity == null) {
            return null;
        }

        VendorDetail vendorDetail = null;
        if (activity.getVendor() != null) {
            vendorDetail = VendorDetail.builder()
                    .id(activity.getVendor().getId())
                    .name(activity.getVendor().getName())
                    .build();
        }

        return ActivityDto.builder()
                .id(activity.getId())
                .name(activity.getName())
                .description(activity.getDescription())
                .address(activity.getAddress())
                .startTime(activity.getStartTime())
                .endTime(activity.getEndTime())
                .registrationDate(activity.getRegistrationDate())
                .numberVisitor(activity.getNumberVisitor())
                .registrationRequired(activity.isRegistrationRequired())
                .activityType(activity.getActivityType())
                .vendor(vendorDetail)
                .build();
    }
}
