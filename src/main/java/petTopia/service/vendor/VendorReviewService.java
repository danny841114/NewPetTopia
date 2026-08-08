package petTopia.service.vendor;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import petTopia.dto.vendor.request.AddReviewRequest;
import petTopia.dto.vendor.request.AddReviewStarRequest;
import petTopia.dto.vendor.request.ModifyReviewRequest;
import petTopia.dto.vendor.response.VendorReviewInfo;
import petTopia.model.user.Member;
import petTopia.model.vendor.ReviewPhoto;
import petTopia.model.vendor.Vendor;
import petTopia.model.vendor.VendorReview;
import petTopia.repository.user.MemberRepository;
import petTopia.repository.vendor.ReviewPhotoRepository;
import petTopia.repository.vendor.VendorRepository;
import petTopia.repository.vendor.VendorReviewRepository;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class VendorReviewService {
    private final VendorRepository vendorRepository;
    private final VendorReviewRepository vendorReviewRepository;
    private final ReviewPhotoRepository reviewPhotoRepository;
    private final MemberRepository memberRepository;

    /* 尋找單一店家其所有的評分及留言 */
    public List<VendorReview> findReviewsByVendorId(Integer vendorId) {
        return vendorReviewRepository.findByVendorId(vendorId);
    }

    /* 查詢某個vendorId所有評價之DTO */
    public List<VendorReviewInfo> findReviewListByVendorId(Integer vendorId) {
        return vendorReviewRepository.findByVendorId(vendorId)
                .stream()
                .map(VendorReviewInfo::fromEntity)
                .toList();
    }

    public byte[] getVendorReviewPhoto(Integer vendorId, Integer reviewId, Integer photoId) {
        ReviewPhoto photo = reviewPhotoRepository.findById(photoId)
                .orElseThrow(() -> new EntityNotFoundException("Vendor review photo not found"));

        Integer reviewIdFromPhoto = photo.getVendorReview().getId();
        if (!Objects.equals(reviewIdFromPhoto, reviewId)) {
            throw new IllegalArgumentException("Review ID not match");
        }

        Integer vendorIdFromPhoto = photo.getVendorReview().getVendor().getId();
        if (!Objects.equals(vendorIdFromPhoto, vendorId)) {
            throw new IllegalArgumentException("Vendor ID not match");
        }

        return photo.getPhoto();
    }

    /* 刪除某成員對某店家之評論及評分 */
    @Transactional
    public void deleteReviewByMemberIdAndVendorId(Integer memberId, Integer vendorId) {
        vendorReviewRepository.findFirstByMemberIdAndVendorId(memberId, vendorId)
                .ifPresent(vendorReviewRepository::delete);
    }

    /* 藉由ID尋找評論 */
    public VendorReview findReviewById(Integer reviewId) {
        return vendorReviewRepository.findById(reviewId)
                .orElseThrow(() -> new EntityNotFoundException("Review not found"));
    }

    /* 藉由ID修改評論 */
    @Transactional
    public VendorReview rewriteReviewById(Integer reviewId, String content) {
        VendorReview review = findReviewById(reviewId);

        review.setReviewContent(content);
        review.setReviewTime(new Date());

        return vendorReviewRepository.save(review);
    }

    /* 藉由ID刪除評論 */
    @Transactional
    public void deleteReviewById(Integer reviewId) {
        vendorReviewRepository.deleteById(reviewId);
    }

    /* 新增文字及圖片評論 */
    @Transactional
    public VendorReview addReview(Integer memberId, Integer vendorId, String content, List<MultipartFile> reviewPhotos) throws IOException {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("Member not found"));

        Vendor vendor = vendorRepository.findById(vendorId)
                .orElseThrow(() -> new EntityNotFoundException("Vendor not found"));

        VendorReview review = new VendorReview();

        review.setMember(member);
        review.setVendor(vendor);
        review.setReviewContent(content);
        review.setReviewTime(new Date());

        VendorReview savedReview = vendorReviewRepository.save(review);

        addReviewPhotos(savedReview, reviewPhotos);

        return savedReview;
    }

    /* 新增星星評分 */
    @Transactional
    public VendorReview addStarReview(Integer vendorId, AddReviewStarRequest request) {
        Integer memberId = request.getMemberId();

        VendorReview vendorReview = vendorReviewRepository.findFirstByMemberIdAndVendorId(memberId, vendorId)
                .orElse(null);

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("Member not found"));

        Vendor vendor = vendorRepository.findById(vendorId)
                .orElseThrow(() -> new EntityNotFoundException("Vendor not found"));

        if (vendorReview == null) {
            VendorReview newVendorReview = new VendorReview();

            newVendorReview.setMember(member);
            newVendorReview.setVendor(vendor);
            newVendorReview.setRatingEnvironment(request.getRatingEnv());
            newVendorReview.setRatingPrice(request.getRatingPrice());
            newVendorReview.setRatingService(request.getRatingService());
            newVendorReview.setReviewTime(new Date());

            return vendorReviewRepository.save(newVendorReview);
        } else {
            vendorReview.setRatingEnvironment(request.getRatingEnv());
            vendorReview.setRatingPrice(request.getRatingPrice());
            vendorReview.setRatingService(request.getRatingService());
            vendorReview.setReviewTime(new Date());

            return vendorReviewRepository.save(vendorReview);
        }
    }

    /* 找出單一店家有評分的留言 */
    public List<VendorReview> findReviewsWithRating(Integer vendorId) {
        return vendorReviewRepository.findByVendorId(vendorId)
                .stream()
                .filter(review -> review.getRatingEnvironment() != null
                        && review.getRatingPrice() != null
                        && review.getRatingService() != null)
                .collect(Collectors.toList());
    }

    /* 計算單一店家評分之平均數 */
    @Transactional
    public Vendor setAverageRating(Integer vendorId) {
        Vendor vendor = vendorRepository.findById(vendorId)
                .orElseThrow(() -> new EntityNotFoundException("Vendor not found"));

        List<VendorReview> filteredReviews = vendorReviewRepository.findByVendorId(vendorId)
                .stream()
                .filter(review -> review.getRatingEnvironment() != null
                        && review.getRatingPrice() != null
                        && review.getRatingService() != null)
                .collect(Collectors.toList());

        int sumRatingEnvironment = 0;
        int sumRatingPrice = 0;
        int sumRatingService = 0;
        int sumRatingAll = 0;

        for (VendorReview review : filteredReviews) {
            Integer ratingEnvironment = review.getRatingEnvironment();
            sumRatingEnvironment += ratingEnvironment;

            Integer ratingPrice = review.getRatingPrice();
            sumRatingPrice += ratingPrice;

            Integer ratingService = review.getRatingService();
            sumRatingService += ratingService;

            int ratingAll = ratingEnvironment + ratingPrice + ratingService;
            sumRatingAll += ratingAll;
        }

        int count = filteredReviews.size();
        float avgRatingEnvironment = Math.round((sumRatingEnvironment / (float) count) * 10) / 10.0f;
        float avgRatingPrice = Math.round((sumRatingPrice / (float) count) * 10) / 10.0f;
        float avgRatingService = Math.round((sumRatingService / (float) count) * 10) / 10.0f;
        float avgRatingAll = Math.round((sumRatingAll / 3.0f / (float) count) * 10) / 10.0f;

        vendor.setAvgRatingEnvironment(avgRatingEnvironment);
        vendor.setAvgRatingPrice(avgRatingPrice);
        vendor.setAvgRatingService(avgRatingService);
        vendor.setTotalRating(avgRatingAll);

        return vendorRepository.save(vendor);
    }

    /* 尋找某會員是否有對某店家留下評論 */
    public boolean getReviewIsExisted(Integer memberId, Integer vendorId) {
        List<VendorReview> reviews = vendorReviewRepository.findByMemberIdAndVendorId(memberId, vendorId);
        return !reviews.isEmpty();
    }

    /* 查詢某個member所有評價之DTO */
    public List<VendorReviewInfo> findReviewListByMemberId(Integer memberId) {
        return vendorReviewRepository.findByMemberId(memberId)
                .stream()
                .map(VendorReviewInfo::fromEntity)
                .toList();
    }

    /* 新增評論(完整版) */
    @Transactional
    public VendorReview addNewReview(Integer vendorId, AddReviewRequest request) throws IOException {
        Member member = memberRepository.findById(request.getMemberId())
                .orElseThrow(() -> new EntityNotFoundException("Member not found"));

        Vendor vendor = vendorRepository.findById(vendorId)
                .orElseThrow(() -> new EntityNotFoundException("Vendor not found"));

        VendorReview review = new VendorReview();

        review.setMember(member);
        review.setVendor(vendor);
        review.setReviewContent(request.getContent());
        review.setRatingEnvironment(request.getRatingEnv());
        review.setRatingPrice(request.getRatingPrice());
        review.setRatingService(request.getRatingService());
        review.setReviewTime(new Date());

        VendorReview savedReview = vendorReviewRepository.save(review);

        addReviewPhotos(savedReview, request.getReviewPhotos());

        return savedReview;
    }

    /* 修改評論(完整版) */
    @Transactional
    public VendorReview modifyReview(Integer reviewId, ModifyReviewRequest request) throws IOException {
        VendorReview review = findReviewById(reviewId);

        review.setReviewContent(request.getContent());
        review.setRatingEnvironment(request.getRatingEnv());
        review.setRatingPrice(request.getRatingPrice());
        review.setRatingService(request.getRatingService());
        review.setReviewTime(new Date());

        VendorReview savedReview = vendorReviewRepository.save(review);

        addReviewPhotos(review, request.getReviewPhotos());

        List<Integer> deletePhotoIds = request.getDeletePhotoIds();
        if (deletePhotoIds != null && !deletePhotoIds.isEmpty()) {
            for (Integer photoId : deletePhotoIds) {
                reviewPhotoRepository.deleteById(photoId);
            }
        }

        return savedReview;
    }

    /* 上傳多張圖片 */
    private void addReviewPhotos(VendorReview review, List<MultipartFile> reviewPhotos) throws IOException {
        if (reviewPhotos == null || reviewPhotos.isEmpty()) return;

        for (MultipartFile photo : reviewPhotos) {
            ReviewPhoto reviewPhoto = new ReviewPhoto();

            reviewPhoto.setVendorReview(review);
            reviewPhoto.setPhoto(photo.getBytes());

            reviewPhotoRepository.save(reviewPhoto);
        }
    }
}
