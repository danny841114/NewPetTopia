package petTopia.controller.vendor_admin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import petTopia.model.vendor.CertificationTag;
import petTopia.model.vendor.VendorCertification;
import petTopia.model.vendor.VendorCertificationTag;
import petTopia.repository.vendor_admin.VendorCertificationRepository;
import petTopia.repository.vendor_admin.VendorCertificationTagRepository;
import petTopia.service.vendor_admin.VendorCertificationService;
import petTopia.service.vendor_admin.VendorCertificationTagService;

@RequiredArgsConstructor
@RestController
public class VendorCertificationController {
    private final VendorCertificationService vendorCertificationService;
    private final VendorCertificationTagService vendorCertificationTagService;

    private final VendorCertificationTagRepository vendorCertificationTagRepository;
    private final VendorCertificationRepository vendorCertificationRepository;

    @PostMapping("/api/vendor_admin/certification/add")
    public ResponseEntity<String> AddCertification(@RequestParam Integer vendorId, @RequestParam Integer tagId) {
        try {
            vendorCertificationService.createVendorCertificationWithTag(vendorId, tagId);
            return ResponseEntity.ok("申请已提交成功！");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("申请提交失败，请稍后重试！");
        }
    }

    @GetMapping("/api/vendor_admin/certification/exists/{vendorId}/{certificationTagId}")
    public ResponseEntity<Map<String, Object>> checkIfExists(@PathVariable Integer vendorId,
                                                             @PathVariable Integer certificationTagId) {
        boolean exists = vendorCertificationTagService.checkIfExists(vendorId, certificationTagId);
        return ResponseEntity.ok(Map.of("exists", exists));
    }

    // 新增 API 來獲取所有認證語
    @GetMapping("/api/certification_type/all")
    public ResponseEntity<List<CertificationTag>> getAllCertificationTypes() {
        try {
            List<CertificationTag> certificationTypes = vendorCertificationService.getAllCertificationTypes();
            return ResponseEntity.ok(certificationTypes);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // TODO: this method is not modified
    @GetMapping("/api/vendor_admin/certification/{vendorId}")
    public ResponseEntity<List<Map<String, Object>>> getCertificationByVendorId(@PathVariable Integer vendorId) {
        List<VendorCertificationTag> vendorCertificationTags = vendorCertificationTagRepository.findByVendorId(vendorId);

        // 提取 certification_id 列表
        List<Integer> certificationIds = vendorCertificationTags.stream()
                .map(tag -> tag.getCertification().getId())
                .distinct()
                .collect(Collectors.toList());

        // 透過 certification_id 查找對應的 VendorCertification
        List<VendorCertification> vendorCertifications = vendorCertificationRepository.findByIdIn(certificationIds);

        // 组合数据结构
        List<Map<String, Object>> response = vendorCertifications.stream().map(cert -> {
            Map<String, Object> certMap = new HashMap<>();
            certMap.put("id", cert.getId());
            certMap.put("vendor", cert.getVendor());
            certMap.put("certificationStatus", cert.getCertificationStatus());
            certMap.put("reason", cert.getReason());
            certMap.put("requestDate", cert.getRequestDate());
            certMap.put("approvedDate", cert.getApprovedDate());

            // 找到对应的 VendorCertificationTag（只有一个）
            vendorCertificationTags.stream()
                    .filter(tag -> tag.getCertification().getId().equals(cert.getId()))
                    .findFirst()  // 只取第一个
                    .ifPresent(tag -> {
                        Map<String, Object> tagMap = new HashMap<>();
                        tagMap.put("id", tag.getId());
                        tagMap.put("tag", tag.getTag());
                        tagMap.put("meetsStandard", tag.isMeetsStandard());
                        certMap.put("tag", tagMap); // 直接存单个对象
                    });

            return certMap;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/api/vendor_admin/certification/delete/{certificationId}")
    public ResponseEntity<String> cancelCertification(@PathVariable Integer certificationId) {
        if (!vendorCertificationRepository.existsById(certificationId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("認證申請不存在");
        }

        vendorCertificationRepository.deleteById(certificationId);
        return ResponseEntity.ok("認證申請已取消");
    }
}
