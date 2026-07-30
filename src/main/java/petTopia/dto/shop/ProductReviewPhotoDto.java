package petTopia.dto.shop;

import lombok.*;
import petTopia.model.shop.ProductReviewPhoto;
import petTopia.util.ImageConverter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductReviewPhotoDto {
    private Integer reviewPhotoId;
    private String reviewPhotos; // base64

    public static ProductReviewPhotoDto convertToDto(@NonNull ProductReviewPhoto photo) {
        String imageBase64 = ImageConverter.byteToBase64(photo.getReviewPhoto());

        return ProductReviewPhotoDto.builder()
                .reviewPhotoId(photo.getId())
                .reviewPhotos(imageBase64)
                .build();
    }
}
