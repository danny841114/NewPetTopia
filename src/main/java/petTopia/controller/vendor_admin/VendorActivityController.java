package petTopia.controller.vendor_admin;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    public ResponseEntity<?> getVendorActivityDetail(@RequestParam Integer id) {
        ActivityDetailResponse activityDetail = vendorActivityServiceAdmin.getActivityDetail(id);

        if (activityDetail == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("活動不存在");

        return ResponseEntity.ok(activityDetail);
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

    // TODO:
    //  REQUEST PARAM TO BODY
    @PostMapping("/add")
    public ResponseEntity<?> addActivity(@RequestParam("vendor_id") Integer vendorId,
                                         @RequestParam("activity_name") String activityName,
                                         @RequestParam("activity_type_id") ActivityType typeId,
                                         @RequestParam("activity_description") String description,
                                         @RequestParam("activity_address") String address,
                                         @RequestParam("start_time") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") Date startTime,
                                         @RequestParam("end_time") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") Date endTime,
                                         @RequestParam("is_registration_required") String isRegistrationRequired,
                                         @RequestParam("max_participants") Integer maxParticipants,
                                         @RequestParam("files") MultipartFile[] files) throws IOException {
        AddActivityRequest request = new AddActivityRequest();
        request.setVendorId(vendorId);
        request.setActivityName(activityName);
        request.setTypeId(typeId);
        request.setDescription(description);
        request.setAddress(address);
        request.setStartTime(startTime);
        request.setEndTime(endTime);
        request.setIsRegistrationRequired(isRegistrationRequired);
        request.setMaxParticipants(maxParticipants);
        request.setFiles(files);

        vendorActivityServiceAdmin.addActivity(request);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // TODO:
    //  POST TO PUT
    //  REQUEST PARAM TO BODY
    @PostMapping("/update")
    public ResponseEntity<?> updateActivity(@RequestParam("activity_id") Integer activityId,
                                            @RequestParam("vendor_id") Integer vendorId,
                                            @RequestParam("activity_name") String activityName,
                                            @RequestParam("activity_type_id") ActivityType typeId,
                                            @RequestParam("activity_description") String description,
                                            @RequestParam("activity_address") String address,
                                            @RequestParam("start_time") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") Date startTime,
                                            @RequestParam("end_time") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") Date endTime,
                                            @RequestParam("is_registration_required") String isRegistrationRequired,
                                            @RequestParam("max_participants") Integer maxParticipants,
                                            @RequestParam(value = "files", required = false) MultipartFile[] files,
                                            @RequestParam(value = "deletedImageIds", required = false) List<Integer> deletedImageIds) throws IOException {
        UpdateActivityRequest request = new UpdateActivityRequest();
        request.setActivityId(activityId);
        request.setVendorId(vendorId);
        request.setActivityName(activityName);
        request.setTypeId(typeId);
        request.setDescription(description);
        request.setAddress(address);
        request.setStartTime(startTime);
        request.setEndTime(endTime);
        request.setIsRegistrationRequired(isRegistrationRequired);
        request.setMaxParticipants(maxParticipants);
        request.setFiles(files);
        request.setDeletedImageIds(deletedImageIds);

        CalendarEvent calendarEvent = vendorActivityServiceAdmin.updateActivity(request);

        return ResponseEntity.ok(calendarEvent);
    }

    @GetMapping("/activity/{vendorId}")
    public ResponseEntity<List<VendorActivity>> getVendorActivitiesByVendorId(@PathVariable Integer vendorId) {
        List<VendorActivity> activities = vendorActivityServiceAdmin.getVendorActivityByVendorId(vendorId);

        if (activities.isEmpty()) return ResponseEntity.notFound().build();

        return ResponseEntity.ok(activities);
    }


    // TODO: the endpoints below are different

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteVendorActivity(@PathVariable Integer id) {
        vendorActivityServiceAdmin.deleteVendorActivity(id);
        return ResponseEntity.ok(Map.of("message", "刪除成功"));
    }

    @GetMapping("/photos/download")
    public ResponseEntity<?> downloadPhotoById(@RequestParam Integer photoId) {
        byte[] photo = vendorActivityServiceAdmin.getPhotoById(photoId);
        return ResponseEntity.ok()
                .headers(HeadersUtil.createHeadersWithMediaTypeJpg())
                .body(photo);
    }

    @GetMapping("/photos/ids")
    public ResponseEntity<?> findPhotoIdsByVendorActivityId(@RequestParam Integer vendorActivityId) {
        List<Integer> photoIds = vendorActivityServiceAdmin.getPhotoIdsByActivityId(vendorActivityId);
        return ResponseEntity.ok(photoIds);
    }

    @GetMapping("/photos/idss")
    public ResponseEntity<?> findPhotoIdsByVendorActivityIds(@RequestParam List<Integer> vendorActivityIds) {
        List<Map<String, Object>> response = vendorActivityServiceAdmin.findPhotoIdsByActivityIds(vendorActivityIds);
        return ResponseEntity.ok(response);
    }
}
