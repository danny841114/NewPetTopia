package petTopia.controller.vendor_admin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import petTopia.model.vendor.Vendor;
import petTopia.model.vendor.VendorCategory;
import petTopia.model.vendor.VendorImages;
import petTopia.repository.vendor.VendorCategoryRepository;
import petTopia.repository.vendor.VendorImagesRepository;
import petTopia.repository.vendor.VendorRepository;
import petTopia.service.vendor_admin.VendorServiceAdmin;

@Slf4j
@RequiredArgsConstructor
@RestController
public class VendorProfileController {
    private final VendorServiceAdmin vendorService;
    private final VendorRepository vendorRepository;
    private final VendorCategoryRepository categoryRepository;
    private final VendorImagesRepository vendorImagesRepository;

    @GetMapping("api/vendor_admin/status/{vendorId}")
    public ResponseEntity<?> getVendorStatus(@PathVariable Integer vendorId) {
        Optional<Vendor> statusOptional = vendorService.getVendorStatus(vendorId);

        if (statusOptional.isPresent()) {
            boolean status = statusOptional.get().isStatus();
            return ResponseEntity.ok(Map.of("status", status));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "找不到該店家"));
        }
    }

    @GetMapping("api/vendor_admin/profile/{id}")
    public ResponseEntity<?> getVendorById(@PathVariable Integer id) {
        Optional<Vendor> vendor = vendorRepository.findById(id);
        return ResponseEntity.ok(Map.of("vendor", vendor));
    }

    @GetMapping("api/vendor_admin/profile")
    public ResponseEntity<Map<String, Object>> getVendorProfile(@RequestParam Integer id) {
        Optional<Vendor> vendorDetail = vendorRepository.findById(id);

        Map<String, Object> response = new HashMap<>();

        if (vendorDetail.isPresent()) {
            Vendor vendor = vendorDetail.get();
            String vendorLogoImgBase64 = vendorService.getVendorLogoBase64(vendor);
            List<VendorCategory> allCategories = vendorService.getAllVendorCategories();
            int activityCount = vendorService.getActivityCountByVendor(vendor.getId());

            response.put("vendor", vendor);
            response.put("allcategory", allCategories);
            response.put("vendorLogoImgBase64", vendorLogoImgBase64);
            response.put("activityCount", activityCount);

            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping("/api/vendor/update/{vendorId}")
    public ResponseEntity<Map<String, Object>> updateVendor(@PathVariable Integer vendorId,
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
                                                            @RequestParam(value = "deletedImageIds", required = false) List<Integer> deletedImageIds,
                                                            Model model) throws IOException {
        Map<String, Object> response = new HashMap<>();

        Vendor vendor = vendorService.getVendorById(vendorId)
                .orElseThrow(() -> new EntityNotFoundException("Vendor not found"));

        if (vendorName != null) vendor.setName(vendorName);
        if (contactEmail != null) vendor.setContactEmail(contactEmail);
        if (vendorPhone != null) vendor.setPhone(vendorPhone);
        if (contactPerson != null) vendor.setContactPerson(contactPerson);
        if (vendorAddress != null) vendor.setAddress(vendorAddress);
        if (vendorDescription != null) vendor.setDescription(vendorDescription);
        if (vendorTaxidNumber != null) vendor.setTaxidNumber(vendorTaxidNumber);

        if (vendorLogoImg != null && !vendorLogoImg.isEmpty()) {
            try {
                vendor.setLogoImg(vendorLogoImg.getBytes());
            } catch (IOException e) {
                log.error("Get byte array from vendor logo image failed", e);
                return ResponseEntity.internalServerError().body(Map.of("error", "Image upload failed"));
            }
        }

        if (category != null) {
            VendorCategory vendorCategory = categoryRepository.findById(category)
                    .orElseThrow(() -> new EntityNotFoundException("Category not found"));
            vendor.setVendorCategory(vendorCategory);
        }

        Vendor updatedVendor = vendorService.updateVendor(vendor);

        if (deletedImageIds != null && !deletedImageIds.isEmpty()) {
            vendorImagesRepository.deleteAllById(deletedImageIds);
        }

        if (files != null && files.length > 0) {
            List<VendorImages> vendorImagesList = new ArrayList<>();
            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    VendorImages vendorImage = new VendorImages();

                    vendorImage.setImage(file.getBytes());
                    vendorImage.setVendor(vendor);

                    vendorImagesList.add(vendorImage);
                }
            }

            vendor.getVendorImages().addAll(vendorImagesList);
            vendorImagesRepository.saveAll(vendorImagesList);
        }

        response.put("success", true);
        response.put("vendor", updatedVendor);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/profileImage/{vendorId}")
    public ResponseEntity<byte[]> getProfileImage(@PathVariable Integer vendorId) {
        Vendor vendor = vendorService.getVendorById(vendorId)
                .orElseThrow(() -> new EntityNotFoundException("Vendor not found"));

        byte[] imageBytes = vendor.getLogoImg();
        if (imageBytes == null || imageBytes.length == 0) {
            return ResponseEntity.notFound().build();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "image/jpeg");

        return ResponseEntity.ok().headers(headers).body(imageBytes);
    }

    @GetMapping("/profile_photos/download")
    public ResponseEntity<?> downloadPhotoById(@RequestParam Integer photoId) {
        Optional<VendorImages> imageOpt = vendorImagesRepository.findById(photoId);

        if (imageOpt.isPresent()) {
            byte[] imageFile = imageOpt.get().getImage();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG);

            return ResponseEntity.ok().headers(headers).body(imageFile);
        }

        return ResponseEntity.notFound().build();
    }

    @GetMapping("/profile_photos/ids")
    public ResponseEntity<List<Integer>> findPhotoIdsByVendorId(@RequestParam Integer vendorId) {
        Optional<Vendor> op = vendorRepository.findById(vendorId);

        if (op.isPresent()) {
            List<Integer> imageIdList = new ArrayList<>();

            List<VendorImages> images = op.get().getVendorImages();
            images.forEach(image -> imageIdList.add(image.getId()));

            return ResponseEntity.ok(imageIdList);
        }

        return ResponseEntity.notFound().build();
    }

    @GetMapping("/api/vendor/{vendorId}/slogans")
    public ResponseEntity<List<String>> getCertifiedSlogansByVendorId(@PathVariable Integer vendorId) {
        List<String> slogans = vendorService.getSlogansByVendorId(vendorId);
        return ResponseEntity.ok(slogans);
    }
}
