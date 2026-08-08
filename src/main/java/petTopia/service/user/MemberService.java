package petTopia.service.user;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import petTopia.dto.user.request.ChangePasswordRequest;
import petTopia.dto.user.request.UpdateProfile;
import petTopia.model.user.Member;
import petTopia.model.user.User;
import petTopia.repository.user.MemberRepository;
import petTopia.repository.user.UserRepository;
import petTopia.util.ImageConverter;
import petTopia.util.StringHelper;

@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class MemberService {
    private final MemberRepository memberRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Member createOrUpdateMember(Member member) {
        if (member.getUser() == null || member.getUser().getId() == null) {
            throw new IllegalArgumentException("User from member is null");
        }

        if (!member.getId().equals(member.getUser().getId())) {
            throw new IllegalArgumentException("Member ID and User ID are not match");
        }

        User user = userRepository.findById(member.getId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

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

    public Member getMemberById(Integer memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("Member not found"));
    }

    public void checkIsRegisteredViaEmail(User user, Member member, String email) {
        // 對於第三方登入用戶，特別檢查名稱格式
        if (user.getProvider() != User.Provider.LOCAL) {
            String currentName = member.getName();

            if (StringHelper.isEmailFormat(currentName, email)) {
                String emailUsername = email.split("@")[0];
                log.info("名稱是郵箱格式，轉換為更友好的格式: {} -> {}", currentName, emailUsername);

                if (!emailUsername.equals(currentName)) {
                    log.info("更新會員資料中的名稱為更友好的格式: {} -> {}", currentName, emailUsername);

                    member.setName(emailUsername);
                    member.setUpdatedDate(LocalDateTime.now());

                    this.createOrUpdateMember(member);
                }
            }
        }
    }

    public void updateProfile(Member member, UpdateProfile request) {
        member.setName(request.getName());
        member.setPhone(request.getPhone());
        member.setGender(request.getGender());
        member.setAddress(request.getAddress());
        if (request.getBirthdate() != null) member.setBirthdate(request.getBirthdate());
        member.setUpdatedDate(LocalDateTime.now());

        this.createOrUpdateMember(member);
    }

    public void uploadPhoto(Member member, MultipartFile photo) throws IOException {
        if (photo != null && !photo.isEmpty()) {
            byte[] processedImage = ImageConverter.processImage(photo);

            member.setProfilePhoto(processedImage);
            member.setUpdatedDate(LocalDateTime.now());

            this.createOrUpdateMember(member);
        } else {
            log.warn("Photo is null, no updates will be executed.");
            throw new BadRequestException("Photo is null");
        }
    }

    @Transactional
    public void changePassword(ChangePasswordRequest request) {
        String email = request.getEmail();
        String newPassword = request.getNewPassword();

        if (email == null || newPassword == null) {
            throw new IllegalArgumentException("電子郵件和密碼不能為空");
        }

        User user = userRepository.findByEmailAndUserRole(email, User.UserRole.MEMBER)
                .orElseThrow(() -> new EntityNotFoundException("User with email '%s' not found".formatted(email)));

        user.setPassword(passwordEncoder.encode(newPassword));

        userRepository.save(user);

        log.info("用戶 {} 密碼更改成功", email);
    }

    public byte[] getProfilePhotoById(Integer memberId) {
        return memberRepository.findById(memberId)
                .map(Member::getProfilePhoto)
                .orElse(null);
    }
}
