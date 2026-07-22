package petTopia.service.vendor;

import java.util.Base64;
import java.util.List;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import petTopia.model.vendor.VendorActivity;
import petTopia.model.vendor.VendorActivityImages;
import petTopia.repository.vendor.VendorActivityRepository;
import petTopia.repository.vendor_admin.VendorActivityImagesRepository;
import petTopia.util.ImageConverter;

@RequiredArgsConstructor
@Service
public class VendorActivityImagesService {
    private final VendorActivityRepository vendorActivityRepository;
    private final VendorActivityImagesRepository vendorActivityImagesRepository;

    /* 設定Base64 */
    public List<VendorActivityImages> findImageListByActivityId(Integer activityId) {
        VendorActivity activity = vendorActivityRepository.findById(activityId)
                .orElseThrow(() -> new EntityNotFoundException("Vendor activity not found"));

        List<VendorActivityImages> imageList = vendorActivityImagesRepository.findByVendorActivity(activity);

        // TODO: change base64 to byte[]
        for (VendorActivityImages images : imageList) {
            byte[] imageByte = images.getImage();
            if (imageByte != null) {
                String mimeType = ImageConverter.getMimeType(imageByte);
                String base64 = "data:%s;base64,".formatted(mimeType) + Base64.getEncoder().encodeToString(imageByte);
                images.setImageBase64(base64);
            }
        }
        return imageList;
    }
}
