package petTopia.controller.vendor_admin;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import petTopia.dto.vendor_admin.request.UpdateVendorRequest;
import petTopia.dto.vendor_admin.response.VendorProfile;
import petTopia.model.vendor.Vendor;
import petTopia.service.vendor_admin.VendorProfileService;
import petTopia.service.vendor_admin.VendorServiceAdmin;
import petTopia.util.HeadersUtil;

@Slf4j
@RequiredArgsConstructor
@RestController
public class VendorProfileController {
    private final VendorServiceAdmin vendorService;
    private final VendorProfileService vendorProfileService;

    // TODO: CHANGE RESPONSE DATA
    @GetMapping("/api/vendor_admin/status/{vendorId}")
    public ResponseEntity<Map<String, Object>> getVendorStatus(@PathVariable Integer vendorId) {
        Boolean vendorStatus = vendorService.getVendorStatus(vendorId);
        return ResponseEntity.ok(Map.of("status", vendorStatus));
    }

    // TODO: CHANGE RESPONSE DATA
    @GetMapping("/api/vendor_admin/profile/{id}")
    public ResponseEntity<Map<String, Object>> getVendor(@PathVariable Integer id) {
        Vendor vendor = vendorProfileService.getVendorById(id);
        return ResponseEntity.ok(Map.of("vendor", vendor));
    }

    @GetMapping("/api/vendor_admin/profile")
    public ResponseEntity<VendorProfile> getVendorProfile(@RequestParam Integer vendorId) {
        VendorProfile vendorProfile = vendorProfileService.getVendorProfile(vendorId);
        return ResponseEntity.ok(vendorProfile);
    }

    @PutMapping("/api/vendor/update/{vendorId}")
    public ResponseEntity<HashMap<String, Object>> updateVendor(@PathVariable Integer vendorId,
                                                                @ModelAttribute UpdateVendorRequest request) throws IOException {
        Vendor savedVendor = vendorProfileService.updateVendor(vendorId, request);

        HashMap<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("vendor", savedVendor);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/profileImage/{vendorId}")
    public ResponseEntity<byte[]> getProfileImage(@PathVariable Integer vendorId) {
        byte[] photoByteArray = vendorService.getVendorLogoImgByVendorId(vendorId);
        return ResponseEntity.ok()
                .headers(HeadersUtil.createHeadersWithMediaTypeJpg())
                .body(photoByteArray);
    }

    @GetMapping("/profile_photos/download")
    public ResponseEntity<byte[]> downloadPhotoById(@RequestParam Integer photoId) {
        byte[] photoByteArray = vendorProfileService.downloadPhotoById(photoId);
        return ResponseEntity.ok()
                .headers(HeadersUtil.createHeadersWithMediaTypeJpg())
                .body(photoByteArray);
    }

    @GetMapping("/profile_photos/ids")
    public ResponseEntity<List<Integer>> findPhotoIds(@RequestParam Integer vendorId) {
        List<Integer> photoIds = vendorProfileService.findPhotoIdsByVendorId(vendorId);
        return ResponseEntity.ok(photoIds);
    }

    @GetMapping("/api/vendor/{vendorId}/slogans")
    public ResponseEntity<List<String>> getCertifiedSlogans(@PathVariable Integer vendorId) {
        List<String> slogans = vendorService.getSlogansByVendorId(vendorId);
        return ResponseEntity.ok(slogans);
    }
}
