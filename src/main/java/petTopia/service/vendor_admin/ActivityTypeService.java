package petTopia.service.vendor_admin;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import petTopia.model.vendor.ActivityType;
import petTopia.repository.vendor_admin.ActivityTypeRepository;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class ActivityTypeService {
    private final ActivityTypeRepository activityTypeRepository;

    public List<ActivityType> getAllActivityTypes() {
        return activityTypeRepository.findAll();
    }
}
