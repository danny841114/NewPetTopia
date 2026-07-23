package petTopia.controller.vendor_admin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import petTopia.model.vendor.ReviewPhoto;
import petTopia.model.vendor.VendorReview;
import petTopia.repository.vendor.ReviewPhotoRepository;
import petTopia.repository.vendor.VendorReviewRepository;
import petTopia.service.vendor_admin.VendorReviewsServiceAdmin;

@Slf4j
@RequiredArgsConstructor
@RestController
public class VendorReviewsController {
    private final VendorReviewsServiceAdmin vendorReviewsService;
    private final VendorReviewRepository vendorReviewRepository;
    private final ReviewPhotoRepository reviewPhotoRepository;

    @GetMapping("/api/vendor_admin/reviews/{vendorId}")
    public ResponseEntity<?> getAllReviews() {
        List<VendorReview> reviews = vendorReviewRepository.findAll();
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/api/vendor_admin/review")
    public ResponseEntity<?> getReviewsByVendorId(@RequestParam Integer vendorId) {
        List<VendorReview> vendorReviews = vendorReviewsService.getReviewsByVendorId(vendorId);
        return ResponseEntity.ok(vendorReviews);
    }

    @GetMapping("/api/vendor_admin/review/photos/{reviewId}")
    public List<ReviewPhoto> getPhotosByReviewId(@PathVariable Integer reviewId) {
        return vendorReviewsService.getPhotosByReviewId(reviewId);
    }

    @GetMapping("/review_photos/ids")
    public ResponseEntity<?> findPhotoIdByVendorReviewId(@RequestParam Integer vendorReviewId) {
        Optional<VendorReview> op = vendorReviewRepository.findById(vendorReviewId);

        if (op.isPresent()) {
            VendorReview vendorReviews = op.get();
            List<Integer> photoIdList = new ArrayList<>();
            vendorReviews.getReviewPhotos()
                    .forEach(photo -> photoIdList.add(photo.getId()));

            return ResponseEntity.ok(photoIdList);
        }

        return ResponseEntity.notFound().build();
    }

    @GetMapping("/review_photos/download")
    public ResponseEntity<?> downloadPhotoById(@RequestParam Integer photoId) {
        Optional<ReviewPhoto> photoOpt = reviewPhotoRepository.findById(photoId);

        if (photoOpt.isPresent()) {
            byte[] photoFile = photoOpt.get().getPhoto();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG);

            return ResponseEntity.ok().headers(headers).body(photoFile);
        }

        return ResponseEntity.notFound().build();
    }

    @PostMapping("/api/vendor_admin/review/add")
    public ResponseEntity<?> addReview(@RequestBody VendorReview review,
                                       @RequestPart(value = "photo", required = false) MultipartFile photo) {
        try {
            VendorReview vendorReviews = new VendorReview();

            vendorReviews.setVendorId(review.getVendorId());
            vendorReviews.setMemberId(review.getMemberId());
            vendorReviews.setReviewContent(review.getReviewContent());
            vendorReviews.setReviewTime(review.getReviewTime());
            vendorReviews.setRatingEnvironment(review.getRatingEnvironment());
            vendorReviews.setRatingPrice(review.getRatingPrice());
            vendorReviews.setRatingService(review.getRatingService());

            VendorReview savedReview = vendorReviewRepository.save(vendorReviews);

            if (photo != null && !photo.isEmpty()) {
                ReviewPhoto reviewPhoto = new ReviewPhoto();

                reviewPhoto.setVendorReview(savedReview);
                reviewPhoto.setPhoto(photo.getBytes()); // 转换为 byte[]

                reviewPhotoRepository.save(reviewPhoto); // 保存图片
            }

            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (Exception e) {
            log.error("Add review failed", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to add review");
        }
    }

    @DeleteMapping("/api/vendor_admin/review/delete/{reviewId}")
    public ResponseEntity<?> deleteReview(@PathVariable Integer reviewId) {
        Optional<VendorReview> review = vendorReviewRepository.findById(reviewId);

        Map<String, String> response = new HashMap<>();

        if (review.isPresent()) {
            vendorReviewsService.deleteReview(reviewId);
            response.put("message", "刪除成功");
        } else {
            response.put("message", "刪除失敗無此資料");
        }

        return ResponseEntity.ok(response);
    }
}
