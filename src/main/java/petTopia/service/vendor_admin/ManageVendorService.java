package petTopia.service.vendor_admin;

import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import petTopia.model.vendor.Vendor;
import petTopia.repository.vendor.VendorRepository;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class ManageVendorService {
    private final VendorRepository vendorRepository;

    public List<Vendor> getAllVendors(Integer categoryId, Boolean status) {
        if (categoryId == null && status == null) {
            return vendorRepository.findAll();
        } else if (categoryId != null && status == null) {
            return vendorRepository.findByVendorCategoryId(categoryId);
        } else if (categoryId == null && status != null) {
            return vendorRepository.findByStatus(status);
        } else {
            return vendorRepository.findByVendorCategoryIdAndStatus(categoryId, status);
        }
    }

    @Transactional
    public boolean updateVendorStatus(Integer id, boolean status) {
        Optional<Vendor> vendorOptional = vendorRepository.findById(id);

        if (vendorOptional.isPresent()) {
            Vendor vendor = vendorOptional.get();
            vendor.setStatus(status);

            vendorRepository.save(vendor);
            return true;
        } else {
            return false;
        }
    }

    @Transactional
    public boolean bulkUpdateVendorStatus(List<Integer> vendorIds, boolean status) {
        if (vendorIds == null || vendorIds.isEmpty()) {
            vendorRepository.updateAllVendorStatus(status);
        } else {
            vendorRepository.updateVendorStatusByIds(vendorIds, status);
        }

        return true;
    }
}