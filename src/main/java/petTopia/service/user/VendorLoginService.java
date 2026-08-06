package petTopia.service.user;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Date;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import petTopia.dto.user.request.LoginRequest;
import petTopia.dto.user.response.VendorEligibilityResponse;
import petTopia.jwt.JwtUtil;
import petTopia.model.user.User;
import petTopia.model.vendor.Vendor;
import petTopia.repository.user.UserRepository;
import petTopia.repository.vendor.VendorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class VendorLoginService {
    private static final Logger logger = LoggerFactory.getLogger(VendorLoginService.class);

    private final UserRepository usersRepository;
    private final VendorRepository vendorRepository;
    private final PasswordEncoder passwordEncoder;
    private final EntityManager entityManager;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public User findByEmail(String email) {
        return usersRepository.findByEmailAndUserRole(email, User.UserRole.VENDOR)
                .orElseThrow(() -> new EntityNotFoundException("User with email '" + email + "' not found"));
    }

    // TODO: Change repository response type
    public Map<String, Object> vendorLogin(LoginRequest request) {
        String email = request.getEmail();
        String password = request.getPassword();

        Map<String, Object> result = new HashMap<>();

        logger.info("開始商家登入流程，email: {}", email);

        try {
            if (email == null || email.isBlank()) {
                logger.warn("登入失敗：電子郵件為空");

                result.put("success", false);
                result.put("error", "請輸入電子郵件地址");

                return result;
            }

            if (password == null || password.isBlank()) {
                logger.warn("登入失敗：密碼為空");

                result.put("success", false);
                result.put("error", "請輸入密碼");

                return result;
            }

            if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                logger.warn("登入失敗：郵件格式不正確，email: {}", email);

                result.put("success", false);
                result.put("error", "請輸入有效的電子郵件地址");

                return result;
            }

            // 查找用戶
            logger.info("開始查找商家用戶，email: {}", email);
            Optional<User> userOptional = usersRepository.findByEmailAndUserRole(email, User.UserRole.VENDOR);
            logger.info("查詢用戶結果: {}, 用戶角色: {}",
                    userOptional.isPresent() ? "找到用戶" : "未找到用戶",
                    userOptional.isPresent() ? userOptional.get().getUserRole() : "無");

            if (userOptional.isEmpty()) {
                // 檢查是否是會員帳號
                Optional<User> memberUserOptional = usersRepository.findByEmailAndUserRole(email, User.UserRole.MEMBER);
                if (memberUserOptional.isPresent()) {
                    logger.warn("登入失敗：會員帳號嘗試登入商家系統，email: {}", email);

                    result.put("success", false);
                    result.put("error", "此帳號為會員帳號，請使用會員登入頁面");

                    return result;
                }

                logger.warn("登入失敗：找不到商家帳號，email: {}", email);

                result.put("success", false);
                result.put("error", "此電子郵件尚未註冊，請先申請成為商家");

                return result;
            }

            User user = userOptional.get();

            logger.info("檢查帳號類型，Provider: {}", user.getProvider());
            if (user.getProvider() == User.Provider.GOOGLE) {
                logger.warn("登入失敗：Google帳號嘗試使用密碼登入，userId: {}", user.getId());

                result.put("success", false);
                result.put("error", "此帳號是使用Google註冊的，請點擊「使用Google登入」按鈕");

                return result;
            }

            logger.info("開始驗證密碼，userId: {}", user.getId());
            boolean passwordMatch = passwordEncoder.matches(password, user.getPassword());
            logger.info("密碼驗證結果: {}, userId: {}", passwordMatch ? "成功" : "失敗", user.getId());

            if (!passwordMatch) {
                logger.warn("登入失敗：密碼錯誤，userId: {}", user.getId());

                result.put("success", false);
                result.put("error", "密碼錯誤，請重新輸入");

                return result;
            }

            // 檢查郵箱驗證狀態
            logger.info("檢查郵箱驗證狀態，isEmailVerified: {}, userId: {}", user.isEmailVerified(), user.getId());
            if (!user.isEmailVerified()) {
                logger.warn("登入失敗：郵箱未驗證，userId: {}", user.getId());

                result.put("success", false);
                result.put("error", "您的郵箱尚未驗證，請查收驗證郵件並完成驗證");
                result.put("needVerification", true);

                return result;
            }

            // 獲取商家信息
            logger.info("開始獲取商家信息，userId: {}", user.getId());
            Vendor vendor = vendorRepository.findByUserIdWithJoin(user.getId()).orElse(null);
            logger.info("查詢商家信息結果: {}, userId: {}",
                    vendor != null ? "找到商家信息" : "未找到商家信息",
                    user.getId());

            if (vendor == null) {
                logger.error("登入失敗：商家信息不存在，userId: {}", user.getId());

                result.put("success", false);
                result.put("error", "無法找到您的商家資料，請聯繫客服處理");

                return result;
            }

            // 登入成功，設置返回信息
            logger.info("商家登入成功，userId: {}, vendorName: {}, userRole: {}",
                    user.getId(),
                    vendor.getName() != null ? vendor.getName() : "未設置名稱",
                    user.getUserRole());

            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(email, password);
            Authentication authentication = authenticationManager.authenticate(authToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            String token = jwtUtil.generateToken(user.getEmail(), user.getId(), user.getUserRole().toString());

            result.put("success", true);
            result.put("token", token);
            result.put("userId", user.getId());
            result.put("vendorName", vendor.getName() != null ? vendor.getName() : "未設置名稱");
            result.put("email", email);
            result.put("message", "登入成功");
        } catch (Exception e) {
            logger.error("商家登入發生異常，email: {}", email, e);

            result.put("success", false);
            result.put("error", "系統發生錯誤，請稍後再試或聯繫客服");
        }

        return result;
    }

    @Transactional
    public Map<String, Object> vendorOAuth2Login(OAuth2User oauth2User) {
        Map<String, Object> result = new HashMap<>();

        try {
            String provider = oauth2User.getAttribute("provider") != null
                    ? oauth2User.getAttribute("provider")
                    : null;
            logger.info("處理OAuth2登入 - 提供者: {}", provider);

            String email = oauth2User.getAttribute("email");

            // 檢查是否有對應的商家帳號
            Optional<User> vendorUserOptional = usersRepository.findByEmailAndUserRole(email, User.UserRole.VENDOR);
            if (vendorUserOptional.isEmpty()) {
                result.put("success", false);
                result.put("message", "此Google帳號尚未註冊為商家");
                return result;
            }

            User vendor = vendorUserOptional.get();

            // 檢查商家狀態
            Optional<Vendor> vendorInfo = vendorRepository.findByUserId(vendor.getId());
            if (vendorInfo.isEmpty()) {
                result.put("success", false);
                result.put("message", "找不到商家資料");
                return result;
            }

            // 檢查email是否已驗證
            if (!vendor.isEmailVerified()) {
                result.put("success", false);
                result.put("message", "請先驗證您的電子郵件");
                return result;
            }

            String credentials = oauth2User.getAttribute("provider") + "_" + email;
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(email, credentials);
            Authentication authentication = authenticationManager.authenticate(authToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            String token = jwtUtil.generateToken(vendor.getEmail(), vendor.getId(), vendor.getUserRole().toString());

            logger.info("OAuth2商家登入成功 - 使用者ID: {}", vendor.getId());

            result.put("success", true);
            result.put("token", token);
            result.put("userId", vendor.getId());
            result.put("vendorName", vendor.getEmail());
            result.put("email", email);
            result.put("role", vendor.getUserRole().toString());
            result.put("provider", oauth2User.getAttribute("provider"));
            result.put("message", "OAuth2 登入成功");
        } catch (Exception e) {
            logger.error("OAuth2商家登入過程發生錯誤", e);

            result.put("success", false);
            result.put("message", "系統發生錯誤，請稍後再試");
        }

        return result;
    }

    public Map<String, Object> getVendorInfo(String email) {
        Map<String, Object> result = new HashMap<>();

        User user = this.findByEmail(email);
        Integer userId = user.getId();

        // Double check
        if (!usersRepository.existsById(userId)) {
            throw new EntityNotFoundException("用戶不存在");
        }

        if (user.getUserRole() != User.UserRole.VENDOR) {
            throw new RuntimeException("此帳號不是商家帳號");
        }

        Vendor vendor = vendorRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("商家資料不存在"));

        result.put("isLoggedIn", true);
        result.put("userId", vendor.getId());
        result.put("vendorName", vendor.getName());
        result.put("email", user.getEmail());
        result.put("role", "VENDOR");

        return result;
    }

    /**
     * 檢查用戶是否有未完成的商家申請
     */
    public boolean hasPendingVendorApplication(Integer userId) {
        try {
            // TODO: 實作檢查未完成申請的邏輯
            return false;
        } catch (Exception e) {
            logger.error("檢查未完成商家申請失敗", e);
            return false;
        }
    }

    /**
     * 檢查用戶是否有被拒絕的商家申請
     */
    public boolean hasRejectedVendorApplication(Integer userId) {
        try {
            // TODO: 實作檢查被拒絕申請的邏輯
            return false;
        } catch (Exception e) {
            logger.error("檢查被拒絕商家申請失敗", e);
            return false;
        }
    }

    /**
     * 檢查用戶是否有資格成為商家
     */
    public boolean isNotEligibleForVendor(Integer userId) {
        try {
            User user = usersRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("用戶不存在"));

            // 檢查用戶是否已經是商家
            if (user.getUserRole() == User.UserRole.VENDOR) return true;

            // 檢查郵箱是否已驗證
            if (!user.isEmailVerified()) return true;

            // 檢查是否有未完成的申請
            if (hasPendingVendorApplication(userId)) return true;

            // 檢查是否有被拒絕的申請
            if (hasRejectedVendorApplication(userId)) return true;

            // TODO: 添加其他資格檢查邏輯
            // 例如：檢查用戶年齡、信用評分等

            return false;
        } catch (Exception e) {
            logger.error("檢查商家資格失敗", e);
            return true;
        }
    }

    /**
     * 將用戶轉換為商家
     */
    @Transactional
    public Map<String, Object> convertToVendor(String email) {
        Map<String, Object> result = new HashMap<>();

        try {
            User user = this.findByEmail(email);
            Integer userId = user.getId();

            // Double check
            if (!usersRepository.existsById(userId)) {
                throw new EntityNotFoundException("用戶不存在");
            }

            // 檢查用戶是否已經是商家
            if (user.getUserRole() == User.UserRole.VENDOR) {
                throw new BadRequestException("用戶已經是商家身份");
            }

            // 檢查用戶是否有資格成為商家
            if (isNotEligibleForVendor(userId)) {
                throw new BadRequestException("用戶不符合成為商家的資格");
            }

            // 創建新的商家資料
            Vendor vendor = new Vendor();
            vendor.setId(userId); // 使用用戶ID作為商家ID
            vendor.setName(user.getEmail().split("@")[0]); // 預設使用郵箱前綴作為商家名稱
            vendor.setStatus(false); // 預設狀態為未啟用
            vendor.setRegistrationDate(new Date()); // 設置註冊時間
            vendor.setUpdatedDate(new Date()); // 設置更新時間

            // 保存商家資料
            vendorRepository.save(vendor);

            // 更新用戶角色
            user.setUserRole(User.UserRole.VENDOR);
            usersRepository.save(user);

            logger.info("用戶 {} 成功轉換為商家", userId);

            String token = jwtUtil.generateToken(user.getEmail(), user.getId(), "VENDOR");

            user.setUserRole(User.UserRole.VENDOR);
            entityManager.merge(user);
            entityManager.flush();

            result.put("success", true);
            result.put("token", token);
            result.put("vendorId", vendor.getId());
            result.put("email", user.getEmail());
            result.put("role", "VENDOR");
            result.put("message", "成功轉換為商家");
        } catch (Exception e) {
            logger.error("轉換商家失敗", e);

            result.put("success", false);
            result.put("message", e.getMessage());
        }

        return result;
    }

    public VendorEligibilityResponse checkVendorEligibility(String email) {
        User user = this.findByEmail(email);
        Integer userId = user.getId();

        if (user.getUserRole().toString().equals("VENDOR")) {
            return VendorEligibilityResponse.builder()
                    .eligible(false)
                    .hasExistingAccount(true)
                    .message("您已經是商家身份")
                    .build();
        }

        if (this.hasPendingVendorApplication(userId)) {
            return VendorEligibilityResponse.builder()
                    .eligible(false)
                    .hasExistingAccount(false)
                    .message("您有未完成的商家申請，請等待審核")
                    .build();
        }

        if (this.hasRejectedVendorApplication(userId)) {
            return VendorEligibilityResponse.builder()
                    .eligible(false)
                    .hasExistingAccount(false)
                    .message("您的商家申請已被拒絕，請聯繫客服")
                    .build();
        }

        if (this.isNotEligibleForVendor(userId)) {
            return VendorEligibilityResponse.builder()
                    .eligible(false)
                    .hasExistingAccount(false)
                    .message("您目前不符合成為商家的資格")
                    .build();
        }

        return VendorEligibilityResponse.builder()
                .eligible(true)
                .hasExistingAccount(false)
                .message("您可以申請成為商家")
                .build();
    }
}
