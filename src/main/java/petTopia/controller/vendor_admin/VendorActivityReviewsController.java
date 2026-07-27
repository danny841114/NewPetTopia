package petTopia.controller.vendor_admin;

import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import petTopia.dto.vendor.ActivityReviewDto;
import petTopia.service.vendor.VendorActivityReviewService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/vendor_admin")
public class VendorActivityReviewsController {
    private final VendorActivityReviewService vendorActivityReviewService;

    @GetMapping("/activityreviews")
    public ResponseEntity<List<ActivityReviewDto>> getReviews(@RequestParam Integer vendorActivityId) {
        List<ActivityReviewDto> reviews = vendorActivityReviewService.getReviewsByActivityId(vendorActivityId);
        return ResponseEntity.ok(reviews);
    }

    @DeleteMapping("/activityreviews/delete/{reviewId}")
    public ResponseEntity<?> deleteReview(@PathVariable Integer reviewId) {
        vendorActivityReviewService.deleteReviewById(reviewId);
        return ResponseEntity.ok(Map.of("message", "刪除成功"));
    }
}
