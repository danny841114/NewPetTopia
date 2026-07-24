package petTopia.controller.vendor_admin;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import petTopia.dto.vendor_admin.request.UpdateVendorRequest;
import petTopia.dto.vendor_admin.response.UpdateVendorResponse;
import petTopia.dto.vendor_admin.response.VendorProfile;
import petTopia.model.vendor.Vendor;
import petTopia.service.vendor_admin.VendorProfileService;
import petTopia.service.vendor_admin.VendorServiceAdmin;

@Slf4j
@RequiredArgsConstructor
@RestController
public class VendorProfileController {
    private final VendorServiceAdmin vendorService;
    private final VendorProfileService vendorProfileService;

    // TODO:
    //  CHANGE RESPONSE DATA
    @GetMapping("api/vendor_admin/status/{vendorId}")
    public ResponseEntity<Map<String, Object>> getVendorStatus(@PathVariable Integer vendorId) {
        Boolean vendorStatus = vendorService.getVendorStatus(vendorId);
        return ResponseEntity.ok(Map.of("status", vendorStatus));
    }

    // TODO:
    //  CHANGE RESPONSE TO DTO
    //  CHANGE RESPONSE DATA
    @GetMapping("api/vendor_admin/profile/{id}")
    public ResponseEntity<Map<String, Object>> getVendor(@PathVariable Integer id) {
        Vendor vendor = vendorProfileService.getVendorById(id);
        return ResponseEntity.ok(Map.of("vendor", vendor));
    }

    @GetMapping("api/vendor_admin/profile")
    public ResponseEntity<VendorProfile> getVendorProfile(@RequestParam Integer id) {
        VendorProfile vendorProfile = vendorProfileService.getVendorProfile(id);
        return ResponseEntity.ok(vendorProfile);
    }

    // TODO:
    //  CHANGE RESPONSE TO DTO
    //  CHANGE RESPONSE DATA
    @PostMapping("/api/vendor/update/{vendorId}")
    public ResponseEntity<UpdateVendorResponse> updateVendor(@PathVariable Integer vendorId,
                                                             @RequestParam(required = false) String vendorName,
                                                             @RequestParam(required = false) String contactEmail,
                                                             @RequestParam(required = false) String vendorPhone,
                                                             @RequestParam(required = false) String vendorAddress,
                                                             @RequestParam(required = false) String vendorDescription,
                                                             @RequestParam(required = false) String contactPerson,
                                                             @RequestParam(required = false) String vendorTaxidNumber,
                                                             @RequestParam(required = false) Integer category,
                                                             @RequestParam(required = false) MultipartFile vendorLogoImg,
                                                             @RequestParam(value = "files", required = false) MultipartFile[] files,
                                                             @RequestParam(value = "deletedImageIds", required = false) List<Integer> deletedImageIds) throws IOException {

        UpdateVendorRequest request = new UpdateVendorRequest();

        request.setVendorName(vendorName);
        request.setContactEmail(contactEmail);
        request.setVendorPhone(vendorPhone);
        request.setVendorAddress(vendorAddress);
        request.setVendorDescription(vendorDescription);
        request.setContactPerson(contactPerson);
        request.setVendorTaxIdNumber(vendorTaxidNumber);
        request.setCategoryId(category);
        request.setVendorLogoImg(vendorLogoImg);
        request.setFiles(files);
        request.setDeletedImageIds(deletedImageIds);

        Vendor savedVendor = vendorProfileService.updateVendor(vendorId, request);

        UpdateVendorResponse response = UpdateVendorResponse.builder()
                .success(true)
                .vendor(savedVendor)
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/profileImage/{vendorId}")
    public ResponseEntity<byte[]> getProfileImage(@PathVariable Integer vendorId) {
        byte[] photoByteArray = vendorService.getVendorLogoImgByVendorId(vendorId);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "image/jpeg");

        return ResponseEntity.ok().headers(headers).body(photoByteArray);
    }

    @GetMapping("/profile_photos/download")
    public ResponseEntity<byte[]> downloadPhotoById(@RequestParam Integer photoId) {
        byte[] photoByteArray = vendorProfileService.downloadPhotoById(photoId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);

        return ResponseEntity.ok().headers(headers).body(photoByteArray);
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
