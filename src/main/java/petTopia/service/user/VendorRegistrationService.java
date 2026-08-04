package petTopia.service.user;

import java.time.LocalDateTime;
import java.util.*;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import petTopia.dto.user.request.RegisterRequest;
import petTopia.dto.user.response.VendorRegisterResponse;
import petTopia.model.user.User;
import petTopia.model.vendor.Vendor;
import petTopia.model.user.Member;
import petTopia.repository.user.UserRepository;
import petTopia.repository.vendor.VendorRepository;
import petTopia.repository.user.MemberRepository;

@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class VendorRegistrationService {
    private final UserRepository usersRepository;
    private final VendorRepository vendorRepository;
    private final MemberRepository memberRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public VendorRegisterResponse register(RegisterRequest request) {
        String email = request.getEmail();
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email is null");
        }

        String password = request.getPassword();
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password is null");
        }

        String confirmPassword = request.getConfirmPassword();
        if (!password.equals(confirmPassword)) {
            throw new IllegalArgumentException("Confirming password is not the same as password");
        }

        Optional<User> userOptional = usersRepository.findByEmailAndUserRole(email, User.UserRole.VENDOR);
        if (userOptional.isPresent()) {
            log.warn("註冊失敗 - 電子郵件已存在: {}", email);
            throw new HttpClientErrorException(HttpStatus.CONFLICT);
        }

        log.info("開始商家註冊流程，email: {}", email);

        try {
            // 檢查是否已存在相同email的商家帳號
            Optional<User> existingVendor = usersRepository.findByEmailAndUserRole(email, User.UserRole.VENDOR);
            if (existingVendor.isPresent()) {
                log.warn("註冊失敗：商家帳號已存在，email: {}", email);

                return VendorRegisterResponse.builder()
                        .success(false)
                        .message("此 email 已註冊為商家")
                        .build();
            }

            // 創建用戶基本信息
            User user = new User();

            user.setEmail(email);
            user.setPassword(passwordEncoder.encode(password));
            user.setUserRole(User.UserRole.VENDOR);
            user.setProvider(User.Provider.LOCAL);

            // 生成驗證令牌
            String token = UUID.randomUUID().toString();
            user.setVerificationToken(token);
            user.setTokenExpiry(LocalDateTime.now().plusHours(24));

            // 保存用戶
            User savedUser = usersRepository.save(user);

            log.info("商家用戶資訊儲存成功，userId: {}", savedUser.getId());

            // 創建商家資料
            Vendor vendor = new Vendor();

            vendor.setUser(savedUser);  // 設置關聯，ID 會自動對應
            vendor.setRegistrationDate(new Date());
            vendor.setUpdatedDate(new Date());
            vendor.setStatus(false); // 預設為未認證狀態
            vendor.setVendorCategory(null); // 預設分類

            // 保存商家資料
            Vendor savedVendor = vendorRepository.save(vendor);
            log.info("商家詳細資訊儲存成功，vendorId: {}", savedVendor.getId());

            // 發送驗證郵件
            emailService.sendVerificationEmail(user.getEmail(), token);
            log.info("驗證郵件發送成功，email: {}", user.getEmail());

            return VendorRegisterResponse.builder()
                    .success(true)
                    .message("註冊成功，請查收驗證郵件")
                    .userId(savedUser.getId())
                    .vendorId(savedVendor.getId())
                    .build();
        } catch (Exception e) {
            log.error("商家註冊過程發生錯誤", e);

            return VendorRegisterResponse.builder()
                    .success(false)
                    .message("註冊失敗：" + e.getMessage())
                    .build();
        }
    }

    // TODO: Change repository response type
    @Transactional
    public boolean verifyEmail(String token) {
        Optional<User> userOptional = usersRepository.findByVerificationToken(token);

        if (userOptional.isPresent()
                && !userOptional.get().isEmailVerified()
                && LocalDateTime.now().isBefore(userOptional.get().getTokenExpiry())) {
            try {
                // 更新用戶驗證狀態
                User user = userOptional.get();
                user.setEmailVerified(true);
                user.setVerificationToken(null);
                user.setTokenExpiry(null);
                usersRepository.save(user);

                // 更新商家狀態
                Vendor vendor = vendorRepository.findByUserId(user.getId()).orElse(null);
                if (vendor != null) {
                    vendor.setStatus(true);
                    vendorRepository.save(vendor);
                    log.info("商家驗證完成，userId: {}", user.getId());
                    return true;
                } else {
                    log.error("商家驗證失敗：找不到商家資料，userId: {}", user.getId());
                }
            } catch (Exception e) {
                log.error("商家驗證過程發生錯誤", e);
            }
        } else {
            log.warn("商家驗證失敗：驗證碼無效或已過期");
        }

        return false;
    }

    public User findVendorByEmail(String email) {
        return usersRepository.findByEmailAndUserRole(email, User.UserRole.VENDOR)
                .orElseThrow(() -> new EntityNotFoundException("User with email '" + email + "' not found"));
    }

    public User findMemberByEmail(String email) {
        return usersRepository.findByEmailAndUserRole(email, User.UserRole.MEMBER)
                .orElseThrow(() -> new EntityNotFoundException("User with email '" + email + "' not found"));

    }

    // TODO: Change repository response type
    @Transactional
    public Map<String, Object> convertMemberToVendor(Integer memberId) {
        Map<String, Object> result = new HashMap<>();

        try {
            // 檢查會員是否存在
            User memberUser = usersRepository.findById(memberId)
                    .orElseThrow(() -> new EntityNotFoundException("會員不存在"));

            if (memberUser.getUserRole() != User.UserRole.MEMBER) {
                throw new BadRequestException("只有會員帳號可以轉換為商家");
            }

            // 檢查是否已經有相同 email 的商家帳號
            usersRepository.findByEmailAndUserRole(memberUser.getEmail(), User.UserRole.VENDOR)
                    .orElseThrow(() -> new BadRequestException("此 email 已註冊為商家"));

            Member member = memberRepository.findById(memberId)
                    .orElseThrow(() -> new EntityNotFoundException("會員資料不存在"));

            // 創建新的商家用戶
            User vendorUser = new User();

            vendorUser.setEmail(memberUser.getEmail());
            vendorUser.setPassword(memberUser.getPassword()); // 直接使用已加密的密碼
            vendorUser.setUserRole(User.UserRole.VENDOR);
            vendorUser.setProvider(memberUser.getProvider());
            vendorUser.setEmailVerified(true); // 因為會員已驗證過 email

            // 先保存商家用戶
            User savedVendorUser = usersRepository.save(vendorUser);

            // 創建新的商家資料
            Vendor vendor = new Vendor();

            vendor.setUser(savedVendorUser);  // 設置用戶關聯
            vendor.setName(member.getName());
            vendor.setPhone(member.getPhone());
            vendor.setAddress(member.getAddress());
            vendor.setStatus(false);  // 預設未認證
            vendor.setRegistrationDate(new Date());
            vendor.setUpdatedDate(new Date());
            vendor.setVendorCategory(null); // 預設分類

            // 保存商家資料
            Vendor savedVendor = vendorRepository.save(vendor);

            // 重新獲取完整的商家用戶資訊
            User newVendorUser = usersRepository.findById(savedVendorUser.getId())
                    .orElseThrow(() -> new EntityNotFoundException("商家用戶資料不存在"));

            result.put("success", true);
            result.put("message", "商家帳號創建成功");
            result.put("vendorUser", newVendorUser);
            result.put("vendorId", savedVendor.getId());
            result.put("vendorEmail", newVendorUser.getEmail());
            result.put("rawPassword", memberUser.getPassword()); // 返回已加密的密碼供認證使用
        } catch (Exception e) {
            log.error("商家轉換過程發生錯誤", e);
            result.put("success", false);
            result.put("message", e.getMessage());
        }

        return result;
    }
} 