package petTopia.service.vendor;

import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import petTopia.model.vendor.ActivityType;
import petTopia.repository.vendor_admin.ActivityTypeRepository;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class ActivityTypeUserService {
    private final ActivityTypeRepository activityTypeRepository;

    public List<ActivityType> findAllActivityType() {
        return activityTypeRepository.findAll().stream()
                .filter(activityType -> !activityType.getVendorActivities().isEmpty())
                .collect(Collectors.toList());
    }
}
