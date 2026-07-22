package petTopia.service.vendor;

import java.util.Base64;
import java.util.List;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import petTopia.model.vendor.ActivityType;
import petTopia.model.vendor.VendorActivity;
import petTopia.repository.vendor.VendorActivityRepository;
import petTopia.repository.vendor_admin.ActivityTypeRepository;
import petTopia.util.ImageConverter;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class VendorActivityService {
    private final VendorActivityRepository vendorActivityRepository;
    private final ActivityTypeRepository activityTypeRepository;

    public List<VendorActivity> findAllActivity() {
        List<VendorActivity> activityList = vendorActivityRepository.findAll();

        // TODO: change base64 to byte[]
        for (VendorActivity activity : activityList) {
            byte[] logoImg = activity.getVendor().getLogoImg();
            if (logoImg != null) {
                String mimeType = ImageConverter.getMimeType(logoImg);
                String base64 = "data:%s;base64,".formatted(mimeType) + Base64.getEncoder().encodeToString(logoImg);
                activity.getVendor().setLogoImgBase64(base64);
            }
        }

        return activityList;
    }

    public VendorActivity findActivityById(Integer id) {
        VendorActivity activity = vendorActivityRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Activity not found"));

        // TODO: change base64 to byte[]
        byte[] logoImg = activity.getVendor().getLogoImg();
        if (logoImg != null) {
            String mimeType = ImageConverter.getMimeType(logoImg);
            String base64 = "data:%s;base64,".formatted(mimeType) + Base64.getEncoder().encodeToString(logoImg);
            activity.getVendor().setLogoImgBase64(base64);
        }

        return activity;
    }

    // TODO: add JPQL
    public List<VendorActivity> findAllActivityExceptOne(Integer activityId) {
        List<VendorActivity> activityList = vendorActivityRepository.findAll();

        VendorActivity activityToRemove = vendorActivityRepository.findById(activityId)
                .orElseThrow(() -> new EntityNotFoundException("Activity not found"));

        activityList.removeIf(v -> v.getId().equals(activityToRemove.getId()));

        return activityList;
    }

    // TODO: add JPQL
    public List<VendorActivity> findActivityByTypeId(Integer typeId) {
        ActivityType type = activityTypeRepository.findById(typeId)
                .orElseThrow(() -> new EntityNotFoundException("Activity type not found"));

        return vendorActivityRepository.findByActivityType(type);
    }

    public List<VendorActivity> findActivityByTypeIdExceptOne(Integer typeId, Integer activityId) {
        ActivityType type = activityTypeRepository.findById(typeId)
                .orElseThrow(() -> new EntityNotFoundException("Activity type not found"));

        List<VendorActivity> activityList = vendorActivityRepository.findByActivityType(type);

        VendorActivity activityToRemove = vendorActivityRepository.findById(activityId)
                .orElseThrow(() -> new EntityNotFoundException("Activity not found"));

        activityList.removeIf(activity -> activity.getId().equals(activityToRemove.getId()));

        return activityList;
    }

    @Transactional
    public VendorActivity increaseNumberOfVisitor(Integer activityId) {
        VendorActivity activity = vendorActivityRepository.findById(activityId)
                .orElseThrow(() -> new EntityNotFoundException("Activity not found"));

        activity.setNumberVisitor(activity.getNumberVisitor() + 1);
        return vendorActivityRepository.save(activity);
    }

    public List<VendorActivity> findVendorByNameOrDescription(String keyword) {
        return vendorActivityRepository.findDistinctByNameContainingOrDescriptionContainingOrAddressContaining(keyword, keyword, keyword);
    }

    public List<VendorActivity> findActivityListByVendorId(Integer vendorId) {
        return vendorActivityRepository.findByVendorId(vendorId);
    }
}
