package petTopia.service.vendor;

import java.util.Base64;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import petTopia.model.vendor.VendorImages;
import petTopia.repository.vendor.VendorImagesRepository;
import petTopia.util.ImageConverter;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class VendorImagesService {
    private final VendorImagesRepository vendorImagesRepository;

    /* 設定Base64 */
    // TODO: change base64 to byte[]
    public List<VendorImages> findImageListByVendorId(Integer vendorId) {
        List<VendorImages> imageList = vendorImagesRepository.findByVendorId(vendorId);
        for (VendorImages images : imageList) {
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
