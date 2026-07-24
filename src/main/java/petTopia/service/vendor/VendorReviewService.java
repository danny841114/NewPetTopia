package petTopia.service.vendor;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import petTopia.dto.vendor.VendorReviewDto;
import petTopia.model.user.Member;
import petTopia.model.vendor.ReviewPhoto;
import petTopia.model.vendor.Vendor;
import petTopia.model.vendor.VendorActivityReview;
import petTopia.model.vendor.VendorReview;
import petTopia.repository.user.MemberRepository;
import petTopia.repository.vendor.ReviewPhotoRepository;
import petTopia.repository.vendor.VendorActivityReviewRepository;
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
    private final VendorActivityReviewRepository vendorActivityReviewRepository;

    private final ReviewPhotoService reviewPhotoService;

    /* 尋找單一店家其所有的評分及留言 */
    public List<VendorReview> findReviewsByVendorId(Integer vendorId) {
        return vendorReviewRepository.findByVendorId(vendorId);
    }

    /* 新增或修改文字評論 */
    @Transactional
    public void addOrModifyVendorTextReview(Integer memberId, Integer vendorId, String content) {
        VendorReview vendorReview = vendorReviewRepository.findFirstByMemberIdAndVendorId(memberId, vendorId)
                .orElse(null);

        if (vendorReview == null) {
            VendorReview newVendorReview = new VendorReview();

            newVendorReview.setMemberId(memberId);
            newVendorReview.setVendorId(vendorId);
            newVendorReview.setReviewContent(content);
            newVendorReview.setReviewTime(new Date());

            vendorReviewRepository.save(newVendorReview);
        } else {
            vendorReview.setReviewContent(content);
            vendorReview.setReviewTime(new Date());

            vendorReviewRepository.save(vendorReview);
        }
    }

    /* 查詢某個vendorId所有評價之DTO */
    public List<VendorReviewDto> findReviewListByVendorId(Integer vendorId) {
        return vendorReviewRepository.findByVendorId(vendorId)
                .stream()
                .map(this::fromEntity)
                .collect(Collectors.toList());
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
        VendorReview review = new VendorReview();

        review.setMemberId(memberId);
        review.setVendorId(vendorId);
        review.setReviewContent(content);
        review.setReviewTime(new Date());

        VendorReview savedReview = vendorReviewRepository.save(review);

        addReviewPhotos(savedReview, reviewPhotos);

        return savedReview;
    }

    // TODO: Change to request body
    /* 新增星星評分 */
    @Transactional
    public VendorReview addStarReview(Integer memberId, Integer vendorId, Integer ratingEnv, Integer ratingPrice, Integer ratingService) {
        VendorReview vendorReview = vendorReviewRepository.findFirstByMemberIdAndVendorId(memberId, vendorId)
                .orElse(null);

        if (vendorReview == null) {
            VendorReview newVendorReview = new VendorReview();

            newVendorReview.setMemberId(memberId);
            newVendorReview.setVendorId(vendorId);
            newVendorReview.setRatingEnvironment(ratingEnv);
            newVendorReview.setRatingPrice(ratingPrice);
            newVendorReview.setRatingService(ratingService);
            newVendorReview.setReviewTime(new Date());

            return vendorReviewRepository.save(newVendorReview);
        } else {
            vendorReview.setRatingEnvironment(ratingEnv);
            vendorReview.setRatingPrice(ratingPrice);
            vendorReview.setRatingService(ratingService);
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
    public List<VendorReviewDto> findReviewListByMemberId(Integer memberId) {
        return vendorReviewRepository.findByMemberId(memberId)
                .stream()
                .map(this::fromEntity)
                .collect(Collectors.toList());
    }

    /* 新增評論(完整版) */
    @Transactional
    public VendorReview addNewReview(Integer memberId,
                                     Integer vendorId,
                                     String content,
                                     Integer ratingEnv,
                                     Integer ratingPrice,
                                     Integer ratingService,
                                     List<MultipartFile> reviewPhotos) throws IOException {
        VendorReview review = new VendorReview();

        review.setMemberId(memberId);
        review.setVendorId(vendorId);
        review.setReviewContent(content);
        review.setRatingEnvironment(ratingEnv);
        review.setRatingPrice(ratingPrice);
        review.setRatingService(ratingService);
        review.setReviewTime(new Date());

        VendorReview savedReview = vendorReviewRepository.save(review);

        addReviewPhotos(savedReview, reviewPhotos);

        return savedReview;
    }

    /* 修改評論(完整版) */
    @Transactional
    public VendorReview modifyReview(Integer reviewId,
                                     String content,
                                     Integer ratingEnv,
                                     Integer ratingPrice,
                                     Integer ratingService,
                                     List<MultipartFile> reviewPhotos,
                                     List<Integer> deletePhotoIds) throws IOException {
        VendorReview review = findReviewById(reviewId);

        review.setReviewContent(content);
        review.setRatingEnvironment(ratingEnv);
        review.setRatingPrice(ratingPrice);
        review.setRatingPrice(ratingPrice);
        review.setRatingService(ratingService);
        review.setReviewTime(new Date());

        VendorReview savedReview = vendorReviewRepository.save(review);

        addReviewPhotos(review, reviewPhotos);

        if (deletePhotoIds != null && !deletePhotoIds.isEmpty()) {
            for (Integer photoId : deletePhotoIds) {
                reviewPhotoRepository.deleteById(photoId);
            }
        }

        return savedReview;
    }

    /* 將Member和VendorReview轉換成DTO */
    public VendorReviewDto fromEntity(VendorReview review) {
        Member member = memberRepository.findById(review.getMemberId())
                .orElseThrow(() -> new EntityNotFoundException("Member not found"));

        Vendor vendor = vendorRepository.findById(review.getVendorId())
                .orElseThrow(() -> new EntityNotFoundException("Vendor not found"));

        List<ReviewPhoto> reviewPhotoList = reviewPhotoService.findPhotoListByReviewId(review.getId());

        VendorReviewDto dto = new VendorReviewDto();

        // 設定評價資訊
        dto.setReviewId(review.getId());
        dto.setVendorId(review.getVendorId());
        dto.setVendorName(vendor.getName());
        dto.setReviewTime(review.getReviewTime());
        dto.setReviewContent(review.getReviewContent());
        dto.setRatingEnvironment(review.getRatingEnvironment());
        dto.setRatingPrice(review.getRatingPrice());
        dto.setRatingService(review.getRatingService());

        // 設定Base64後再寫入
        dto.setReviewPhotos(reviewPhotoList);

        // 判斷 ReviewPhoto 是否為空，以控制按鈕
        List<ReviewPhoto> reviewPhotos = review.getReviewPhotos();
        dto.setHasPhotos(reviewPhotos != null && !reviewPhotos.isEmpty());

        // 設定會員資訊
        dto.setMemberId(member.getId());
        dto.setName(member.getName());
        dto.setGender(member.getGender());
        dto.setProfilePhoto(member.getProfilePhoto());

        return dto;
    }


    /* 上傳多張圖片 */
    @Transactional
    private void addReviewPhotos(VendorReview review, List<MultipartFile> reviewPhotos) throws IOException {
        for (MultipartFile photo : reviewPhotos) {
            ReviewPhoto reviewPhoto = new ReviewPhoto();

            reviewPhoto.setVendorReview(review);
            reviewPhoto.setPhoto(photo.getBytes());

            reviewPhotoRepository.save(reviewPhoto);
        }
    }
}
