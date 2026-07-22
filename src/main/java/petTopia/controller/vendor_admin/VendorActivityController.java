package petTopia.controller.vendor_admin;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import petTopia.dto.vendor_admin.TopActivityDTO;
import petTopia.model.vendor.ActivityPeopleNumber;
import petTopia.model.vendor.ActivityType;
import petTopia.model.vendor.CalendarEvent;
import petTopia.model.vendor.Vendor;
import petTopia.model.vendor.VendorActivity;
import petTopia.model.vendor.VendorActivityImages;
import petTopia.repository.vendor.CalendarEventRepository;
import petTopia.repository.vendor.VendorActivityRepository;
import petTopia.repository.vendor.VendorRepository;
import petTopia.repository.vendor_admin.ActivityPeopleNumberRepository;
import petTopia.repository.vendor_admin.ActivityRegistrationRepository;
import petTopia.repository.vendor_admin.VendorActivityImagesRepository;
import petTopia.service.vendor_admin.ActivityTypeService;
import petTopia.service.vendor_admin.VendorActivityServiceAdmin;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/vendor_admin")
public class VendorActivityController {
    private final VendorActivityServiceAdmin vendorActivityServiceAdmin;
    private final ActivityTypeService activityTypeService;

    private final VendorRepository vendorRepository;
    private final VendorActivityRepository vendorActivityRepository;
    private final VendorActivityImagesRepository vendorActivityImagesRepository;
    private final ActivityPeopleNumberRepository activityPeopleNumberRepository;
    private final CalendarEventRepository calendarEventRepository;
    private final ActivityRegistrationRepository activityRegistrationRepository;

    @GetMapping("/activity/top5")
    public ResponseEntity<List<TopActivityDTO>> getTop5Activities() {
        List<TopActivityDTO> topActivities = vendorActivityServiceAdmin.getTop5Activities();
        return ResponseEntity.ok(topActivities);
    }

    private void updateActivityCount(Vendor vendor) {
        int activityCount = vendorActivityRepository.countByVendor(vendor);
        vendor.setEventCount(activityCount);

        // **根据活动数量更新 level**
        if (activityCount > 8) {
            vendor.setVendorLevel("頂級"); // 例如：50场以上是白金等级
        } else if (activityCount > 5) {
            vendor.setVendorLevel("資深"); // 20-49场是黄金等级
        } else if (activityCount > 2) {
            vendor.setVendorLevel("進階"); // 10-19场是白银等级
        } else {
            vendor.setVendorLevel("普通"); // 10场以下是青铜等级
        }
        vendorRepository.save(vendor); // 更新活动数量到数据库
    }

