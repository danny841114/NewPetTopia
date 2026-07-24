package petTopia.service.vendor;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import petTopia.dto.vendor.ActivityReviewDto;
import petTopia.model.user.Member;
import petTopia.model.vendor.Vendor;
import petTopia.model.vendor.VendorActivity;
import petTopia.model.vendor.VendorActivityReview;
import petTopia.repository.user.MemberRepository;
import petTopia.repository.vendor.VendorActivityRepository;
import petTopia.repository.vendor.VendorActivityReviewRepository;
import petTopia.repository.vendor.VendorRepository;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class VendorActivityReviewService {
    private final VendorRepository vendorRepository;
    private final VendorActivityRepository vendorActivityRepository;
    private final VendorActivityReviewRepository vendorActivityReviewRepository;
    private final MemberRepository memberRepository;

    /* 尋找單一活動其所有的評分及留言 */
    public List<VendorActivityReview> findActivityReviewByVendorId(Integer activityId) {
        return vendorActivityReviewRepository.findByVendorActivityId(activityId);
    }

    /* 新增或修改活動評論 */
    @Transactional
    public void addOrModifyActivityReview(Integer memberId, Integer activityId, String content) {
        VendorActivityReview review = vendorActivityReviewRepository.findByMemberIdAndVendorActivityId(memberId, activityId)
                .orElse(null);

        VendorActivity activity = vendorActivityRepository.findById(activityId)
                .orElseThrow(() -> new EntityNotFoundException("Activity not found"));

        Vendor vendor = activity.getVendor();

        if (review == null) {
            VendorActivityReview newReview = new VendorActivityReview();

            newReview.setMemberId(memberId);
            newReview.setVendor(vendor);
            newReview.setVendorActivity(activity);
            newReview.setReviewContent(content);
            newReview.setReviewTime(new Date());

            vendorActivityReviewRepository.save(newReview);
        } else {
            review.setReviewContent(content);
            review.setReviewTime(new Date());

            vendorActivityReviewRepository.save(review);
        }
    }

    /* 刪除某成員對某活動之評論及評分 */
    @Transactional
    public void deleteReviewByMemberIdAndVendorId(Integer memberId, Integer activityId) {
        vendorActivityReviewRepository.findByMemberIdAndVendorActivityId(memberId, activityId)
                .ifPresent(vendorActivityReviewRepository::delete);
    }

    /* 查詢某個Activity所有評價之DTO */
    public List<ActivityReviewDto> findReviewListByActivityId(Integer activityId) {
        List<VendorActivityReview> reviewList = vendorActivityReviewRepository.findByVendorActivityId(activityId);

        return reviewList.stream()
                .map(this::fromEntity)
                .collect(Collectors.toList());
    }

    /* 藉由 ID 尋找評論 */
    public VendorActivityReview findReviewById(Integer reviewId) {
        return vendorActivityReviewRepository.findById(reviewId)
                .orElseThrow(() -> new EntityNotFoundException("Review not found"));
    }

    /* 藉由ID修改評論 */
    @Transactional
    public VendorActivityReview rewriteReviewById(Integer reviewId, String content) {
        VendorActivityReview review = this.findReviewById(reviewId);

        review.setReviewContent(content);
        review.setReviewTime(new Date());

        return vendorActivityReviewRepository.save(review);
    }

    /* 新增文字評論 */
    @Transactional
    public VendorActivityReview addReview(Integer memberId, Integer activityId, String content) {
        VendorActivity activity = vendorActivityRepository.findById(activityId)
                .orElseThrow(() -> new EntityNotFoundException("Activity not found"));

        VendorActivityReview review = new VendorActivityReview();

        review.setMemberId(memberId);
        review.setVendor(activity.getVendor());
        review.setVendorActivity(activity);
        review.setReviewContent(content);
        review.setReviewTime(new Date());

        return vendorActivityReviewRepository.save(review);
    }

    /* 尋找某會員是否有對某活動留下評論 */
    public boolean getReviewIsExisted(Integer memberId, Integer activityId) {
        VendorActivityReview review = vendorActivityReviewRepository.findByMemberIdAndVendorActivityId(memberId, activityId)
                .orElse(null);

        return review != null;
    }

    /* 藉由 memberId 找到所有評論 */
    public List<VendorActivityReview> findReviewListByMemberId(Integer memberId) {
        return vendorActivityReviewRepository.findByMemberId(memberId);
    }

    private ActivityReviewDto fromEntity(VendorActivityReview review) {
        Member member = memberRepository.findById(review.getMemberId())
                .orElseThrow(() -> new EntityNotFoundException("Member not found"));

        ActivityReviewDto dto = new ActivityReviewDto();

        // 設定評價資訊
        dto.setReviewId(review.getId());
        dto.setVendorId(review.getVendor().getId());
        dto.setReviewTime(review.getReviewTime());
        dto.setReviewContent(review.getReviewContent());

        // 設定會員資訊
        dto.setMemberId(member.getId());
        dto.setName(member.getName());
        dto.setGender(member.getGender());
        dto.setProfilePhoto(member.getProfilePhoto());

        return dto;
    }

    public List<VendorActivityReview> getReviewsByActivityId(Integer activityId) {
        return vendorActivityReviewRepository.findByVendorActivityId(activityId);
    }

    @Transactional
    public void deleteReviewById(Integer reviewId) {
        vendorActivityReviewRepository.findById(reviewId)
                .ifPresent(vendorActivityReviewRepository::delete);
    }
}
