package petTopia.controller.vendor;

import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import petTopia.dto.vendor.ActivityImageDto;
import petTopia.model.vendor.ActivityType;
import petTopia.model.vendor.VendorActivity;
import petTopia.model.vendor.VendorActivityImages;
import petTopia.service.vendor.ActivityTypeUserService;
import petTopia.service.vendor.VendorActivityImagesService;
import petTopia.service.vendor.VendorActivityService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/activity")
public class ActivityController {
    private final VendorActivityService vendorActivityService;
    private final VendorActivityImagesService vendorActivityImagesService;
    private final ActivityTypeUserService activityTypeUserService;

    @GetMapping("/all")
    public ResponseEntity<List<VendorActivity>> getAllActivities() {
        List<VendorActivity> activityList = vendorActivityService.findAllActivity();
        return ResponseEntity.ok(activityList);
    }

    @GetMapping("/{activityId}")
    public ResponseEntity<VendorActivity> getActivityDetail(@PathVariable Integer activityId) {
        VendorActivity activity = vendorActivityService.findActivityById(activityId);
        return ResponseEntity.ok(activity);
    }

    @GetMapping("/all/except/{activityId}")
    public ResponseEntity<List<VendorActivity>> getAllActivitiesExceptOne(@PathVariable Integer activityId) {
        List<VendorActivity> activityList = vendorActivityService.findAllActivityExceptOne(activityId);
        return ResponseEntity.ok(activityList);
    }

    @GetMapping("/{activityId}/image")
    public ResponseEntity<List<ActivityImageDto>> getActivityImages(@PathVariable Integer activityId) {
        List<ActivityImageDto> images = vendorActivityImagesService.findImagesByActivityId(activityId);
        return ResponseEntity.ok(images);
    }

    @GetMapping("/type/{typeId}")
    public ResponseEntity<List<VendorActivity>> getActivitiesByType(@PathVariable Integer typeId) {
        List<VendorActivity> activityList = vendorActivityService.findActivityByTypeId(typeId);
        return ResponseEntity.ok(activityList);
    }

    @GetMapping("/type/{typeId}/except/activity/{activityId}")
    public ResponseEntity<List<VendorActivity>> getActivitiesByCategoryExceptOne(@PathVariable Integer typeId,
                                                                                 @PathVariable Integer activityId) {
        List<VendorActivity> activityList = vendorActivityService.findActivityByTypeIdExceptOne(typeId, activityId);
        return ResponseEntity.ok(activityList);
    }

    @PostMapping("/find")
    public ResponseEntity<List<VendorActivity>> getActivitiesByKeyword(@RequestBody Map<String, String> data) {
        String keyword = data.get("keyword");
        List<VendorActivity> activityList = vendorActivityService.findVendorByNameOrDescription(keyword);
        return ResponseEntity.ok(activityList);
    }

    @GetMapping("/type/show")
    public ResponseEntity<List<ActivityType>> getAllTypes() {
        List<ActivityType> typeList = activityTypeUserService.findAllActivityType();
        return ResponseEntity.ok(typeList);
    }

    @GetMapping("/{activityId}/increase/number/visitor")
    public ResponseEntity<VendorActivity> increaseNumberOfVisitor(@PathVariable Integer activityId) {
        VendorActivity activity = vendorActivityService.increaseNumberOfVisitor(activityId);
        return ResponseEntity.ok(activity);
    }

    @GetMapping("/vendor/{vendorId}")
    public ResponseEntity<List<VendorActivity>> getActivitiesByVendorId(@PathVariable Integer vendorId) {
        List<VendorActivity> activityList = vendorActivityService.findActivityListByVendorId(vendorId);
        return ResponseEntity.ok(activityList);
    }

    @GetMapping(path = "/img/{id}", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> getActivityImage(@PathVariable Integer id) {
        byte[] imageByteArray = vendorActivityImagesService.findById(id);
        return ResponseEntity.ok(imageByteArray);
    }
}
