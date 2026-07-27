package petTopia.service.vendor;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import petTopia.model.vendor.VendorCertificationTag;
import petTopia.repository.vendor_admin.VendorCertificationTagRepository;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class VendorTagService {
    private final VendorCertificationTagRepository vendorCertificationTagRepository;

    public List<VendorCertificationTag> findConfirmedTagByVendorId(Integer vendorId) {
        return vendorCertificationTagRepository.findByVendorIdAndCertificationCertificationStatus(vendorId, "已認證");
    }
}
