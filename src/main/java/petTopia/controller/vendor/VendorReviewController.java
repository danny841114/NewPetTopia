package petTopia.controller.vendor;

import java.io.IOException;
import java.util.*;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import petTopia.dto.vendor.VendorReviewDto;
import petTopia.model.vendor.ReviewPhoto;
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
    public ResponseEntity<List<VendorReviewDto>> getVendorReview(@PathVariable Integer vendorId) {
        List<VendorReviewDto> reviewList = vendorReviewService.findReviewListByVendorId(vendorId);
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
    public ResponseEntity<List<ReviewPhoto>> getReviewPhoto(@PathVariable Integer reviewId) {
        List<ReviewPhoto> photoList = reviewPhotoService.findPhotoListByReviewId(reviewId);
        return ResponseEntity.ok(photoList);
    }

    @PostMapping(value = "/{vendorId}/review/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> giveReview(@PathVariable Integer vendorId,
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

    @PostMapping(value = "/{vendorId}/review/star/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> giveReviewStar(@PathVariable Integer vendorId,
                                            @RequestParam Integer memberId,
                                            @RequestParam Integer ratingEnv,
                                            @RequestParam Integer ratingPrice,
                                            @RequestParam Integer ratingService) {
        VendorReview starReview = vendorReviewService.addStarReview(memberId, vendorId, ratingEnv, ratingPrice, ratingService);
        return ResponseEntity.ok(Map.of("review", starReview));
    }

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

    // TODO: Request body should be rearranged
    @PostMapping(value = "/{vendorId}/review/add/final", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> addReview(@PathVariable Integer vendorId,
                                       @RequestParam Integer memberId,
                                       @RequestParam String content,
                                       Integer ratingEnv,
                                       Integer ratingPrice,
                                       Integer ratingService,
                                       @RequestPart(required = false) List<MultipartFile> reviewPhotos) throws IOException {
        if (reviewPhotos == null) reviewPhotos = new ArrayList<>();

        VendorReview review = vendorReviewService.addNewReview(
                memberId,
                vendorId,
                content,
                ratingEnv,
                ratingPrice,
                ratingService,
                reviewPhotos
        );

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("review", review);

        return ResponseEntity.ok(response);
    }

    // TODO: Request body should be rearranged
    @PutMapping(value = "/review/{reviewId}/rewrite/final", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> modifyReview(@PathVariable Integer reviewId,
                                          @RequestParam String content,
                                          Integer ratingEnv,
                                          Integer ratingPrice,
                                          Integer ratingService,
                                          @RequestPart(required = false) List<MultipartFile> reviewPhotos,
                                          @RequestParam(required = false) List<Integer> deletePhotoIds) throws IOException {
        if (reviewPhotos == null) reviewPhotos = new ArrayList<>();
        if (deletePhotoIds == null) deletePhotoIds = new ArrayList<>();

        VendorReview review = vendorReviewService.modifyReview(
                reviewId,
                content,
                ratingEnv,
                ratingPrice,
                ratingService,
                reviewPhotos,
                deletePhotoIds
        );

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("review", review);

        return ResponseEntity.ok(response);
    }
}
