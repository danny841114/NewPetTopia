package petTopia.controller.vendor_admin;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import petTopia.dto.vendor_admin.CertificationDTO;
import petTopia.model.vendor.VendorCertification;
import petTopia.service.vendor_admin.VendorCertificationService;

@RequiredArgsConstructor
@RestController
public class ManageCertificationController {
    private final VendorCertificationService vendorCertificationService;

    @GetMapping("/api/admin/certification")
    public ResponseEntity<?> getAllCertificationsWithTags() {
        List<CertificationDTO> certificationsWithTags = vendorCertificationService.getAllCertificationsWithTags();
        return ResponseEntity.ok(certificationsWithTags);
    }

    @PutMapping("/api/admin/certification/status/update/{certificationId}")
    public ResponseEntity<VendorCertification> updateCertificationStatus(@PathVariable Integer certificationId,
                                                                         @RequestParam String status,
                                                                         @RequestParam String reason) {
        VendorCertification updatedCertification = vendorCertificationService.updateCertificationStatus(certificationId, status, reason);
        return ResponseEntity.ok(updatedCertification);
    }
}
