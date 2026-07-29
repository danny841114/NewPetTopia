package petTopia.service.vendor;

import java.util.List;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import petTopia.dto.vendor.ActivityImageDto;
import petTopia.model.vendor.VendorActivity;
import petTopia.model.vendor.VendorActivityImages;
import petTopia.repository.vendor.VendorActivityRepository;
import petTopia.repository.vendor_admin.VendorActivityImagesRepository;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class VendorActivityImagesService {
    private final VendorActivityRepository vendorActivityRepository;
    private final VendorActivityImagesRepository vendorActivityImagesRepository;

    public List<ActivityImageDto> findImagesByActivityId(Integer activityId) {
        VendorActivity activity = vendorActivityRepository.findById(activityId)
                .orElseThrow(() -> new EntityNotFoundException("Vendor activity not found"));

        return vendorActivityImagesRepository.findByVendorActivity(activity)
                .stream()
                .map(ActivityImageDto::fromEntity)
                .toList();
    }

    // TODO: Change to JPQL
    public byte[] findById(Integer imageId) {
        return vendorActivityImagesRepository.findById(imageId)
                .map(VendorActivityImages::getImage)
                .orElse(null);
    }
}
