package petTopia.dto.vendor.request;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
public class ModifyReviewRequest {
    private String content;
    private Integer ratingEnv;
    private Integer ratingPrice;
    private Integer ratingService;

    private List<MultipartFile> reviewPhotos;
    private List<Integer> deletePhotoIds;
}
