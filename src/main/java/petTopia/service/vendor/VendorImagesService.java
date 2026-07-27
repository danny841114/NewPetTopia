package petTopia.service.vendor;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import petTopia.dto.vendor.VendorImageDto;
import petTopia.model.vendor.VendorImages;
import petTopia.repository.vendor.VendorImagesRepository;

import static petTopia.constant.ImageUrl.VENDOR_IMG_URL_PREFIX;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class VendorImagesService {
    private final VendorImagesRepository vendorImagesRepository;

    public List<VendorImageDto> findImageListByVendorId(Integer vendorId) {
        List<VendorImages> images = vendorImagesRepository.findByVendorId(vendorId);

        List<VendorImageDto> dtos = new ArrayList<>();

        for (VendorImages image : images) {
            String imgUrl = VENDOR_IMG_URL_PREFIX.replace("{id}", String.valueOf(image.getId()));

            VendorImageDto dto = VendorImageDto.builder()
                    .id(image.getId())
                    .imgUrl(imgUrl)
                    .build();

            dtos.add(dto);
        }

        return dtos;
    }

    public byte[] getVendorImageById(Integer imageId) {
        return vendorImagesRepository.findById(imageId)
                .map(VendorImages::getImage)
                .orElseThrow(() -> new EntityNotFoundException("Vendor image not found"));
    }
}
