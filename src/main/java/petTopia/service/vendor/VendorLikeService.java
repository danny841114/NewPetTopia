package petTopia.service.vendor;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import petTopia.dto.vendor.VendorLikeDto;
import petTopia.model.user.Member;
import petTopia.model.vendor.Vendor;
import petTopia.model.vendor.VendorLike;
import petTopia.repository.user.MemberRepository;
import petTopia.repository.vendor.VendorLikeRepository;
import petTopia.repository.vendor.VendorRepository;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class VendorLikeService {
    private final VendorLikeRepository vendorLikeRepository;
    private final VendorRepository vendorRepository;
    private final MemberRepository memberRepository;

    public Boolean getActivityLikeStatus(Integer memberId, Integer vendorId) {
        Optional<VendorLike> vendorLike = vendorLikeRepository.findByMemberIdAndVendorId(memberId, vendorId);
        return vendorLike.isPresent();
    }

    public Boolean toggleVendorLike(Integer memberId, Integer vendorId) {
        VendorLike vendorLike = vendorLikeRepository.findByMemberIdAndVendorId(memberId, vendorId)
                .orElse(null);

        if (vendorLike == null) {
            VendorLike newVendorLike = new VendorLike();

            newVendorLike.setMemberId(memberId);
            newVendorLike.setVendorId(vendorId);

            vendorLikeRepository.save(newVendorLike);
            return true;
        } else {
            vendorLikeRepository.delete(vendorLike);
            return false;
        }
    }

    public List<VendorLikeDto> findMemberListByVendorId(Integer vendorId) {
        List<VendorLike> likeList = vendorLikeRepository.findByVendorId(vendorId);

        return likeList.stream()
                .map(this::fromEntity)
                .collect(Collectors.toList());
    }

    public List<VendorLikeDto> findListByMemberId(Integer memberId) {
        List<VendorLike> likeList = vendorLikeRepository.findByMemberId(memberId);

        return likeList.stream()
                .map(this::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public boolean deleteByLikeId(Integer likeId) {
        if (vendorLikeRepository.existsById(likeId)) {
            vendorLikeRepository.deleteById(likeId);
            return true;
        } else {
            return false;
        }
    }

    private VendorLikeDto fromEntity(VendorLike like) {
        Member member = memberRepository.findById(like.getMemberId())
                .orElseThrow(() -> new EntityNotFoundException("Member not found"));

        Vendor vendor = vendorRepository.findById(like.getVendorId())
                .orElseThrow(() -> new EntityNotFoundException("Vendor not found"));

        VendorLikeDto dto = new VendorLikeDto();

        dto.setId(like.getId());
        dto.setVendorId(like.getVendorId());
        dto.setVendorName(vendor.getName());
        dto.setVendorDescription(vendor.getDescription());
        dto.setVendorCategory(vendor.getVendorCategory().getName());
        dto.setMemberId(member.getId());
        dto.setName(member.getName());
        dto.setGender(member.getGender());
        dto.setProfilePhoto(member.getProfilePhoto());

        return dto;
    }
}
