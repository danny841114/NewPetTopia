package petTopia.controller.vendor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import petTopia.dto.vendor.ActivityReviewDetail;
import petTopia.dto.vendor.ActivityReviewDto;
import petTopia.dto.vendor.request.AddActivityReviewRequest;
import petTopia.model.vendor.VendorActivityReview;
import petTopia.service.vendor.VendorActivityReviewService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/activity")
public class ActivityReviewController {
    private final VendorActivityReviewService vendorActivityReviewService;

    @GetMapping("/{activityId}/review")
    public ResponseEntity<List<ActivityReviewDetail>> getActivityReview(@PathVariable Integer activityId) {
        List<ActivityReviewDetail> reviewList = vendorActivityReviewService.findReviewListByActivityId(activityId);
        return ResponseEntity.ok(reviewList);
    }

    // TODO: Modify response body
    @GetMapping("/review/{reviewId}")
    public ResponseEntity<?> getActivityReviewById(@PathVariable Integer reviewId) {
        ActivityReviewDto review = vendorActivityReviewService.findReviewById(reviewId);
        return ResponseEntity.ok(Map.of("review", review));
    }

    // TODO:
    //  Modify request / response body
    //  activityId -> request body
    //  memberId -> credential
    @PostMapping("/{activityId}/review/add")
    public ResponseEntity<?> addReview(@PathVariable Integer activityId,
                                       @Valid @RequestBody AddActivityReviewRequest request) {
        ActivityReviewDto review = vendorActivityReviewService.addReview(activityId, request);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("review", review);

        return ResponseEntity.ok(response);
    }

    // TODO: Modify response body
    @PutMapping("/review/{reviewId}/rewrite")
    public ResponseEntity<?> modifyReview(@PathVariable Integer reviewId, @RequestBody Map<String, String> data) {
        String content = data.get("content");
        ActivityReviewDto review = vendorActivityReviewService.modifyReviewById(reviewId, content);
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
