package petTopia.controller.vendor;

import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import petTopia.dto.vendor.ActivityDto;
import petTopia.dto.vendor.ActivityImageDto;
import petTopia.model.vendor.ActivityType;
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
    public ResponseEntity<List<ActivityDto>> getAllActivities() {
        List<ActivityDto> activities = vendorActivityService.findAllActivities();
        return ResponseEntity.ok(activities);
    }

    @GetMapping("/{activityId}")
    public ResponseEntity<ActivityDto> getActivity(@PathVariable Integer activityId) {
        ActivityDto activity = vendorActivityService.findById(activityId);
        return ResponseEntity.ok(activity);
    }

    @GetMapping("/all/except/{activityId}")
    public ResponseEntity<List<ActivityDto>> getAllActivitiesExceptOne(@PathVariable Integer activityId) {
        List<ActivityDto> activities = vendorActivityService.findAllActivityExceptOne(activityId);
        return ResponseEntity.ok(activities);
    }

    @GetMapping("/{activityId}/image")
    public ResponseEntity<List<ActivityImageDto>> getActivityImages(@PathVariable Integer activityId) {
        List<ActivityImageDto> images = vendorActivityImagesService.findImagesByActivityId(activityId);
        return ResponseEntity.ok(images);
    }

    @GetMapping("/type/{typeId}")
    public ResponseEntity<List<ActivityDto>> getActivitiesByType(@PathVariable Integer typeId) {
        List<ActivityDto> activities = vendorActivityService.findActivityByTypeId(typeId);
        return ResponseEntity.ok(activities);
    }

    @GetMapping("/type/{typeId}/except/activity/{activityId}")
    public ResponseEntity<List<ActivityDto>> getActivitiesByCategoryExceptOne(@PathVariable Integer typeId,
                                                                              @PathVariable Integer activityId) {
        List<ActivityDto> activities = vendorActivityService.findActivityByTypeIdExceptOne(typeId, activityId);
        return ResponseEntity.ok(activities);
    }

    @PostMapping("/find")
    public ResponseEntity<List<ActivityDto>> getActivitiesByKeyword(@RequestBody Map<String, String> data) {
        String keyword = data.get("keyword");
        List<ActivityDto> activities = vendorActivityService.findByNameOrDescription(keyword);
        return ResponseEntity.ok(activities);
    }

    @GetMapping("/type/show")
    public ResponseEntity<List<ActivityType>> getAllActivityTypes() {
        List<ActivityType> types= activityTypeUserService.findAllActivityType();
        return ResponseEntity.ok(types);
    }

    @GetMapping("/{activityId}/increase/number/visitor")
    public ResponseEntity<ActivityDto> increaseNumberOfVisitor(@PathVariable Integer activityId) {
        ActivityDto activity = vendorActivityService.increaseNumberOfVisitor(activityId);
        return ResponseEntity.ok(activity);
    }

    @GetMapping("/vendor/{vendorId}")
    public ResponseEntity<List<ActivityDto>> getActivitiesByVendorId(@PathVariable Integer vendorId) {
        List<ActivityDto> activities = vendorActivityService.findByVendorId(vendorId);
        return ResponseEntity.ok(activities);
    }

    @GetMapping(path = "/img/{id}", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> getActivityImage(@PathVariable Integer id) {
        byte[] activityImg = vendorActivityImagesService.findById(id);
        return activityImg != null
                ? ResponseEntity.ok(activityImg)
                : ResponseEntity.notFound().build();
    }
}
