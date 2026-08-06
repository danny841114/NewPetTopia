package petTopia.controller.vendor_admin;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import petTopia.dto.vendor_admin.TopActivityDTO;
import petTopia.dto.vendor_admin.request.AddActivityRequest;
import petTopia.dto.vendor_admin.request.UpdateActivityRequest;
import petTopia.dto.vendor_admin.response.ActivityDetailResponse;
import petTopia.model.vendor.ActivityType;
import petTopia.model.vendor.CalendarEvent;
import petTopia.model.vendor.VendorActivity;
import petTopia.service.vendor_admin.ActivityTypeService;
import petTopia.service.vendor_admin.VendorActivityServiceAdmin;
import petTopia.util.HeadersUtil;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/vendor_admin")
public class VendorActivityController {
    private final VendorActivityServiceAdmin vendorActivityServiceAdmin;
    private final ActivityTypeService activityTypeService;

    @GetMapping("/activity/top5")
    public ResponseEntity<List<TopActivityDTO>> getTop5Activities() {
        List<TopActivityDTO> topActivities = vendorActivityServiceAdmin.getTop5Activities();
        return ResponseEntity.ok(topActivities);
    }

    // TODO: should modify response body
    @GetMapping("/vendor_admin_activityDetail")
    public ResponseEntity<ActivityDetailResponse> getVendorActivityDetail(@RequestParam Integer id) {
        ActivityDetailResponse activityDetail = vendorActivityServiceAdmin.getActivityDetail(id);
        return activityDetail != null
                ? ResponseEntity.ok(activityDetail)
                : ResponseEntity.notFound().build();
    }

    @GetMapping("/activity/allTypes")
    public ResponseEntity<List<ActivityType>> getAllActivityTypes() {
        List<ActivityType> types = activityTypeService.getAllActivityTypes();
        return ResponseEntity.ok(types);
    }

    @GetMapping("/activity/checkConflictDetail")
    public ResponseEntity<Boolean> checkActivityTimeConflictDetail(@RequestParam Integer vendorId,
                                                                   @RequestParam Integer activityId,
                                                                   @RequestParam String startTime,
                                                                   @RequestParam String endTime) {
        boolean conflictExists = vendorActivityServiceAdmin.checkTimeConflictDetail(vendorId, activityId, startTime, endTime);
        return ResponseEntity.ok(conflictExists);
    }

    @GetMapping("/activity/checkConflict")
    public ResponseEntity<Boolean> checkActivityTimeConflict(@RequestParam Integer vendorId,
                                                             @RequestParam String startTime,
                                                             @RequestParam String endTime) {
        boolean conflictExists = vendorActivityServiceAdmin.checkTimeConflict(vendorId, startTime, endTime);
        return ResponseEntity.ok(conflictExists);
    }

    // TODO: 2026-08-06 Modify API spec, front-end not fixed
    //  REQUEST PARAM TO BODY
    //  CREATED need to guide to source
    @PostMapping("/add")
    public ResponseEntity<Void> addActivity(@RequestBody AddActivityRequest request) throws IOException {
        vendorActivityServiceAdmin.addActivity(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // TODO: 2026-08-06 Modify API spec, front-end not fixed
    //  POST TO PUT
    //  REQUEST PARAM TO BODY
    //  Unify parameters naming to camel case
    @PutMapping("/update")
    public ResponseEntity<CalendarEvent> updateActivity(@RequestBody UpdateActivityRequest request) throws IOException {
        CalendarEvent calendarEvent = vendorActivityServiceAdmin.updateActivity(request);
        return ResponseEntity.ok(calendarEvent);
    }

    @GetMapping("/activity/{vendorId}")
    public ResponseEntity<List<VendorActivity>> getVendorActivitiesByVendorId(@PathVariable Integer vendorId) {
        List<VendorActivity> activities = vendorActivityServiceAdmin.getVendorActivityByVendorId(vendorId);
        return activities.isEmpty()
                ? ResponseEntity.notFound().build()
                : ResponseEntity.ok(activities);
    }

    // TODO: the endpoints below are different

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteVendorActivity(@PathVariable Integer id) {
        vendorActivityServiceAdmin.deleteVendorActivity(id);
        return ResponseEntity.ok(Map.of("message", "刪除成功"));
    }

    @GetMapping("/photos/download")
    public ResponseEntity<byte[]> downloadPhotoById(@RequestParam Integer photoId) {
        byte[] photo = vendorActivityServiceAdmin.getPhotoById(photoId);
        return ResponseEntity.ok()
                .headers(HeadersUtil.createHeadersWithMediaTypeJpg())
                .body(photo);
    }

    @GetMapping("/photos/ids")
    public ResponseEntity<List<Integer>> findPhotoIdsByVendorActivityId(@RequestParam Integer vendorActivityId) {
        List<Integer> photoIds = vendorActivityServiceAdmin.getPhotoIdsByActivityId(vendorActivityId);
        return ResponseEntity.ok(photoIds);
    }

    @GetMapping("/photos/idss")
    public ResponseEntity<List<Map<String, Object>>> findPhotoIdsByVendorActivityIds(@RequestParam List<Integer> vendorActivityIds) {
        List<Map<String, Object>> response = vendorActivityServiceAdmin.findPhotoIdsByActivityIds(vendorActivityIds);
        return ResponseEntity.ok(response);
    }
}