    @GetMapping("/vendor_admin_activityDetail")
    public ResponseEntity<?> getVendorActivityDetail(@RequestParam Integer id) {
        Optional<VendorActivity> optional = vendorActivityServiceAdmin.getVendorActivityById(id);

        if (optional.isPresent()) {
            VendorActivity vendorActivity = optional.get();
            ActivityPeopleNumber activityPeopleNumber = vendorActivity.getActivityPeopleNumber();
            List<ActivityType> activityTypes = activityTypeService.getAllActivityTypes();

            List<Integer> ids = vendorActivity.getImages()
                    .stream()
                    .map(VendorActivityImages::getId)
                    .toList();

            // 轉換成 JSON
            Map<String, Object> response = new HashMap<>();
            response.put("vendorActivity", vendorActivity);
            response.put("vendorActivityImageIdList", ids);
            response.put("activityPeopleNumber", activityPeopleNumber);
            response.put("activityTypes", activityTypes);

            // 報名選項
            List<Map<String, Object>> registrationOptions = new ArrayList<>();
            registrationOptions.add(Map.of("value", "true", "label", "需要報名"));
            registrationOptions.add(Map.of("value", "false", "label", "不需報名"));
            response.put("registrationOptions", registrationOptions);

            return ResponseEntity.ok(response);
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("活動不存在");
    }

    @GetMapping("/activity/allTypes")
    public ResponseEntity<List<ActivityType>> getAllActivityTypes() {
        List<ActivityType> types = activityTypeService.getAllActivityTypes();
        return ResponseEntity.ok(types);
    }

    @GetMapping("/activity/checkConflictDetail")
    public ResponseEntity<Boolean> checkActivityTimeConflictDetail(@RequestParam("vendorId") Integer vendorId,
                                                                   @RequestParam("activityId") Integer activityId,
                                                                   @RequestParam("startTime") String startTime,
                                                                   @RequestParam("endTime") String endTime) {
        boolean conflictExists = vendorActivityServiceAdmin.checkTimeConflictDetail(vendorId, activityId, startTime, endTime);
        return ResponseEntity.ok(conflictExists);
    }

    @GetMapping("/activity/checkConflict")
    public ResponseEntity<Boolean> checkActivityTimeConflict(@RequestParam("vendorId") Integer vendorId,
                                                             @RequestParam("startTime") String startTime,
                                                             @RequestParam("endTime") String endTime) {
        boolean conflictExists = vendorActivityServiceAdmin.checkTimeConflict(vendorId, startTime, endTime);
        return ResponseEntity.ok(conflictExists);
    }

    // TODO: Move to service layer
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
                                         @RequestParam("files") MultipartFile[] files) {
        try {
            Vendor vendor = vendorRepository.findById(vendorId)
                    .orElseThrow(() -> new Exception("Vendor not found"));

            VendorActivity vendorActivity = new VendorActivity();

            vendorActivity.setVendor(vendor);
            vendorActivity.setName(activityName);
            vendorActivity.setActivityType(typeId);
            vendorActivity.setDescription(description);
            vendorActivity.setAddress(address);
            vendorActivity.setStartTime(startTime);
            vendorActivity.setEndTime(endTime);

            boolean isRegistrationRequiredBoolean = Boolean.parseBoolean(isRegistrationRequired);
            vendorActivity.setRegistrationRequired(isRegistrationRequiredBoolean);

            List<VendorActivityImages> vendorActivityImagesList = new ArrayList<>();

            for (MultipartFile oneFile : files) {
                VendorActivityImages image = new VendorActivityImages();

                image.setImage(oneFile.getBytes());
                image.setVendorActivity(vendorActivity); // 多set 一

                vendorActivityImagesList.add(image);
            }
            vendorActivity.setImages(vendorActivityImagesList); // 一set多

            VendorActivity savedActivity = vendorActivityRepository.save(vendorActivity);

            ActivityPeopleNumber activityPeopleNumber = new ActivityPeopleNumber();
            activityPeopleNumber.setVendorActivity(savedActivity);
            activityPeopleNumber.setMaxParticipants(maxParticipants);
            activityPeopleNumber.setCurrentParticipants(0); // 初始參與人數設為 0
            activityPeopleNumberRepository.save(activityPeopleNumber);

            updateActivityCount(vendor);

            CalendarEvent calendarEvent = new CalendarEvent();
            calendarEvent.setEventTitle(activityName);
            calendarEvent.setStartTime(startTime);
            calendarEvent.setEndTime(endTime);
            calendarEvent.setVendorActivity(vendorActivity);
            calendarEvent.setVendor(vendor);
            calendarEvent.setCreatedAt(new Date());
            calendarEvent.setUpdatedAt(new Date());
            calendarEvent.setColor("#ffb8b8");
            calendarEventRepository.save(calendarEvent);

            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (Exception e) {
            log.error("Add activity failed", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // TODO: Move to service layer
    @PostMapping("/update")
    public ResponseEntity<?> updateActivity(@RequestParam("activity_id") Integer activityId,
                                            @RequestParam("vendor_id") Integer vendorId,
                                            @RequestParam("activity_name") String activityName,
                                            @RequestParam("activity_type_id") ActivityType typeId,
                                            @RequestParam("activity_description") String description,
                                            @RequestParam("activity_address") String activity_address,
                                            @RequestParam("start_time") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") Date startTime,
                                            @RequestParam("end_time") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") Date endTime,
                                            @RequestParam("is_registration_required") String isRegistrationRequired,
                                            @RequestParam("max_participants") Integer maxParticipants,
                                            @RequestParam(value = "files", required = false) MultipartFile[] files,
                                            @RequestParam(value = "deletedImageIds", required = false) List<Integer> deletedImageIds) {
        try {
            VendorActivity vendorActivity = vendorActivityRepository.findById(activityId)
                    .orElseThrow(() -> new EntityNotFoundException("活動不存在"));

            vendorActivity.setName(activityName);
            vendorActivity.setDescription(description);
            vendorActivity.setAddress(activity_address);
            vendorActivity.setStartTime(startTime);
            vendorActivity.setEndTime(endTime);
            vendorActivity.setActivityType(typeId);

            boolean isRegistrationRequiredBoolean = Boolean.parseBoolean(isRegistrationRequired);
            vendorActivity.setRegistrationRequired(isRegistrationRequiredBoolean);

            // 3. 刪除指定的圖片
            if (deletedImageIds != null && !deletedImageIds.isEmpty()) {
                vendorActivityImagesRepository.deleteAllById(deletedImageIds);
            }

            // 4. 更新新圖片（如果有新圖片則更新）
            if (files != null && files.length > 0) {
                List<VendorActivityImages> vendorActivityImagesList = new ArrayList<>();
                for (MultipartFile file : files) {
                    if (!file.isEmpty()) {
                        VendorActivityImages vendorActivityImage = new VendorActivityImages();
                        vendorActivityImage.setImage(file.getBytes());
                        vendorActivityImage.setVendorActivity(vendorActivity);
                        vendorActivityImagesList.add(vendorActivityImage);
                    }
                }
                vendorActivity.getImages().addAll(vendorActivityImagesList);
            }

            // 5. 更新最大參與人數
            ActivityPeopleNumber activityPeopleNumber = activityPeopleNumberRepository.findByVendorActivityId(activityId)
                    .orElseThrow(() -> new EntityNotFoundException("人數表不存在"));
            activityPeopleNumber.setMaxParticipants(maxParticipants);

            activityPeopleNumberRepository.save(activityPeopleNumber);

            if (!isRegistrationRequiredBoolean) {
                activityRegistrationRepository.deleteByVendorActivityId(activityId);

                activityPeopleNumber.setCurrentParticipants(0);

                activityPeopleNumberRepository.save(activityPeopleNumber);
            }

            // 6. 儲存變更
            vendorActivityRepository.save(vendorActivity);

            Optional<CalendarEvent> calendarOpt = calendarEventRepository.findByVendorActivityId(activityId);
            if (calendarOpt.isPresent()) {
                CalendarEvent calendarEvent = calendarOpt.get();

                calendarEvent.setEventTitle(activityName);
                calendarEvent.setStartTime(startTime);
                calendarEvent.setEndTime(endTime);
                calendarEvent.setUpdatedAt(new Date());

                CalendarEvent savedCalendarEvent = calendarEventRepository.save(calendarEvent);

                // TODO: change to status 201
                return ResponseEntity.ok(savedCalendarEvent);
            }

            // TODO: maybe throw exception
            return ResponseEntity.ok(null);
        } catch (Exception e) {
            log.error("Update activity failed", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/activity/{vendorId}")
    public ResponseEntity<List<VendorActivity>> getVendorActivitiesByVendorId(@PathVariable Integer vendorId) {
        List<VendorActivity> activities = vendorActivityServiceAdmin.getVendorActivityByVendorId(vendorId);

        if (!activities.isEmpty()) {
            return ResponseEntity.ok(activities);
        }

        return ResponseEntity.notFound().build();
    }


    // TODO: the endpoints below are different

    // TODO: Move to service layer
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteVendorActivity(@PathVariable Integer id) {
        vendorActivityServiceAdmin.deleteVendorActivity(id);

        vendorActivityRepository.findById(id)
                .ifPresent(activity -> updateActivityCount(activity.getVendor()));

        return ResponseEntity.ok(Map.of("message", "刪除成功"));
    }

    // TODO: Move to service layer
    @GetMapping("/photos/download")
    public ResponseEntity<?> downloadPhotoById(@RequestParam Integer photoId) {
        Optional<VendorActivityImages> imageOpt = vendorActivityImagesRepository.findById(photoId);

        if (imageOpt.isPresent()) {
            VendorActivityImages image = imageOpt.get();
            byte[] imageFile = image.getImage(); // 假設每個 VendorActivityPhoto 實體有一個 photoFile 字段，存儲圖片二進制數據

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG); // 假設圖片是 JPEG 格式

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(imageFile);
        }

        return ResponseEntity.notFound().build();
    }

    // TODO: Move to service layer
    @GetMapping("/photos/ids")
    public ResponseEntity<?> findPhotoIdsByVendorActivityId(@RequestParam Integer vendorActivityId) {
        Optional<VendorActivity> op = vendorActivityRepository.findById(vendorActivityId);

        if (op.isPresent()) {
            List<Integer> imageIdList = new ArrayList<>();

            VendorActivity vendorActivity = op.get();
            vendorActivity.getVendorActivityImages()
                    .forEach(image -> imageIdList.add(image.getId()));

            return ResponseEntity.ok(imageIdList);
        }

        // TODO: should not return 404
        return ResponseEntity.notFound().build();
    }

    // TODO: Move to service layer
    @GetMapping("/photos/idss")
    public ResponseEntity<?> findPhotoIdsByVendorActivityIds(@RequestParam List<Integer> vendorActivityIds) {
        if (vendorActivityIds == null) return ResponseEntity.notFound().build();

        List<Map<String, Object>> result = new ArrayList<>();

        for (Integer vendorActivityId : vendorActivityIds) {
            Optional<VendorActivity> op = vendorActivityRepository.findById(vendorActivityId);

            if (op.isPresent()) {
                List<Integer> imageIdList = new ArrayList<>();

                VendorActivity vendorActivity = op.get();

                vendorActivity.getVendorActivityImages()
                        .forEach(image -> imageIdList.add(image.getId()));

                Map<String, Object> data = new HashMap<>();
                data.put("vendorActivityId", vendorActivityId);
                data.put("imageIds", imageIdList);

                result.add(data);
            }
        }

        return ResponseEntity.ok(result);
    }
}
