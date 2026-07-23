package petTopia.controller.vendor_admin;

import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import petTopia.model.vendor.VendorActivityReview;
import petTopia.service.vendor_admin.VendorActivityReviewService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/vendor_admin")
public class VendorActivityReviewsController {
    private final VendorActivityReviewService vendorActivityReviewService;

    @GetMapping("/activityreviews")
    public ResponseEntity<?> getReviews(@RequestParam Integer vendorActivityId) {
        List<VendorActivityReview> vendorActivityReviews = vendorActivityReviewService.getReviewsByActivityId(vendorActivityId);
        return ResponseEntity.ok(vendorActivityReviews);
    }

    @DeleteMapping("/activityreviews/delete/{reviewId}")
    public ResponseEntity<?> deleteReview(@PathVariable Integer reviewId) {
        vendorActivityReviewService.deleteReviewById(reviewId);
        return ResponseEntity.ok(Map.of("message", "刪除成功"));
    }
}
