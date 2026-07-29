package petTopia.service.user;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import petTopia.model.user.Member;
import petTopia.model.user.User;
import petTopia.repository.user.MemberRepository;
import petTopia.repository.user.UserRepository;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class MemberService {
    private final MemberRepository memberRepository;
    private final UserRepository usersRepository;

    @Transactional
    public Member createOrUpdateMember(Member member) {
        validateMemberInput(member);

        User user = usersRepository.findById(member.getId())
                .orElseThrow(() -> new EntityNotFoundException("用戶不存在"));

        member.setUser(user);

        Optional<Member> memberOptional = memberRepository.findById(member.getId());

        if (memberOptional.isPresent()) {
            Member existingMember = memberOptional.get();

            existingMember.setName(member.getName());
            existingMember.setPhone(member.getPhone());
            existingMember.setBirthdate(member.getBirthdate());
            existingMember.setGender(member.getGender());
            existingMember.setAddress(member.getAddress());
            existingMember.setUpdatedDate(LocalDateTime.now());

            if (member.getProfilePhoto() != null) {
                existingMember.setProfilePhoto(member.getProfilePhoto());
            }

            return memberRepository.save(existingMember);
        } else {
            member.setStatus(false);
            member.setUpdatedDate(LocalDateTime.now());

            return memberRepository.save(member);
        }
    }

    // TODO: Same method
    public Member getMemberById(Integer userId) {
        return memberRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Member not found"));
    }

    // TODO: Same method
    public Member findById(Integer userId) {
        return memberRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Member not found"));
    }

    public List<Member> findAllById(List<Integer> memberIds) {
        return memberRepository.findAllById(memberIds);
    }

    private void validateMemberInput(Member member) {
        // 確保必要的關聯存在
        if (member.getUser() == null || member.getUser().getId() == null) {
            throw new IllegalArgumentException("用戶關聯不能為空");
        }

        // 確保ID匹配
        if (!member.getId().equals(member.getUser().getId())) {
            throw new IllegalArgumentException("用戶ID不匹配");
        }
    }
}
