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
    public ResponseEntity<Page<ProductReviewResponseDto>> getAllReviews(@RequestParam(defaultValue = "1") Integer page,
                                                                        @RequestParam(defaultValue = "10") Integer size,
                                                                        @RequestParam(defaultValue = "time") String sortBy) {
        Page<ProductReviewResponseDto> reviews = productReviewService.getAllReviews(page, size, sortBy);
        return ResponseEntity.ok(reviews);
    }


    // 模糊搜尋（可搜尋商品 ID、會員 ID、評論 ID 或評論描述）
    @GetMapping("/reviews/search")
    public ResponseEntity<Page<ProductReviewResponseDto>> searchReviews(@RequestParam String keyword,
                                                                        @RequestParam(defaultValue = "1") Integer page,
                                                                        @RequestParam(defaultValue = "10") Integer size) {
        Page<ProductReviewResponseDto> reviews = productReviewService.searchReviews(keyword, page, size);
        return ResponseEntity.ok(reviews);
    }

    //刪除評論
    @DeleteMapping("/review/{reviewId}/delete")
    public ResponseEntity<Void> deleteReview(@PathVariable Integer reviewId) {
        productReviewService.deleteReviewById(reviewId);
        return ResponseEntity.noContent().build();
    }

    // 獲取評分最高的前 5 名商品
    @GetMapping("/review/ratingTop5Product")
    public ResponseEntity<List<ProductRatingProjection>> getTop5ProductsByAverageRating() {
        List<ProductRatingProjection> products = productReviewService.getTop5ProductsByAverageRating();
        return ResponseEntity.ok(products);
    }

    // 獲取評分最高的前 3 名商品種類
    @GetMapping("/review/ratingTop3ProductDetail")
    public ResponseEntity<List<ProductDetailRatingProjection>> getTop3ProductDetailsByAverageRating() {
        List<ProductDetailRatingProjection> productDetails = productReviewService.getTop3ProductDetailsByAverageRating();
        return ResponseEntity.ok(productDetails);
    }
}
