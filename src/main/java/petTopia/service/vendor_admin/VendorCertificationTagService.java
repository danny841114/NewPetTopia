package petTopia.service.vendor_admin;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import petTopia.repository.vendor_admin.VendorCertificationTagRepository;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class VendorCertificationTagService {
    private final VendorCertificationTagRepository vendorCertificationTagRepository;

    public boolean checkIfExists(int vendorId, int certificationTagId) {
        return vendorCertificationTagRepository.existsByVendorIdAndTagId(vendorId, certificationTagId);
    }
}
