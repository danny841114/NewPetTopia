package petTopia.service.vendor;

import java.util.Base64;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import petTopia.model.vendor.ReviewPhoto;
import petTopia.repository.vendor.ReviewPhotoRepository;
import petTopia.util.ImageConverter;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class ReviewPhotoService {
    private final ReviewPhotoRepository reviewPhotoRepository;

    /* 設定Base64 */
    public List<ReviewPhoto> findPhotoListByReviewId(Integer reviewId) {
        List<ReviewPhoto> list = reviewPhotoRepository.findByVendorReviewId(reviewId);

        // TODO: change base64 to byte[]
        for (ReviewPhoto photo : list) {
            byte[] photoByte = photo.getPhoto();
            if (photoByte != null) {
                String mimeType = ImageConverter.getMimeType(photoByte);
                String base64 = "data:%s;base64,".formatted(mimeType) + Base64.getEncoder().encodeToString(photoByte);
                photo.setPhotoBase64(base64);
            }
        }
        return list;
    }
}
