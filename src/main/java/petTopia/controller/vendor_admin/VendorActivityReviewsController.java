package petTopia.controller.vendor_admin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import petTopia.model.vendor.VendorActivityReview;
import petTopia.repository.vendor.VendorActivityReviewRepository;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/vendor_admin")
public class VendorActivityReviewsController {
    private final VendorActivityReviewRepository vendorActivityReviewRepository;

    @GetMapping("/activityreviews")
    public ResponseEntity<?> getReviewsByVendorActivityId(@RequestParam Integer vendorActivityId) {
        List<VendorActivityReview> vendorActivityReviews = vendorActivityReviewRepository.findByVendorActivityId(vendorActivityId);
        return ResponseEntity.ok(vendorActivityReviews);
    }

    @DeleteMapping("/activityreviews/delete/{reviewId}")
    public ResponseEntity<?> deleteReview(@PathVariable Integer reviewId) {
        Optional<VendorActivityReview> optional = vendorActivityReviewRepository.findById(reviewId);

        Map<String, String> response = new HashMap<>();

        if (optional.isPresent()) {
            vendorActivityReviewRepository.deleteById(reviewId);
            response.put("message", "刪除成功");
        } else {
            response.put("message", "刪除失敗無此資料");
        }

        return ResponseEntity.ok(response);
    }
}
