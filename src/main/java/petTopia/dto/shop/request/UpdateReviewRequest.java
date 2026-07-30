package petTopia.dto.shop.request;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
public class UpdateReviewRequest {
    private Integer rating;
    private String reviewDescription;
    private List<MultipartFile> newPhotos;
    private List<Integer> deletePhotoIds;
}
