package petTopia.service.vendor_admin;

import java.util.Base64;
import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import petTopia.model.vendor.Vendor;
import petTopia.model.vendor.VendorActivity;
import petTopia.model.vendor.VendorCategory;
import petTopia.repository.vendor.VendorActivityRepository;
import petTopia.repository.vendor.VendorCategoryRepository;
import petTopia.repository.vendor.VendorRepository;
import petTopia.repository.vendor_admin.VendorCertificationRepository;
import petTopia.repository.vendor_admin.VendorCertificationTagRepository;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class VendorServiceAdmin {
    private final VendorRepository vendorRepository;
    private final VendorCategoryRepository vendorCategoryRepository;
    private final VendorActivityRepository vendorActivityRepository;
    private final VendorCertificationRepository vendorCertificationRepository;
    private final VendorCertificationTagRepository vendorCertificationTagRepository;

    public Optional<Vendor> getVendorById(Integer vendorId) {
        return vendorRepository.findById(vendorId);
    }

    public Optional<Vendor> getVendorStatus(Integer vendorId) {
        return vendorRepository.findStatusById(vendorId);
    }

    public String getVendorLogoBase64(Vendor vendor) {
        if (vendor.getLogoImg() != null) {
            return "data:image/jpeg;base64," + Base64.getEncoder().encodeToString(vendor.getLogoImg());
        }

        return null;
    }

    public List<VendorCategory> getAllVendorCategories() {
        return vendorCategoryRepository.findAll();
    }

    @Transactional
    public Vendor updateVendor(Vendor vendor) {
        return vendorRepository.save(vendor);
    }

    @Transactional
    public void deleteVendor(Vendor vendor) {
        vendorRepository.delete(vendor);
    }

    public Optional<Vendor> getVendorByUserId(Integer userId) {
        return vendorRepository.findById(userId);
    }

    public int getActivityCountByVendor(Integer vendorId) {
        List<VendorActivity> activities = vendorActivityRepository.findByVendorId(vendorId);
        return activities.size();
    }

    public List<String> getCertifiedVendorsSlogans() {
        List<Integer> certifiedVendorIds = vendorCertificationRepository.findCertifiedVendorIds();
        return vendorCertificationTagRepository.findSlogansByVendorIds(certifiedVendorIds);
    }

    public List<String> getSlogansByVendorId(Integer vendorId) {
        return vendorCertificationTagRepository.findCertifiedSlogansByVendorId(vendorId);
    }
}
