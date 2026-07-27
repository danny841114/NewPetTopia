package petTopia.controller.vendor;

import java.io.IOException;
import java.util.*;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import petTopia.dto.vendor.VendorDetail;
import petTopia.dto.vendor.VendorReviewPhotoDto;
import petTopia.dto.vendor.request.AddReviewRequest;
import petTopia.dto.vendor.request.AddReviewStarRequest;
import petTopia.dto.vendor.request.ModifyReviewRequest;
import petTopia.model.vendor.Vendor;
import petTopia.model.vendor.VendorReview;
import petTopia.service.vendor.ReviewPhotoService;
import petTopia.service.vendor.VendorReviewService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/vendor")
public class VendorReviewController {
    private final VendorReviewService vendorReviewService;
    private final ReviewPhotoService reviewPhotoService;

    @GetMapping("/{vendorId}/review")
    public ResponseEntity<List<VendorDetail>> getVendorReview(@PathVariable Integer vendorId) {
        List<VendorDetail> reviewList = vendorReviewService.findReviewListByVendorId(vendorId);
        return ResponseEntity.ok(reviewList);
    }

    @GetMapping("/{vendorId}/update/rating")
    public ResponseEntity<Vendor> updateVendorRating(@PathVariable Integer vendorId) {
        Vendor vendor = vendorReviewService.setAverageRating(vendorId);
        return ResponseEntity.ok(vendor);
    }

    @GetMapping("/review/{reviewId}")
    public ResponseEntity<?> getVendorReviewById(@PathVariable Integer reviewId) {
        VendorReview review = vendorReviewService.findReviewById(reviewId);
        return ResponseEntity.ok(Map.of("review", review));
    }

    @GetMapping("/review/{reviewId}/photo")
    public ResponseEntity<List<VendorReviewPhotoDto>> getReviewPhoto(@PathVariable Integer reviewId) {
        List<VendorReviewPhotoDto> photos = reviewPhotoService.findPhotoListByReviewId(reviewId);
        return ResponseEntity.ok(photos);
    }

    @PostMapping(value = "/{vendorId}/review/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> addReview(@PathVariable Integer vendorId,
                                       @RequestParam Integer memberId,
                                       @RequestParam String content,
                                       @RequestPart(required = false) List<MultipartFile> reviewPhotos) throws IOException {
        if (reviewPhotos != null) reviewPhotos = Collections.emptyList();
        VendorReview review = vendorReviewService.addReview(memberId, vendorId, content, reviewPhotos);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("review", review);

        return ResponseEntity.ok(response);
    }

    // TODO: Change to request body
    @PostMapping(value = "/{vendorId}/review/star/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> addReviewStar(@PathVariable Integer vendorId, @ModelAttribute AddReviewStarRequest request) {
        VendorReview starReview = vendorReviewService.addStarReview(vendorId, request);
        return ResponseEntity.ok(Map.of("review", starReview));
    }

    // TODO: Change to request body
    @PostMapping(value = "/review/{reviewId}/rewrite", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> rewriteTextReview(@PathVariable Integer reviewId, @RequestParam String content) {
        VendorReview review = vendorReviewService.rewriteReviewById(reviewId, content);
        return ResponseEntity.ok(Map.of("review", review));
    }

    @DeleteMapping("/review/{reviewId}/delete")
    public ResponseEntity<?> deleteReview(@PathVariable Integer reviewId) {
        vendorReviewService.deleteReviewById(reviewId);
        return ResponseEntity.ok(Map.of("success", true));
    }

    @GetMapping("/{vendorId}/member/{memberId}/review/exist")
    public ResponseEntity<?> getReviewIsExisted(@PathVariable Integer vendorId, @PathVariable Integer memberId) {
        boolean isExisted = vendorReviewService.getReviewIsExisted(memberId, vendorId);
        return ResponseEntity.ok(Map.of("action", isExisted));
    }

    // TODO: Move vendorId to form-data
    @PostMapping(value = "/{vendorId}/review/add/final", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> addReview(@PathVariable Integer vendorId,
                                       @ModelAttribute AddReviewRequest request) throws IOException {
        VendorReview review = vendorReviewService.addNewReview(vendorId, request);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("review", review);

        return ResponseEntity.ok(response);
    }

    // TODO: Request body should be rearranged
    @PutMapping(value = "/review/{reviewId}/rewrite/final", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> modifyReview(@PathVariable Integer reviewId,
                                          @ModelAttribute ModifyReviewRequest request) throws IOException {
        VendorReview review = vendorReviewService.modifyReview(reviewId, request);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("review", review);

        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/review/img/{id}", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> getVendorReviewImage(@PathVariable Integer id) {
        byte[] reviewImage = reviewPhotoService.findById(id);
        return ResponseEntity.ok(reviewImage);
    }
}
