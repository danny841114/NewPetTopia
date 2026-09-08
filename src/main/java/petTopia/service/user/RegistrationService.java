package petTopia.service.user;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import petTopia.dto.user.response.CheckEmailResponse;
import petTopia.model.user.User;
import petTopia.model.user.Member;
import petTopia.repository.user.UserRepository;
import petTopia.repository.user.MemberRepository;

@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class RegistrationService {
    private final UserRepository usersRepository;
    private final MemberRepository memberRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Map<String, Object> register(String email, String password) {
        Map<String, Object> result = new HashMap<>();

        try {
            User user = new User();

            // 檢查郵箱是否已存在
            String trimmedEmail = email.toLowerCase().trim();
            if (findByEmail(trimmedEmail) != null) {
                result.put("success", false);
                result.put("message", "此 email 已註冊為會員");
                return result;
            }
            user.setEmail(trimmedEmail);

            // 生成6位數驗證碼
            String code = String.format("%06d", new Random().nextInt(1000000));
            user.setVerificationToken(code);
            user.setTokenExpiry(LocalDateTime.now().plusMinutes(5));

            // 身分屬性
            user.setUserRole(User.UserRole.MEMBER);
            user.setProvider(User.Provider.LOCAL);

            // 加密密碼
            String encodedPassword = passwordEncoder.encode(password);
            user.setPassword(encodedPassword);

            // 保存用戶
            usersRepository.save(user);
            log.info("會員用戶資訊儲存成功，userId: {}", user.getId());

            // 創建會員資料
            Member member = new Member();
            member.setUser(user);
            member.setName("");
            member.setPhone("");
            member.setStatus(false);
            member.setUpdatedDate(LocalDateTime.now());

            // 保存會員資料
            memberRepository.save(member);
            log.info("會員詳細資訊儲存成功，memberId: {}", member.getId());

            // 發送驗證郵件
            emailService.sendVerificationEmail(user.getEmail(), code);
            log.info("驗證郵件發送成功，email: {}", user.getEmail());

            result.put("success", true);
            result.put("message", "註冊成功，請查收驗證郵件");
            result.put("userId", user.getId());
            result.put("memberId", member.getId());
        } catch (Exception e) {
            log.error("會員註冊過程發生錯誤", e);
            result.put("success", false);
            result.put("message", "註冊失敗：" + e.getMessage());
        }

        return result;
    }

    @Transactional
    public boolean verifyEmail(String email, String code) {
        Optional<User> userOptional = usersRepository.findByEmailAndVerificationToken(email, code);

        if (userOptional.isPresent()) {
            User user = userOptional.get();

            if (!user.isEmailVerified() && LocalDateTime.now().isBefore(user.getTokenExpiry())) {
                // 更新用戶驗證狀態
                user.setEmailVerified(true);
                user.setVerificationToken(null);
                user.setTokenExpiry(null);
                usersRepository.save(user);

                // 更新會員狀態
                memberRepository.findByUserId(user.getId()).ifPresent(member -> {
                    member.setStatus(true);
                    memberRepository.save(member);
                    log.info("會員驗證成功，userId: {}", user.getId());
                });

                return true;
            }
        }

        return false;
    }

    public User findByEmail(String email) {
        // 查找任何類型的帳號（會員、商家、本地、Google）
        String trimmedEmail = email.toLowerCase().trim();
        return usersRepository.findByEmailAndUserRole(trimmedEmail, User.UserRole.MEMBER)
                .orElse(null);
    }

    @Transactional
    public void updateUser(User user) {
        // 保存用戶信息
        log.info("更新用戶信息，userId: {}", user.getId());
        usersRepository.save(user);
    }

    public CheckEmailResponse checkEmailIsRegistered(String email) {
        log.info("檢查 email 是否已存在 - email: {}", email);

        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("檢查 email 失敗 - email 為空");
        }

        User existingUser = this.findByEmail(email);
        if (existingUser != null) {
            String message;
            if (existingUser.getProvider() == User.Provider.GOOGLE) {
                message = "此 email 已使用 Google 帳號登入過，請點擊「使用 Google 登入」按鈕";
            } else if (existingUser.getUserRole() == User.UserRole.VENDOR) {
                message = "此 email 已註冊為商家帳號，請使用其他 email 註冊會員";
            } else {
                message = "此 email 已註冊為會員帳號，請直接登入";
            }

            log.info("Email 已存在 - email: {}, 用戶類型: {}", email, existingUser.getUserRole());

            return CheckEmailResponse.builder()
                    .exists(true)
                    .message(message)
                    .userRole(existingUser.getUserRole())
                    .provider(existingUser.getProvider())
                    .build();
        } else {
            log.info("Email 可用 - email: {}", email);

            return CheckEmailResponse.builder()
                    .exists(true)
                    .message("此 email 可用於註冊")
                    .build();
        }
    }
}
