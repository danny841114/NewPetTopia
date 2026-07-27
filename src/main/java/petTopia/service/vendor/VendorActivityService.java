package petTopia.service.vendor;

import java.util.List;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import petTopia.dto.vendor.ActivityDto;
import petTopia.model.vendor.ActivityType;
import petTopia.model.vendor.VendorActivity;
import petTopia.repository.vendor.VendorActivityRepository;
import petTopia.repository.vendor_admin.ActivityTypeRepository;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class VendorActivityService {
    private final VendorActivityRepository vendorActivityRepository;
    private final ActivityTypeRepository activityTypeRepository;

    public List<ActivityDto> findAllActivities() {
        return vendorActivityRepository.findAll()
                .stream()
                .map(ActivityDto::fromEntity)
                .toList();
    }

    public ActivityDto findById(Integer id) {
        return vendorActivityRepository.findById(id)
                .map(ActivityDto::fromEntity)
                .orElseThrow(() -> new EntityNotFoundException("Activity not found"));
    }

    // TODO: add JPQL
    public List<ActivityDto> findAllActivityExceptOne(Integer activityId) {
        List<VendorActivity> activityList = vendorActivityRepository.findAll();

        VendorActivity activityToRemove = vendorActivityRepository.findById(activityId)
                .orElseThrow(() -> new EntityNotFoundException("Activity not found"));

        activityList.removeIf(v -> v.getId().equals(activityToRemove.getId()));

        return activityList.stream()
                .map(ActivityDto::fromEntity)
                .toList();
    }

    // TODO: add JPQL
    public List<ActivityDto> findActivityByTypeId(Integer typeId) {
        ActivityType type = activityTypeRepository.findById(typeId)
                .orElseThrow(() -> new EntityNotFoundException("Activity type not found"));

        return vendorActivityRepository.findByActivityType(type)
                .stream()
                .map(ActivityDto::fromEntity)
                .toList();
    }

    public List<ActivityDto> findActivityByTypeIdExceptOne(Integer typeId, Integer activityId) {
        ActivityType type = activityTypeRepository.findById(typeId)
                .orElseThrow(() -> new EntityNotFoundException("Activity type not found"));

        List<VendorActivity> activityList = vendorActivityRepository.findByActivityType(type);

        VendorActivity activityToRemove = vendorActivityRepository.findById(activityId)
                .orElseThrow(() -> new EntityNotFoundException("Activity not found"));

        activityList.removeIf(activity -> activity.getId().equals(activityToRemove.getId()));

        return activityList.stream()
                .map(ActivityDto::fromEntity)
                .toList();
    }

    @Transactional
    public ActivityDto increaseNumberOfVisitor(Integer activityId) {
        VendorActivity activity = vendorActivityRepository.findById(activityId)
                .orElseThrow(() -> new EntityNotFoundException("Activity not found"));

        activity.setNumberVisitor(activity.getNumberVisitor() + 1);

        VendorActivity savedActivity = vendorActivityRepository.save(activity);

        return ActivityDto.fromEntity(savedActivity);
    }

    public List<ActivityDto> findByNameOrDescription(String keyword) {
        return vendorActivityRepository.findDistinctByNameContainingOrDescriptionContainingOrAddressContaining(keyword, keyword, keyword)
                .stream()
                .map(ActivityDto::fromEntity)
                .toList();
    }

    public List<ActivityDto> findByVendorId(Integer vendorId) {
        return vendorActivityRepository.findByVendorId(vendorId)
                .stream()
                .map(ActivityDto::fromEntity)
                .toList();
    }
}
