package petTopia.service.vendor_admin;

import java.io.IOException;
import java.util.*;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import petTopia.dto.vendor_admin.request.AddReviewRequest;
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

    public List<VendorReview> getAllReviews() {
        return vendorReviewRepository.findAll();
    }

    public List<VendorReview> getReviewsByVendorId(Integer vendorId) {
        return vendorReviewRepository.findByVendorId(vendorId);
    }

    public List<ReviewPhoto> getPhotosByReviewId(Integer reviewId) {
        return reviewPhotoRepository.findByVendorReviewId(reviewId);
    }

    @Transactional
    public void deleteReview(Integer reviewId) {
        vendorReviewRepository.findById(reviewId)
                .ifPresent(vendorReviewRepository::delete);
    }

    public List<Integer> findPhotoIdsByReviewId(Integer reviewId) {
        List<Integer> photoIds = new ArrayList<>();

        vendorReviewRepository.findById(reviewId)
                .map(VendorReview::getReviewPhotos)
                .stream()
                .flatMap(List::stream)
                .forEach(photo -> photoIds.add(photo.getId()));

        return photoIds;
    }

    public byte[] downloadPhotoById(@RequestParam Integer photoId) {
        return reviewPhotoRepository.findById(photoId)
                .map(ReviewPhoto::getPhoto)
                .orElseThrow(() -> new EntityNotFoundException("Photo byte array not found"));
    }

    @Transactional
    public void addReview(AddReviewRequest request, MultipartFile photo) throws IOException {
        VendorReview review = new VendorReview();

        review.setVendorId(request.getVendorId());
        review.setMemberId(request.getMemberId());
        review.setReviewContent(request.getReviewContent());
        review.setReviewTime(request.getReviewTime());
        review.setRatingEnvironment(request.getRatingEnvironment());
        review.setRatingPrice(request.getRatingPrice());
        review.setRatingService(request.getRatingService());

        VendorReview savedReview = vendorReviewRepository.save(review);

        if (photo != null && !photo.isEmpty()) {
            ReviewPhoto reviewPhoto = new ReviewPhoto();

            reviewPhoto.setVendorReview(savedReview);
            reviewPhoto.setPhoto(photo.getBytes());

            reviewPhotoRepository.save(reviewPhoto);
        }
    }
}
