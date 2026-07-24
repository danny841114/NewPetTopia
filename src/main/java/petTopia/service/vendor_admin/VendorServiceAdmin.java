package petTopia.service.vendor_admin;

import java.util.List;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import petTopia.model.vendor.Vendor;
import petTopia.model.vendor.VendorCategory;
import petTopia.repository.vendor.VendorCategoryRepository;
import petTopia.repository.vendor.VendorRepository;
import petTopia.repository.vendor_admin.VendorCertificationTagRepository;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class VendorServiceAdmin {
    private final VendorRepository vendorRepository;
    private final VendorCategoryRepository vendorCategoryRepository;
    private final VendorCertificationTagRepository vendorCertificationTagRepository;

    public byte[] getVendorLogoImgByVendorId(Integer vendorId) {
        return vendorRepository.findById(vendorId)
                .map(Vendor::getLogoImg)
                .orElseThrow(() -> new EntityNotFoundException("Vendor logo image not found"));
    }

    public Boolean getVendorStatus(Integer vendorId) {
        return vendorRepository.findStatusById(vendorId)
                .map(Vendor::isStatus)
                .orElseThrow(()->new EntityNotFoundException("Vendor status not found"));
    }

    public List<VendorCategory> getAllVendorCategories() {
        return vendorCategoryRepository.findAll();
    }

    public List<String> getSlogansByVendorId(Integer vendorId) {
        return vendorCertificationTagRepository.findCertifiedSlogansByVendorId(vendorId);
    }
}
