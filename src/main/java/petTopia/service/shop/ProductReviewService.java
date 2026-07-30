package petTopia.service.shop;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import petTopia.dto.shop.ProductReviewResponseDto;
import petTopia.dto.shop.request.CreateReviewRequest;
import petTopia.dto.shop.request.UpdateReviewRequest;
import petTopia.exception.custom.AlreadyReviewedException;
import petTopia.model.shop.ProductReview;
import petTopia.model.shop.ProductReviewPhoto;
import petTopia.projection.shop.ProductDetailRatingProjection;
import petTopia.projection.shop.ProductRatingProjection;
import petTopia.repository.shop.ProductRepository;
import petTopia.repository.shop.ProductReviewPhotoRepository;
import petTopia.repository.shop.ProductReviewRepository;
import petTopia.repository.user.MemberRepository;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class ProductReviewService {
    private final ProductReviewRepository productReviewRepository;
    private final ProductRepository productRepository;
    private final MemberRepository memberRepository;
    private final ProductReviewPhotoRepository productReviewPhotoRepository;

    public Boolean hasReviewed(Integer productId, Integer memberId) {
        Optional<ProductReview> existingReview = productReviewRepository.findByProductIdAndMemberId(productId, memberId);
        return existingReview.isPresent();
    }

    //===================會員評論==================

    // 新增評論
    @Transactional
    public ProductReview createReview(Integer productId, CreateReviewRequest request) throws IOException {
        Optional<ProductReview> existingReview = productReviewRepository.findByProductIdAndMemberId(
                productId,
                request.getMemberId()
        );

        if (existingReview.isPresent()) throw new AlreadyReviewedException("您已經評論過該商品");

        ProductReview productReview = new ProductReview();

        productReview.setProduct(productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found")));

        productReview.setMember(memberRepository.findById(request.getMemberId())
                .orElseThrow(() -> new EntityNotFoundException("Member not found")));

        productReview.setRating(request.getRating());
        productReview.setReviewDescription(request.getReviewDescription());
        productReview.setReviewTime(new Date());

        ProductReview savedReview = productReviewRepository.save(productReview);

        List<MultipartFile> reviewPhotos = request.getReviewPhotos();
        if (reviewPhotos != null && !reviewPhotos.isEmpty()) {
            for (int i = 0; i < reviewPhotos.size() && i < 5; i++) {
                ProductReviewPhoto reviewPhoto = new ProductReviewPhoto();

                reviewPhoto.setProductReview(savedReview);
                reviewPhoto.setReviewPhoto(reviewPhotos.get(i).getBytes());

                productReviewPhotoRepository.save(reviewPhoto);
            }
        }

        return savedReview;
    }

    // 根據 memberId 查找所有評論，並確保載入評論的圖片
    public Page<ProductReviewResponseDto> getReviewsByMemberId(Integer memberId, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page - 1, size);  // 頁數從0開始

        Page<ProductReview> reviewsPage = productReviewRepository.findByMemberIdOrderByReviewTimeDesc(memberId, pageable);

        reviewsPage.getContent().forEach(review -> review.getReviewPhotos().size()); // 強制 Hibernate 載入評論的圖片

        return reviewsPage.map(ProductReviewResponseDto::convertToDto);
    }


    // 修改單一評論
    @Transactional
    public Boolean updateReview(Integer reviewId, UpdateReviewRequest request) throws IOException {
        Optional<ProductReview> optionalReview = productReviewRepository.findById(reviewId);

        if (optionalReview.isPresent()) {
            ProductReview review = optionalReview.get();

            if (request.getRating() != null) {
                review.setRating(request.getRating());
            }

            if (request.getReviewDescription() != null) {
                review.setReviewDescription(request.getReviewDescription());
            }

            if (request.getDeletePhotoIds() != null && !request.getDeletePhotoIds().isEmpty()) {
                productReviewPhotoRepository.deleteByIdIn(request.getDeletePhotoIds());
            }

            List<MultipartFile> newPhotos = request.getNewPhotos();
            if (newPhotos != null && !newPhotos.isEmpty()) {
                for (int i = 0; i < newPhotos.size() && i < 5; i++) {
                    ProductReviewPhoto reviewPhoto = new ProductReviewPhoto();

                    reviewPhoto.setProductReview(review);
                    reviewPhoto.setReviewPhoto(newPhotos.get(i).getBytes());

                    productReviewPhotoRepository.save(reviewPhoto);
                }
            }

            productReviewRepository.save(review);

            return true;
        }

        return false;
    }

    //===================商品評論===================

    // 找某商品的平均評分
    public Double getAverageRatingByProductDetailId(Integer productDetailId) {
        return productReviewRepository.findAverageRatingByProductDetailId(productDetailId);
    }

    // 取得所有評論
    public Page<ProductReviewResponseDto> getAllReviews(Integer page, Integer size, String sort) {
        Pageable pageable = PageRequest.of(page - 1, size);

        String lowerCaseSort = sort.toLowerCase();

        Page<ProductReview> reviewsPage = switch (lowerCaseSort) {
            case "id" -> productReviewRepository.findAllReviewsByIdDesc(pageable);
            case "rating" -> productReviewRepository.findAllReviewsByRatingDesc(pageable);
            default -> productReviewRepository.findAllReviewsOrderByReviewTimeDesc(pageable);
        };

        reviewsPage.getContent().forEach(review -> review.getReviewPhotos().size()); // 強制 Hibernate 載入評論的圖片

        return reviewsPage.map(ProductReviewResponseDto::convertToDto);
    }

    // 找某商品的所有評論
    public Page<ProductReviewResponseDto> getReviewsByProductDetailId(Integer productDetailId, int page, int size) {
        // 設定分頁請求，根據 reviewTime 降冪排序
        Pageable pageable = PageRequest.of(page - 1, size);  // 頁數從0開始

        // 查詢該商品的所有評論，並使用分頁
        Page<ProductReview> reviewsPage = productReviewRepository.findAllReviewsByProductDetailIdOrderByReviewTimeDesc(productDetailId, pageable);

        // 確保每條評論的圖片都被載入
        reviewsPage.getContent().forEach(review -> review.getReviewPhotos().size()); // 強制 Hibernate 載入評論的圖片

        // 將 ProductReview 轉換為 ProductReviewResponseDto
        return reviewsPage.map(ProductReviewResponseDto::convertToDto);
    }

    //找某商品的總評論數
    public Integer getReviewsCountByProductDetailId(Integer productDetailId) {
        return productReviewRepository.countReviewsByProductDetailId(productDetailId);
    }

    //模糊搜尋
    public Page<ProductReviewResponseDto> searchReviews(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);

        // 執行模糊搜尋
        Page<ProductReview> reviewsPage = productReviewRepository.searchReviews(keyword, pageable);

        // 確保評論的圖片載入
        reviewsPage.getContent().forEach(review -> review.getReviewPhotos().size());

        // 轉換為 DTO
        return reviewsPage.map(ProductReviewResponseDto::convertToDto);
    }

    // 根據評論ID找單個評論
    public ProductReview getReviewById(Integer reviewId) {
        return productReviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));
    }

    // 刪除評論
    public void deleteReviewById(Integer reviewId) {
        productReviewRepository.findById(reviewId).ifPresentOrElse(
                productReviewRepository::delete,
                () -> {
                    throw new EntityNotFoundException("Review not found");
                }
        );
    }

    //=================評分統計=======================
    //評分最高商品
    public List<ProductRatingProjection> getTop5ProductsByAverageRating() {
        Pageable top5Page = PageRequest.of(0, 3);
        return productReviewRepository.findTop5ProductsByAverageRating(top5Page);
    }

    //評分最高商品種類
    public List<ProductDetailRatingProjection> getTop3ProductDetailsByAverageRating() {
        Pageable top3Page = PageRequest.of(0, 3);
        return productReviewRepository.findTop3ProductDetailsByAverageRating(top3Page);
    }
}
