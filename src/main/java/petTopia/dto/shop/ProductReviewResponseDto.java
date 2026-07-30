package petTopia.dto.shop;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import lombok.*;
import petTopia.model.shop.ProductReview;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductReviewResponseDto {
    private Integer reviewId;
    private Integer memberId;
    private String memberName;
    private Integer productId;
    private Integer productDetailId;
    private String productName;
    private String productColor;
    private String productSize;
    private byte[] productPhoto;
    private Integer rating;
    private String reviewDescription;
    private List<ProductReviewPhotoDto> productReviewPhoto; // 存 Base64 格式的圖片
    private Date reviewTime;

    public static ProductReviewResponseDto convertToDto(@NonNull ProductReview review) {
        Integer memberId = review.getMember() != null ? review.getMember().getId() : null;
        String memberName = review.getMember() != null ? review.getMember().getName() : null;
        Integer productId = review.getProduct() != null ? review.getProduct().getId() : null;
        byte[] productPhoto = review.getProduct() != null ? review.getProduct().getPhoto() : null;

        Integer productDetailId = (review.getProduct() != null && review.getProduct().getProductDetail() != null)
                ? review.getProduct().getProductDetail().getId()
                : null;

        String productDetailName = (review.getProduct() != null && review.getProduct().getProductDetail() != null)
                ? review.getProduct().getProductDetail().getName()
                : null;

        String productColorName = (review.getProduct() != null && review.getProduct().getProductColor() != null)
                ? review.getProduct().getProductColor().getName()
                : "無";

        String productSizeName = (review.getProduct() != null && review.getProduct().getProductSize() != null)
                ? review.getProduct().getProductSize().getName()
                : "無";

        // 處理圖片為 Base64 格式
        List<ProductReviewPhotoDto> productReviewPhotoList = review.getReviewPhotos()
                .stream()
                .map(ProductReviewPhotoDto::convertToDto)
                .collect(Collectors.toList());

        return ProductReviewResponseDto.builder()
                .reviewId(review.getId())
                .memberId(memberId)
                .memberName(memberName)
                .productId(productId)
                .productDetailId(productDetailId)
                .productName(productDetailName)
                .productColor(productColorName)
                .productSize(productSizeName)
                .productPhoto(productPhoto)
                .rating(review.getRating())
                .reviewDescription(review.getReviewDescription())
                .reviewTime(review.getReviewTime())
                .productReviewPhoto(productReviewPhotoList)
                .build();
    }
}
