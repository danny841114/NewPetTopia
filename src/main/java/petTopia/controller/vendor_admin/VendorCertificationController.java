package petTopia.controller.vendor_admin;

import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import petTopia.dto.vendor_admin.VendorCertificationDto;
import petTopia.model.vendor.CertificationTag;
import petTopia.service.vendor_admin.VendorCertificationService;
import petTopia.service.vendor_admin.VendorCertificationTagService;

@RequiredArgsConstructor
@RestController
public class VendorCertificationController {
    private final VendorCertificationService vendorCertificationService;
    private final VendorCertificationTagService vendorCertificationTagService;

    // TODO: Response body should be modified
    @PostMapping("/api/vendor_admin/certification/add")
    public ResponseEntity<String> AddCertification(@RequestParam Integer vendorId, @RequestParam Integer tagId) {
        vendorCertificationService.createVendorCertificationWithTag(vendorId, tagId);
        return ResponseEntity.ok("申请已提交成功！");
    }

    // TODO: Response body should be modified
    @GetMapping("/api/vendor_admin/certification/exists/{vendorId}/{certificationTagId}")
    public ResponseEntity<Map<String, Object>> checkIfExists(@PathVariable Integer vendorId,
                                                             @PathVariable Integer certificationTagId) {
        boolean exists = vendorCertificationTagService.checkIfExists(vendorId, certificationTagId);
        return ResponseEntity.ok(Map.of("exists", exists));
    }

    @GetMapping("/api/certification_type/all")
    public ResponseEntity<List<CertificationTag>> getAllCertificationTypes() {
        List<CertificationTag> certificationTypes = vendorCertificationService.getAllCertificationTypes();
        return ResponseEntity.ok(certificationTypes);
    }

    // TODO: this method is not modified
    @GetMapping("/api/vendor_admin/certification/{vendorId}")
    public ResponseEntity<List<VendorCertificationDto>> getCertificationByVendorId(@PathVariable Integer vendorId) {
        List<VendorCertificationDto> certificationDto = vendorCertificationService.getCertificationByVendorId(vendorId);
        return ResponseEntity.ok(certificationDto);
    }

    // TODO: Response body should be modified
    @DeleteMapping("/api/vendor_admin/certification/delete/{certificationId}")
    public ResponseEntity<String> cancelCertification(@PathVariable Integer certificationId) {
        vendorCertificationService.cancelCertificationById(certificationId);
        return ResponseEntity.ok("認證申請已取消");
    }
}
