package petTopia.service.vendor;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import petTopia.dto.vendor.ActivityLikeDto;
import petTopia.model.user.Member;
import petTopia.model.vendor.ActivityLike;
import petTopia.model.vendor.VendorActivity;
import petTopia.repository.user.MemberRepository;
import petTopia.repository.vendor.ActivityLikeRepository;
import petTopia.repository.vendor.VendorActivityRepository;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class ActivityLikeService {
    private final ActivityLikeRepository activityLikeRepository;
    private final VendorActivityRepository vendorActivityRepository;
    private final MemberRepository memberRepository;

    public boolean getActivityLikeStatus(Integer memberId, Integer activityId) {
        Optional<ActivityLike> optional = activityLikeRepository.findByMemberIdAndVendorActivityId(memberId, activityId);
        return optional.isPresent();
    }

    @Transactional
    public boolean toggleActivityLike(Integer memberId, Integer activityId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("Member not found"));

        VendorActivity activity = vendorActivityRepository.findById(activityId)
                .orElseThrow(() -> new EntityNotFoundException("Activity not found"));

        Optional<ActivityLike> optional = activityLikeRepository.findByMemberIdAndVendorActivityId(memberId, activityId);

        if (optional.isEmpty()) {
            ActivityLike newActivityLike = new ActivityLike();
            newActivityLike.setMember(member);
            newActivityLike.setVendorActivity(activity);
            activityLikeRepository.save(newActivityLike);
            return true;
        } else {
            activityLikeRepository.delete(optional.get());
            return false;
        }
    }

    // N+1 problem is existing
    public List<ActivityLikeDto> finLikesByActivityId(Integer activityId) {
        VendorActivity activity = vendorActivityRepository.findById(activityId)
                .orElseThrow(() -> new EntityNotFoundException("Activity not found"));

        List<ActivityLike> likeList = activityLikeRepository.findByVendorActivity(activity);

        return likeList.stream()
                .map(this::fromEntity)
                .collect(Collectors.toList());
    }

    public List<ActivityLike> findLikeListByMemberId(Integer memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("Member not found"));
        return activityLikeRepository.findByMember(member);
    }

    @Transactional
    public boolean deleteByLikeId(Integer likeId) {
        if (activityLikeRepository.existsById(likeId)) {
            activityLikeRepository.deleteById(likeId);
            return true;
        }

        return false;
    }

    private ActivityLikeDto fromEntity(ActivityLike like) {
        Integer likeMemberId = like.getMember().getId();
        Member member = memberRepository.findById(likeMemberId)
                .orElseThrow(() -> new EntityNotFoundException("Member not found"));

        ActivityLikeDto dto = new ActivityLikeDto();
        dto.setId(like.getId());
        dto.setVendorId(like.getVendorActivity().getVendor().getId());
        dto.setActivityId(like.getVendorActivity().getId());
        dto.setMemberId(member.getId());
        dto.setName(member.getName());
        dto.setGender(member.getGender());
        dto.setProfilePhoto(member.getProfilePhoto());

        return dto;
    }
}
