package petTopia.dto.shop.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UploadPhotoRequest {
    private String userId;
    private List<String> image;
}
