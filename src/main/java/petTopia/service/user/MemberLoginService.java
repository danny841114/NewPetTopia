package petTopia.service.user;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.view.RedirectView;
import petTopia.dto.user.request.LoginRequest;
import petTopia.dto.user.response.LoginResponse;
import petTopia.dto.user.response.LoginStatus;
import petTopia.jwt.JwtUtil;
import petTopia.model.user.User;
import petTopia.model.user.Member;
import petTopia.repository.user.UserRepository;
import petTopia.repository.user.MemberRepository;
import petTopia.util.StringHelper;

import java.util.Optional;

@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class MemberLoginService {
    private final UserRepository usersRepository;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final MemberService memberService;

    // 前端應用的URL
    // TODO: move to application.properties
    private static final String FRONTEND_URL = "http://localhost:5173";

    @Transactional
    public LoginResponse memberLogin(LoginRequest request) {
        String email = request.getEmail();
        String password = request.getPassword();

        if (email == null || password == null) {
            throw new IllegalArgumentException("Email or password is null");
        }

        log.info("開始會員登入流程，email: {}", email);

        try {
            Optional<User> userOptional = usersRepository.findByEmailAndUserRole(email, User.UserRole.MEMBER);

            if (userOptional.isEmpty()) {
                log.warn("登入失敗：會員帳號不存在，email: {}", email);

                return LoginResponse.builder()
                        .success(false)
                        .message("會員帳號不存在")
                        .build();
            }

            User user = userOptional.get();

            // 檢查是否是第三方登入帳號且未啟用本地密碼
            if (user.getProvider() != User.Provider.LOCAL && !user.getLocalEnabled()) {
                log.warn("登入失敗：第三方登入帳號嘗試使用密碼登入，userId: {}, provider: {}", user.getId(), user.getProvider().name());

                return LoginResponse.builder()
                        .error("此帳號是使用" + user.getProvider().toString() + "註冊的，請使用對應的登入方式，或設定本地密碼")
                        .isThirdPartyAccount(true)
                        .provider(user.getProvider().name())
                        .email(user.getEmail())
                        .build();
            }

            if (!passwordEncoder.matches(password, user.getPassword())) {
                log.warn("登入失敗：密碼錯誤，userId: {}", user.getId());

                return LoginResponse.builder()
                        .success(false)
                        .message("密碼錯誤")
                        .build();
            }

            // 檢查郵箱驗證狀態
            if (!user.isEmailVerified()) {
                log.warn("登入失敗：郵箱未驗證，userId: {}", user.getId());

                return LoginResponse.builder()
                        .success(false)
                        .message("您的郵箱尚未驗證，請查收驗證郵件並完成驗證")
                        .needVerification(true)
                        .build();
            }

            // 獲取會員信息
            log.info("會員登入成功，userId: {}, email: {}", user.getId(), user.getEmail());

            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(email, password);
            Authentication authentication = authenticationManager.authenticate(authToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            String token = jwtUtil.generateToken(user.getEmail(), user.getId(), user.getUserRole().toString());

            Member member = memberRepository.findById(user.getId()).orElse(null);

            if (member == null) {
                member = new Member();

                member.setId(user.getId());
                member.setUser(user);
                member.setName(email.split("@")[0]);    // 使用友好的顯示名稱
                member.setStatus(false);

                member = memberService.createOrUpdateMember(member);
            }

            // 檢查名稱是否為郵箱格式
            String displayName = member.getName();
            if (StringHelper.isEmailFormat(displayName, email)) {
                displayName = email.split("@")[0];

                // 如果名稱已更改，更新資料庫
                if (!displayName.equals(member.getName())) {
                    member.setName(displayName);
                    memberService.createOrUpdateMember(member);
                }
            }

            return LoginResponse.builder()
                    .success(true)
                    .token(token)
                    .userId(user.getId())
                    .email(user.getEmail())
                    .memberId(member.getId())
                    .name(displayName)
                    .memberName(displayName)
                    .role(user.getProvider().name())
                    .provider(user.getProvider().name())
                    .message("登入成功")
                    .build();
        } catch (Exception e) {
            log.error("會員登入過程發生錯誤", e);

            return LoginResponse.builder()
                    .success(false)
                    .message("登入失敗：" + e.getMessage())
                    .build();
        }
    }

    public RedirectView oauth2Callback(String email) {
        try {
            User user = usersRepository.findByEmailAndUserRole(email, User.UserRole.MEMBER).orElse(null);

            String token = user != null
                    ? jwtUtil.generateToken(email, user.getId(), user.getUserRole().toString())
                    : null;

            boolean isNewUser = (user == null);

            StringBuilder redirectUrl = new StringBuilder(FRONTEND_URL);
            redirectUrl.append("/login?oauth2Success=true");
            redirectUrl.append("&token=").append(token);
            redirectUrl.append("&userId=").append(isNewUser ? null : user.getId());
            redirectUrl.append("&email=").append(email);
            redirectUrl.append("&role=").append(isNewUser ? null : user.getUserRole().toString());
            if (isNewUser) redirectUrl.append("&newUser=true");

            return new RedirectView(redirectUrl.toString());
        } catch (Exception e) {
            // 處理錯誤情況，重定向到帶有錯誤信息的登入頁面
            return new RedirectView(FRONTEND_URL + "/login?error=true&message=" + e.getMessage());
        }
    }

    public User findByEmail(String email) {
        return usersRepository.findByEmailAndUserRole(email, User.UserRole.MEMBER).orElse(null);
    }

    public User findByEmailIfAbsentThrowException(String email) {
        return usersRepository.findByEmailAndUserRole(email, User.UserRole.MEMBER)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

    public LoginStatus getLoginStatus(String token) {
        String email = jwtUtil.extractUsername(token);
        if (!jwtUtil.validateToken(token, email)) return getFailedLoginStatus();

        Integer userId = jwtUtil.extractUserId(token);
        Member member = memberRepository.findById(userId).orElse(null);
        if (member == null) return getFailedLoginStatus();

        User user = this.findByEmail(email);

        // 檢查名稱是否為郵箱格式，如果是則使用更友好的格式
        String displayName = member.getName();
        if (user != null && user.getProvider() != User.Provider.LOCAL && StringHelper.isEmailFormat(displayName, email)) {
            displayName = email.split("@")[0];

            // 如果名稱已更改，更新資料庫
            if (!displayName.equals(member.getName())) {
                log.info("更新會員資料中的名稱為更友好的格式: {} -> {}", member.getName(), displayName);
                member.setName(displayName);
                memberService.createOrUpdateMember(member);
            }
        }

        return LoginStatus.builder()
                .isLoggedIn(true)
                .userId(userId)
                .email(email)
                .name(displayName)
                .memberName(displayName)
                .role(jwtUtil.extractUserRole(token))
                .provider(user != null ? user.getProvider().toString() : null)
                .build();
    }

    private LoginStatus getFailedLoginStatus() {
        return LoginStatus.builder()
                .isLoggedIn(false)
                .build();
    }
}