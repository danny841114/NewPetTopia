package petTopia.service.vendor;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import petTopia.dto.vendor.ActivityReviewDetail;
import petTopia.dto.vendor.ActivityReviewDto;
import petTopia.dto.vendor.request.AddActivityReviewRequest;
import petTopia.model.user.Member;
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

    public List<ActivityReviewDetail> findReviewListByActivityId(Integer activityId) {
        List<VendorActivityReview> reviewList = vendorActivityReviewRepository.findByVendorActivityId(activityId);

        return reviewList.stream()
                .map(this::fromEntity)
                .collect(Collectors.toList());
    }

    public ActivityReviewDto findReviewById(Integer reviewId) {
        return vendorActivityReviewRepository.findById(reviewId)
                .map(ActivityReviewDto::fromEntity)
                .orElseThrow(() -> new EntityNotFoundException("Review not found"));
    }

    @Transactional
    public ActivityReviewDto modifyReviewById(Integer reviewId, String content) {
        VendorActivityReview review = vendorActivityReviewRepository.findById(reviewId)
                .orElseThrow(() -> new EntityNotFoundException("Review not found"));

        review.setReviewContent(content);
        review.setReviewTime(new Date());

        VendorActivityReview savedReview = vendorActivityReviewRepository.save(review);

        return ActivityReviewDto.fromEntity(savedReview);
    }

    // TODO:
    //  Modify request body
    //  activityId -> request body
    //  memberId -> credential
    @Transactional
    public ActivityReviewDto addReview(Integer activityId, AddActivityReviewRequest request) {
        VendorActivity activity = vendorActivityRepository.findById(activityId)
                .orElseThrow(() -> new EntityNotFoundException("Activity not found"));

        VendorActivityReview review = new VendorActivityReview();

        review.setMemberId(request.getMemberId());
        review.setVendor(activity.getVendor());
        review.setVendorActivity(activity);
        review.setReviewContent(request.getContent());
        review.setReviewTime(new Date());

        VendorActivityReview savedReview = vendorActivityReviewRepository.save(review);

        return ActivityReviewDto.fromEntity(savedReview);
    }

    public Boolean getReviewIsExisted(Integer memberId, Integer activityId) {
        VendorActivityReview review = vendorActivityReviewRepository.findByMemberIdAndVendorActivityId(memberId, activityId)
                .orElse(null);

        return review != null;
    }

    public List<ActivityReviewDto> findReviewsByMemberId(Integer memberId) {
        return vendorActivityReviewRepository.findByMemberId(memberId)
                .stream()
                .map(ActivityReviewDto::fromEntity)
                .toList();
    }

    public List<ActivityReviewDto> getReviewsByActivityId(Integer activityId) {
        return vendorActivityReviewRepository.findByVendorActivityId(activityId)
                .stream()
                .map(ActivityReviewDto::fromEntity)
                .toList();
    }

    @Transactional
    public void deleteReviewById(Integer reviewId) {
        vendorActivityReviewRepository.findById(reviewId)
                .ifPresent(vendorActivityReviewRepository::delete);
    }

    private ActivityReviewDetail fromEntity(VendorActivityReview review) {
        Member member = memberRepository.findById(review.getMemberId())
                .orElseThrow(() -> new EntityNotFoundException("Member not found"));

        ActivityReviewDetail dto = new ActivityReviewDetail();

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
}
