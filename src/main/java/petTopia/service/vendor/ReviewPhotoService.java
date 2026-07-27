package petTopia.service.vendor;

import java.util.List;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import petTopia.dto.vendor.VendorReviewPhotoDto;
import petTopia.model.vendor.ReviewPhoto;
import petTopia.repository.vendor.ReviewPhotoRepository;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class ReviewPhotoService {
    private final ReviewPhotoRepository reviewPhotoRepository;

    public List<VendorReviewPhotoDto> findPhotoListByReviewId(Integer reviewId) {
        return reviewPhotoRepository.findByVendorReviewId(reviewId)
                .stream()
                .map(VendorReviewPhotoDto::fromEntity)
                .toList();
    }

    public byte[] findById(Integer reviewImgId) {
        return reviewPhotoRepository.findById(reviewImgId)
                .map(ReviewPhoto::getPhoto)
                .orElseThrow(() -> new EntityNotFoundException("Vendor review photo not found"));
    }
}
