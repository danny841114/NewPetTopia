package petTopia.service.vendor_admin;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;
import petTopia.model.vendor.Vendor;
import petTopia.repository.vendor.VendorCategoryRepository;
import petTopia.repository.vendor.VendorImagesRepository;
import petTopia.repository.vendor.VendorRepository;

import java.util.HashMap;
import java.util.Map;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class VendorProfileService {
    private final VendorRepository vendorRepository;
    private final VendorCategoryRepository categoryRepository;
    private final VendorImagesRepository vendorImagesRepository;

    public Vendor getVendorById(Integer id) {
        return vendorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Vendor not found"));
    }

    // TODO: NOT DONE
    public Map<String, Object> getVendorProfile(@RequestParam Integer id) {
        Vendor vendor = vendorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Vendor not found"));

        Map<String, Object> response = new HashMap<>();

//        String vendorLogoImgBase64 = vendorService.getVendorLogoBase64(vendor);
//        List<VendorCategory> allCategories = vendorService.getAllVendorCategories();
//        int activityCount = vendorService.getActivityCountByVendor(vendor.getId());

//        response.put("vendor", vendor);
//        response.put("allcategory", allCategories);
//        response.put("vendorLogoImgBase64", vendorLogoImgBase64);
//        response.put("activityCount", activityCount);

        return response;
    }
}
