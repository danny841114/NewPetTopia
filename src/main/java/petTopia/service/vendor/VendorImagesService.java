package petTopia.service.vendor;

import java.util.List;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import petTopia.dto.vendor.VendorImageDto;
import petTopia.model.vendor.VendorImages;
import petTopia.repository.vendor.VendorImagesRepository;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class VendorImagesService {
    private final VendorImagesRepository vendorImagesRepository;

    public List<VendorImageDto> findImageListByVendorId(Integer vendorId) {
        return vendorImagesRepository.findByVendorId(vendorId)
                .stream()
                .map(VendorImageDto::fromEntity)
                .toList();
    }

    public byte[] getVendorImageById(Integer imageId) {
        return vendorImagesRepository.findById(imageId)
                .map(VendorImages::getImage)
                .orElseThrow(() -> new EntityNotFoundException("Vendor image not found"));
    }
}
