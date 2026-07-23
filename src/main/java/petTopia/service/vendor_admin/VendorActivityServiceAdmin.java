package petTopia.service.vendor_admin;

import java.io.IOException;
import java.util.*;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import petTopia.dto.vendor_admin.TopActivityDTO;
import petTopia.dto.vendor_admin.request.AddActivityRequest;
import petTopia.dto.vendor_admin.request.UpdateActivityRequest;
import petTopia.dto.vendor_admin.response.ActivityDetailResponse;
import petTopia.model.vendor.*;
import petTopia.repository.vendor.CalendarEventRepository;
import petTopia.repository.vendor.VendorActivityRepository;
import petTopia.repository.vendor.VendorRepository;
import petTopia.repository.vendor_admin.ActivityPeopleNumberRepository;
import petTopia.repository.vendor_admin.ActivityRegistrationRepository;
import petTopia.repository.vendor_admin.ActivityTypeRepository;
import petTopia.repository.vendor_admin.VendorActivityImagesRepository;
import org.springframework.jdbc.core.JdbcTemplate;

@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class VendorActivityServiceAdmin {
    private final VendorRepository vendorRepository;
    private final VendorActivityRepository vendorActivityRepository;
    private final VendorActivityImagesRepository vendorActivityImagesRepository;
    private final CalendarEventRepository calendarEventRepository;
    private final ActivityRegistrationRepository activityRegistrationRepository;
    private final ActivityPeopleNumberRepository activityPeopleNumberRepository;
    private final ActivityTypeRepository activityTypeRepository;
    private final JdbcTemplate jdbcTemplate;

    private List<Map<String, Object>> getRegistrationOptions() {
        List<Map<String, Object>> registrationOptions = new ArrayList<>();
        registrationOptions.add(Map.of("value", "true", "label", "需要報名"));
        registrationOptions.add(Map.of("value", "false", "label", "不需報名"));
        return registrationOptions;
    }

    public List<VendorActivity> getVendorActivityByVendorId(Integer vendorId) {
        return vendorActivityRepository.findByVendorId(vendorId);
    }

    @Transactional
    public void deleteVendorActivity(Integer id) {
        calendarEventRepository.deleteByVendorActivityId(id);

        Vendor vendor = vendorActivityRepository.findById(id)
                .map(VendorActivity::getVendor)
                .orElseThrow(() -> new EntityNotFoundException("Vendor not found"));

        vendorActivityRepository.deleteById(id);

        updateActivityCount(vendor);
    }

    public List<TopActivityDTO> getTop5Activities() {
        Pageable pageable = PageRequest.of(0, 5);
        return activityRegistrationRepository.findTop5Activities(pageable);
    }

    // TODO: move to repository layer
    public boolean checkTimeConflictDetail(Integer vendorId, Integer activityId, String startTime, String endTime) throws DataAccessException {
        String query = """
                SELECT COUNT(*) FROM vendor_activity
                WHERE vendor_id = ?
                AND id != ?
                AND (
                    (start_time BETWEEN ? AND ?)
                    OR (end_time BETWEEN ? AND ?)
                    OR (? BETWEEN start_time AND end_time)
                    OR (? BETWEEN start_time AND end_time)
                )
                """;

        int count = 0;

        try {
            count = jdbcTemplate.queryForObject(
                    query,
                    Integer.class,
                    vendorId, activityId,
                    startTime,
                    endTime,
                    startTime,
                    endTime,
                    startTime,
                    endTime
            );
        } catch (DataAccessException e) {
            log.error("Check time conflict detail failed", e);
        }

        return count > 0;
    }

    // TODO: move to repository layer
    public boolean checkTimeConflict(Integer vendorId, String startTime, String endTime) throws DataAccessException {
        String query = """
                SELECT COUNT(*) FROM vendor_activity
                WHERE vendor_id = ?
                AND (
                    (start_time BETWEEN ? AND ?)
                    OR (end_time BETWEEN ? AND ?)
                    OR (? BETWEEN start_time AND end_time)
                    OR (? BETWEEN start_time AND end_time)
                )
                """;

        int count = 0;

        try {
            count = jdbcTemplate.queryForObject(
                    query,
                    Integer.class,
                    vendorId,
                    startTime,
                    endTime,
                    startTime,
                    endTime,
                    startTime,
                    endTime
            );
        } catch (DataAccessException e) {
            log.error("Check time conflict failed", e);
        }

        return count > 0;
    }

    public ActivityDetailResponse getActivityDetail(Integer activityById) {
        Optional<VendorActivity> activityOptional = vendorActivityRepository.findById(activityById);

        if (activityOptional.isPresent()) {
            VendorActivity vendorActivity = activityOptional.get();
            ActivityPeopleNumber activityPeopleNumber = vendorActivity.getActivityPeopleNumber();

            List<ActivityType> activityTypes = activityTypeRepository.findAll();

            List<Integer> ids = vendorActivity.getImages()
                    .stream()
                    .map(VendorActivityImages::getId)
                    .toList();

            List<Map<String, Object>> registrationOptions = getRegistrationOptions();

            return ActivityDetailResponse.builder()
                    .activity(vendorActivity)
                    .activityImageIds(ids)
                    .activityPeopleNumber(activityPeopleNumber)
                    .activityTypes(activityTypes)
                    .registrationOptions(registrationOptions)
                    .build();
        } else {
            return null;
        }
    }

    @Transactional
    public void addActivity(AddActivityRequest request) throws IOException {
        Vendor vendor = vendorRepository.findById(request.getVendorId())
                .orElseThrow(() -> new EntityNotFoundException("Vendor not found"));

        VendorActivity vendorActivity = new VendorActivity();

        vendorActivity.setVendor(vendor);
        vendorActivity.setName(request.getActivityName());
        vendorActivity.setActivityType(request.getTypeId());
        vendorActivity.setDescription(request.getDescription());
        vendorActivity.setAddress(request.getAddress());
        vendorActivity.setStartTime(request.getStartTime());
        vendorActivity.setEndTime(request.getEndTime());

        boolean isRegistrationRequiredBoolean = Boolean.parseBoolean(request.getIsRegistrationRequired());
        vendorActivity.setRegistrationRequired(isRegistrationRequiredBoolean);

        List<VendorActivityImages> vendorActivityImagesList = new ArrayList<>();
        if (request.getFiles() != null) {
            for (MultipartFile oneFile : request.getFiles()) {
                VendorActivityImages image = new VendorActivityImages();

                image.setImage(oneFile.getBytes());
                image.setVendorActivity(vendorActivity); // 多set 一

                vendorActivityImagesList.add(image);
            }
        }
        vendorActivity.setImages(vendorActivityImagesList); // 一set多

        VendorActivity savedActivity = vendorActivityRepository.save(vendorActivity);

        ActivityPeopleNumber activityPeopleNumber = new ActivityPeopleNumber();
        activityPeopleNumber.setVendorActivity(savedActivity);
        activityPeopleNumber.setMaxParticipants(request.getMaxParticipants());
        activityPeopleNumber.setCurrentParticipants(0); // default number: 0
        activityPeopleNumberRepository.save(activityPeopleNumber);

        updateActivityCount(vendor);

        CalendarEvent calendarEvent = new CalendarEvent();
        calendarEvent.setEventTitle(request.getActivityName());
        calendarEvent.setStartTime(request.getStartTime());
        calendarEvent.setEndTime(request.getEndTime());
        calendarEvent.setVendorActivity(vendorActivity);
        calendarEvent.setVendor(vendor);
        calendarEvent.setCreatedAt(new Date());
        calendarEvent.setUpdatedAt(new Date());
        calendarEvent.setColor("#ffb8b8");
        calendarEventRepository.save(calendarEvent);
    }

    @Transactional
    public CalendarEvent updateActivity(UpdateActivityRequest request) throws IOException {
        Integer activityId = request.getActivityId();

        VendorActivity vendorActivity = vendorActivityRepository.findById(activityId)
                .orElseThrow(() -> new EntityNotFoundException("Activity not found"));

        vendorActivity.setName(request.getActivityName());
        vendorActivity.setDescription(request.getDescription());
        vendorActivity.setAddress(request.getAddress());
        vendorActivity.setStartTime(request.getStartTime());
        vendorActivity.setEndTime(request.getEndTime());
        vendorActivity.setActivityType(request.getTypeId());

        boolean isRegistrationRequiredBoolean = Boolean.parseBoolean(request.getIsRegistrationRequired());
        vendorActivity.setRegistrationRequired(isRegistrationRequiredBoolean);

        if (request.getDeletedImageIds() != null && !request.getDeletedImageIds().isEmpty()) {
            vendorActivityImagesRepository.deleteAllById(request.getDeletedImageIds());
        }

        if (request.getFiles() != null && request.getFiles().length > 0) {
            List<VendorActivityImages> vendorActivityImagesList = new ArrayList<>();
            for (MultipartFile file : request.getFiles()) {
                if (!file.isEmpty()) {
                    VendorActivityImages vendorActivityImage = new VendorActivityImages();
                    vendorActivityImage.setImage(file.getBytes());
                    vendorActivityImage.setVendorActivity(vendorActivity);
                    vendorActivityImagesList.add(vendorActivityImage);
                }
            }
            vendorActivity.getImages().addAll(vendorActivityImagesList);
        }

        ActivityPeopleNumber activityPeopleNumber = activityPeopleNumberRepository.findByVendorActivityId(activityId)
                .orElseThrow(() -> new EntityNotFoundException("Activity people number not found"));
        activityPeopleNumber.setMaxParticipants(request.getMaxParticipants());

        activityPeopleNumberRepository.save(activityPeopleNumber);

        if (!isRegistrationRequiredBoolean) {
            activityRegistrationRepository.deleteByVendorActivityId(activityId);

            activityPeopleNumber.setCurrentParticipants(0);

            activityPeopleNumberRepository.save(activityPeopleNumber);
        }

        vendorActivityRepository.save(vendorActivity);

        Optional<CalendarEvent> calendarOpt = calendarEventRepository.findByVendorActivityId(activityId);
        if (calendarOpt.isPresent()) {
            CalendarEvent calendarEvent = calendarOpt.get();

            calendarEvent.setEventTitle(request.getActivityName());
            calendarEvent.setStartTime(request.getStartTime());
            calendarEvent.setEndTime(request.getEndTime());
            calendarEvent.setUpdatedAt(new Date());

            return calendarEventRepository.save(calendarEvent);
        } else {
            return null;
        }
    }

    public byte[] getPhotoById(Integer imageId) {
        return vendorActivityImagesRepository.findById(imageId)
                .map(VendorActivityImages::getImage)
                .orElseThrow(() -> new EntityNotFoundException("Image not found"));
    }

    public List<Integer> getPhotoIdsByActivityId(Integer activityId) {
        Optional<VendorActivity> optional = vendorActivityRepository.findById(activityId);

        if (optional.isEmpty()) return List.of();

        List<Integer> imageIds = new ArrayList<>();

        optional.get().getVendorActivityImages()
                .forEach(image -> imageIds.add(image.getId()));

        return imageIds;
    }

    // TODO: N+1 problem
    public List<Map<String, Object>> findPhotoIdsByActivityIds(List<Integer> activityIds) {
        if (activityIds == null || activityIds.isEmpty()) return List.of();

        List<Map<String, Object>> result = new ArrayList<>();

        for (Integer activityId : activityIds) {
            Optional<List<VendorActivityImages>> optional = vendorActivityRepository.findById(activityId)
                    .map(VendorActivity::getVendorActivityImages);

            if (optional.isPresent()) {
                List<Integer> imageIdList = new ArrayList<>();

                optional.get().forEach(image -> imageIdList.add(image.getId()));

                Map<String, Object> data = new HashMap<>();
                data.put("vendorActivityId", activityId);
                data.put("imageIds", imageIdList);

                result.add(data);
            }
        }

        return result;
    }

    private void updateActivityCount(Vendor vendor) {
        int activityCount = vendorActivityRepository.countByVendor(vendor);
        vendor.setEventCount(activityCount);

        if (activityCount > 8) {
            vendor.setVendorLevel("頂級");
        } else if (activityCount > 5) {
            vendor.setVendorLevel("資深");
        } else if (activityCount > 2) {
            vendor.setVendorLevel("進階");
        } else {
            vendor.setVendorLevel("普通");
        }

        vendorRepository.save(vendor);
    }
}
