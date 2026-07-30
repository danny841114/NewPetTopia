package petTopia.controller.shop;

import java.io.IOException;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import petTopia.dto.shop.ProductReviewResponseDto;
import petTopia.dto.shop.request.CreateReviewRequest;
import petTopia.dto.shop.request.UpdateReviewRequest;
import petTopia.model.shop.ProductReview;
import petTopia.service.shop.ProductReviewService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/shop")
public class ShopProductReviewController {
    private final ProductReviewService productReviewService;

    // TODO: Check response body
    // 新增會員評論
    @PostMapping("/product/{productId}/review/create")
    public ResponseEntity<ProductReview> createReview(@PathVariable Integer productId,
                                                      @ModelAttribute CreateReviewRequest request) throws IOException {
        ProductReview review = productReviewService.createReview(productId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(review);
    }

    // 根據 memberId 找該會員的所有評論
    @GetMapping("/reviews/member/{memberId}")
    public ResponseEntity<Page<ProductReviewResponseDto>> getReviewsByMemberId(@PathVariable Integer memberId,
                                                                               @RequestParam(defaultValue = "1", required = false) Integer page,
                                                                               @RequestParam(defaultValue = "10", required = false) Integer size) {
        Page<ProductReviewResponseDto> reviews = productReviewService.getReviewsByMemberId(memberId, page, size);
        return ResponseEntity.ok(reviews);
    }

    //修改會員評論
    @PutMapping("/reviews/{reviewId}/update")
    public ResponseEntity<Void> updateReview(@PathVariable Integer reviewId,
                                             @ModelAttribute UpdateReviewRequest request) throws IOException {
        return productReviewService.updateReview(reviewId, request)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.badRequest().build();
    }

    // 根據 productDetailId 查找商品的平均評分
    @GetMapping("/products/{productDetailId}/reviews/avgRating")
    public ResponseEntity<Double> getAverageRating(@PathVariable Integer productDetailId) {
        Double averageRating = productReviewService.getAverageRatingByProductDetailId(productDetailId);

        return averageRating != null
                ? ResponseEntity.ok(averageRating)
                : ResponseEntity.notFound().build();
    }

    // 查詢某個商品的評論總數
    @GetMapping("/products/{productDetailId}/reviews/count")
    public ResponseEntity<Integer> getReviewsCount(@PathVariable Integer productDetailId) {
        Integer count = productReviewService.getReviewsCountByProductDetailId(productDetailId);

        return count != null
                ? ResponseEntity.ok(count)
                : ResponseEntity.notFound().build();
    }

    //該商品的所有評論
    @GetMapping("/reviews/product/{productDetailId}")
    public ResponseEntity<Page<ProductReviewResponseDto>> getReviewsByProductDetailId(@PathVariable Integer productDetailId,
                                                                                      @RequestParam(defaultValue = "1", required = false) int page,  // 默認第1頁
                                                                                      @RequestParam(defaultValue = "10", required = false) int size) {  // 默認每頁10條評論
        Page<ProductReviewResponseDto> reviews = productReviewService.getReviewsByProductDetailId(productDetailId, page, size);

        return reviews.isEmpty()
                ? ResponseEntity.notFound().build()
                : ResponseEntity.ok(reviews);
    }

    // 檢查會員是否已經對該商品評論過
    @GetMapping("/review/hasReviewed")
    public ResponseEntity<Map<String, Boolean>> checkIfReviewed(@RequestParam Integer productId,
                                                                @RequestParam Integer memberId) {
        Boolean hasReviewed = productReviewService.hasReviewed(productId, memberId);
        return ResponseEntity.ok(Map.of("hasReviewed", hasReviewed));
    }
}
