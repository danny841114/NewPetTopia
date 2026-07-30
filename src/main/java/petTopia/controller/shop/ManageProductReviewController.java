package petTopia.controller.shop;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import petTopia.dto.shop.ProductReviewResponseDto;
import petTopia.projection.shop.ProductDetailRatingProjection;
import petTopia.projection.shop.ProductRatingProjection;
import petTopia.service.shop.ProductReviewService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/manage/shop")
public class ManageProductReviewController {
    private final ProductReviewService productReviewService;

    //所有評論
    @GetMapping("/reviews")
    public ResponseEntity<?> getAllReviews(@RequestParam(defaultValue = "1") Integer page,
                                           @RequestParam(defaultValue = "10") Integer size,
                                           @RequestParam(defaultValue = "time") String sortBy) {
        try {
            Page<ProductReviewResponseDto> reviews = productReviewService.getAllReviews(page, size, sortBy);
            return ResponseEntity.ok(reviews);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }


    // 模糊搜尋（可搜尋商品 ID、會員 ID、評論 ID 或評論描述）
    @GetMapping("/reviews/search")
    public ResponseEntity<?> searchReviews(@RequestParam String keyword,
                                           @RequestParam(defaultValue = "1") Integer page,
                                           @RequestParam(defaultValue = "10") Integer size) {
        try {
            Page<ProductReviewResponseDto> reviews = productReviewService.searchReviews(keyword, page, size);
            return ResponseEntity.ok(reviews);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    //刪除評論
    @DeleteMapping("/review/{reviewId}/delete")
    public ResponseEntity<?> deleteReview(@PathVariable Integer reviewId) {
        try {
            productReviewService.deleteReviewById(reviewId);
            return ResponseEntity.ok("Review deleted successfully.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    // 獲取評分最高的前 5 名商品
    @GetMapping("/review/ratingTop5Product")
    public ResponseEntity<?> getTop5ProductsByAverageRating() {
        try {
            List<ProductRatingProjection> products = productReviewService.getTop5ProductsByAverageRating();
            return ResponseEntity.ok(products); // 200 OK
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    // 獲取評分最高的前 3 名商品種類
    @GetMapping("/review/ratingTop3ProductDetail")
    public ResponseEntity<?> getTop3ProductDetailsByAverageRating() {
        try {
            List<ProductDetailRatingProjection> productDetails = productReviewService.getTop3ProductDetailsByAverageRating();
            return ResponseEntity.ok(productDetails); // 200 OK
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }
}
