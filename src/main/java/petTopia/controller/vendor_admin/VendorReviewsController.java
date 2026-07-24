package petTopia.controller.vendor_admin;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import petTopia.dto.vendor_admin.request.AddReviewRequest;
import petTopia.model.vendor.ReviewPhoto;
import petTopia.model.vendor.VendorReview;
import petTopia.repository.vendor.ReviewPhotoRepository;
import petTopia.repository.vendor.VendorReviewRepository;
import petTopia.service.vendor_admin.VendorReviewsServiceAdmin;

@Slf4j
@RequiredArgsConstructor
@RestController
public class VendorReviewsController {
    private final VendorReviewsServiceAdmin vendorReviewsService;
    private final VendorReviewRepository vendorReviewRepository;
    private final ReviewPhotoRepository reviewPhotoRepository;

    @GetMapping("/api/vendor_admin/reviews/{vendorId}")
    public ResponseEntity<?> getAllReviews() {
        List<VendorReview> reviews = vendorReviewRepository.findAll();
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/api/vendor_admin/review")
    public ResponseEntity<?> getReviewsByVendorId(@RequestParam Integer vendorId) {
        List<VendorReview> vendorReviews = vendorReviewsService.getReviewsByVendorId(vendorId);
        return ResponseEntity.ok(vendorReviews);
    }

    @GetMapping("/api/vendor_admin/review/photos/{reviewId}")
    public List<ReviewPhoto> getPhotosByReviewId(@PathVariable Integer reviewId) {
        return vendorReviewsService.getPhotosByReviewId(reviewId);
    }

    @GetMapping("/review_photos/ids")
    public ResponseEntity<?> findPhotoIds(@RequestParam Integer vendorReviewId) {
        List<Integer> photoIds = vendorReviewsService.findPhotoIdsByReviewId(vendorReviewId);
        return ResponseEntity.ok(photoIds);
    }

    @GetMapping("/review_photos/download")
    public ResponseEntity<?> downloadPhoto(@RequestParam Integer photoId) {
        byte[] photoByteArray = vendorReviewsService.downloadPhotoById(photoId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);

        return ResponseEntity.ok().headers(headers).body(photoByteArray);
    }

    // TODO: API REQUEST BODY to MODEL ATTRIBUTE
    @PostMapping("/api/vendor_admin/review/add")
    public ResponseEntity<?> addReview(@RequestBody AddReviewRequest request,
                                       @RequestPart(value = "photo", required = false) MultipartFile photo) throws IOException {
        vendorReviewsService.addReview(request, photo);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // TODO: API RESPONSE SHOULD CHANGE
    @DeleteMapping("/api/vendor_admin/review/delete/{reviewId}")
    public ResponseEntity<?> deleteReview(@PathVariable Integer reviewId) {
        vendorReviewsService.deleteReview(reviewId);
        return ResponseEntity.ok(Map.of("message", "刪除成功"));
    }
}
