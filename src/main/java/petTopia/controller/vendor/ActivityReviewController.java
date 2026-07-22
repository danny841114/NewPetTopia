package petTopia.controller.vendor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import petTopia.dto.vendor.ActivityReviewDto;
import petTopia.model.vendor.VendorActivityReview;
import petTopia.service.vendor.VendorActivityReviewService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/activity")
public class ActivityReviewController {
    private final VendorActivityReviewService vendorActivityReviewService;

    @GetMapping("/{activityId}/review")
    public ResponseEntity<List<ActivityReviewDto>> getActivityReview(@PathVariable Integer activityId) {
        List<ActivityReviewDto> reviewList = vendorActivityReviewService.findReviewListByActivityId(activityId);
        return ResponseEntity.ok(reviewList);
    }

    @GetMapping("/review/{reviewId}")
    public ResponseEntity<?> getActivityReviewById(@PathVariable Integer reviewId) {
        VendorActivityReview review = vendorActivityReviewService.findReviewById(reviewId);
        return ResponseEntity.ok(Map.of("review", review));
    }

    @PostMapping("/{activityId}/review/add")
    public ResponseEntity<?> addReview(@PathVariable Integer activityId, @RequestBody Map<String, String> data) {
        Integer memberId = Integer.parseInt(data.get("memberId"));
        String content = data.get("content");
        VendorActivityReview review = vendorActivityReviewService.addReview(memberId, activityId, content);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("review", review);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/review/{reviewId}/rewrite")
    public ResponseEntity<?> rewriteReview(@PathVariable Integer reviewId, @RequestBody Map<String, String> data) {
        String content = data.get("content");
        VendorActivityReview review = vendorActivityReviewService.rewriteReviewById(reviewId, content);
        return ResponseEntity.ok(Map.of("review", review));
    }

    @DeleteMapping("/review/{reviewId}/delete")
    public ResponseEntity<?> deleteReview(@PathVariable Integer reviewId) {
        vendorActivityReviewService.deleteReviewById(reviewId);
        return ResponseEntity.ok(Map.of("success", true));
    }

    @GetMapping("/{activityId}/member/{memberId}/review/exist")
    public ResponseEntity<?> getReviewIsExisted(@PathVariable Integer activityId, @PathVariable Integer memberId) {
        boolean isExisted = vendorActivityReviewService.getReviewIsExisted(memberId, activityId);
        return ResponseEntity.ok(Map.of("action", isExisted));
    }
}
