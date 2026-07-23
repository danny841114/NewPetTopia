package petTopia.service.vendor_admin;

import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import petTopia.model.vendor.ReviewPhoto;
import petTopia.model.vendor.VendorReview;
import petTopia.repository.vendor.ReviewPhotoRepository;
import petTopia.repository.vendor.VendorReviewRepository;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class VendorReviewsServiceAdmin {
    private final VendorReviewRepository vendorReviewRepository;
    private final ReviewPhotoRepository reviewPhotoRepository;

    public List<VendorReview> getReviewsByVendorId(Integer vendorId) {
        return vendorReviewRepository.findByVendorId(vendorId);
    }

    public List<ReviewPhoto> getPhotosByReviewId(Integer reviewId) {
        return reviewPhotoRepository.findByVendorReviewId(reviewId);
    }

    public VendorReview addReview(VendorReview review) {
        return vendorReviewRepository.save(review);
    }

    public ReviewPhoto addReviewPhoto(ReviewPhoto photo) {
        return reviewPhotoRepository.save(photo);
    }

    public boolean deleteReview(Integer reviewId) {
        Optional<VendorReview> review = vendorReviewRepository.findById(reviewId);

        if (review.isPresent()) {
            vendorReviewRepository.deleteById(reviewId);
            return true;
        }

        return false;
    }
}
