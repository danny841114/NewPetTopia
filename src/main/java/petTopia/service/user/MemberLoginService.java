package petTopia.service.user;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import petTopia.dto.user.response.MemberResponse;
import petTopia.model.user.User;
import petTopia.model.user.Member;
import petTopia.repository.user.UserRepository;
import petTopia.repository.user.MemberRepository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class MemberLoginService {
    private static final Logger logger = LoggerFactory.getLogger(MemberLoginService.class);

    private final UserRepository usersRepository;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final MemberService memberService;

    public Map<String, Object> memberLogin(String email, String password) {
        Map<String, Object> result = new HashMap<>();
        logger.info("開始會員登入流程，email: {}", email);

        try {
            Optional<User> userOptional = usersRepository.findByEmailAndUserRole(email, User.UserRole.MEMBER);

            if (userOptional.isEmpty()) {
                logger.warn("登入失敗：會員帳號不存在，email: {}", email);
                result.put("success", false);
                result.put("message", "會員帳號不存在");
                return result;
            }

            User user = userOptional.get();

            // 檢查是否是第三方登入帳號且未啟用本地密碼
            if (user.getProvider() != User.Provider.LOCAL && !user.isLocalEnabled()) {
                logger.warn("登入失敗：第三方登入帳號嘗試使用密碼登入，userId: {}, provider: {}", user.getId(), user.getProvider());
                result.put("success", false);
                result.put("message", "此帳號是使用" + user.getProvider().toString() + "註冊的，請使用對應的登入方式，或設定本地密碼");
                result.put("isThirdPartyAccount", true);
                result.put("provider", user.getProvider());
                result.put("email", user.getEmail());
                return result;
            }

            if (!passwordEncoder.matches(password, user.getPassword())) {
                logger.warn("登入失敗：密碼錯誤，userId: {}", user.getId());
                result.put("success", false);
                result.put("message", "密碼錯誤");
                return result;
            }

            // 檢查郵箱驗證狀態
            if (!user.isEmailVerified()) {
                logger.warn("登入失敗：郵箱未驗證，userId: {}", user.getId());
                result.put("success", false);
                result.put("message", "您的郵箱尚未驗證，請查收驗證郵件並完成驗證");
                result.put("needVerification", true);
                return result;
            }

            // 獲取會員信息
            Member member = memberRepository.findByUserId(user.getId()).orElse(null);

            result.put("success", true);
            result.put("message", "登入成功，歡迎回來！");
            result.put("user", user);
            result.put("userId", user.getId());
            result.put("memberName", member != null ? member.getName() : user.getEmail().split("@")[0]);
            result.put("userRole", user.getUserRole());
            result.put("email", user.getEmail());
            result.put("loggedInUser", user);
            logger.info("會員登入成功，userId: {}, email: {}", user.getId(), user.getEmail());

        } catch (Exception e) {
            logger.error("會員登入過程發生錯誤", e);
            result.put("success", false);
            result.put("message", "登入失敗：" + e.getMessage());
        }

        return result;
    }

    public User findByEmail(String email) {
        return usersRepository.findByEmailAndUserRole(email, User.UserRole.MEMBER)
                .orElseThrow(() -> new EntityNotFoundException("User with email '" + email + "' not found"));
    }

    public User findById(Integer id) {
        return usersRepository.findById(id).orElse(null);
    }
}