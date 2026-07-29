package petTopia.controller.user;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import petTopia.dto.user.request.*;
import petTopia.dto.user.response.*;
import petTopia.jwt.JwtUtil;
import petTopia.model.user.User;
import petTopia.model.user.Admin;
import petTopia.model.user.Member;
import petTopia.service.user.AdminService;
import petTopia.repository.user.UserRepository;
import petTopia.repository.user.AdminRepository;
import petTopia.repository.user.MemberRepository;

import java.util.HashMap;
import java.util.Map;
import java.time.LocalDateTime;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    private final AdminService adminService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final AdminRepository adminRepository;
    private final MemberRepository memberRepository;

    private static final String saEmail = "sa@pettopia.com";

    /**
     * 初始化超級管理員帳號
     */
    @PostMapping("/init-sa")
    @PreAuthorize("permitAll()")
    public ResponseEntity<?> initSuperAdmin() {
        log.info("初始化超級管理員帳號");

        try {
            // 檢查是否已存在超級管理員
            Optional<User> userOptional = userRepository.findByEmailAndUserRole(saEmail, User.UserRole.ADMIN);
            if (userOptional.isPresent() && userOptional.get().getIsSuperAdmin()) {
                return ResponseEntity.badRequest().body(Map.of("error", "超級管理員帳號已存在"));
            }

            // 創建超級管理員帳號
            User superAdmin = new User();
            superAdmin.setEmail(saEmail);
            superAdmin.setPassword(passwordEncoder.encode("test123"));
            superAdmin.setUserRole(User.UserRole.ADMIN);
            superAdmin.setEmailVerified(true);
            superAdmin.setIsSuperAdmin(true);
            superAdmin.setAdminLevel(1);
            superAdmin.setProvider("LOCAL");
            superAdmin.setLocalEnabled(true);

            // 創建並關聯 Admin 記錄
            Admin admin = new Admin();
            admin.setName("Super Admin");
            admin.setRole(Admin.AdminRole.SA);
            admin.setUsers(superAdmin);
            admin.setRegistrationDate(LocalDateTime.now());

            // 保存超級管理員帳號和關聯的 Admin 記錄
            adminService.createAdmin(superAdmin, true);

            log.info("超級管理員帳號初始化成功");

            Map<String, Object> response = new HashMap<>();
            response.put("message", "超級管理員帳號初始化成功");
            response.put("email", saEmail);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("超級管理員帳號初始化失敗", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "初始化失敗：" + e.getMessage()));
        }
    }

    /**
     * 管理員登入
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        log.info("處理管理員登入請求 - 電子郵件: {}", request.getEmail());

        if (request.getEmail() == null || request.getPassword() == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "電子郵件和密碼不能為空"));
        }

        try {
            // 使用 adminService 進行認證
            User admin = adminService.adminLogin(request);

            if (admin == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "登入失敗，請確認帳號密碼"));
            }

            // 生成 JWT
            String token = jwtUtil.generateToken(request.getEmail(), admin.getId(), "ADMIN");

            log.info("管理員登入成功 - ID: {}", admin.getId());

            LoginResponse response = LoginResponse.builder()
                    .message("登入成功")
                    .token(token)
                    .adminId(admin.getId())
                    .role("ADMIN")
                    .isAuthenticated(true)
                    .build();

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("管理員登入過程發生異常", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "登入失敗，請確認帳號密碼"));
        }
    }

    /**
     * 管理員登出
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        log.info("處理管理員登出請求");
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok(Map.of("message", "登出成功"));
    }

    /**
     * 獲取管理後台資料
     */
    @GetMapping("/dashboard")
    public ResponseEntity<?> getDashboardData() {
        log.info("獲取管理後台資料");

        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            Dashboard.AdminInfo adminInfo = Dashboard.AdminInfo.builder()
                    .id(userDetails.getUsername())
                    .email(userDetails.getUsername())
                    .role("ADMIN")
                    .build();

            Dashboard dashboard = Dashboard.builder()
                    .members(adminService.getAllMembers())
                    .vendors(adminService.getAllVendors())
                    .adminInfo(adminInfo)
                    .build();

            return ResponseEntity.ok(dashboard);
        } catch (Exception e) {
            log.error("獲取管理後台資料失敗", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "獲取資料失敗：" + e.getMessage()));
        }
    }

    /**
     * 切換用戶狀態（啟用/停用）
     */
    @PutMapping("/users/{userId}/status")
    public ResponseEntity<?> toggleUserStatus(@PathVariable Integer userId,
                                              @Valid @RequestBody ToggleUserStatusRequest request) {
        Boolean isActive = request.getIsActive();

        log.info("切換用戶狀態 - 用戶ID: {}, 狀態: {}", userId, isActive);

        try {
            adminService.toggleUserStatus(userId, isActive);

            log.info("用戶狀態切換成功 - 用戶ID: {}, 狀態: {}", userId, isActive);
            return ResponseEntity.ok(Map.of(
                    "message", "用戶狀態已更新",
                    "userId", userId,
                    "isActive", isActive
            ));
        } catch (Exception e) {
            log.error("用戶狀態切換失敗", e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "操作失敗：" + e.getMessage()));
        }
    }

    /**
     * 獲取所有會員（支援分頁、搜尋和篩選）
     */
    @GetMapping("/members")
    public ResponseEntity<?> getAllMembers(@ModelAttribute MemberSearchRequest request) {
        log.info("獲取會員列表 - 頁碼: {}, 每頁數量: {}", request.getPage(), request.getSize());

        try {
            MemberPageResponse response = adminService.getAllMembersWithFilters(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("獲取會員列表失敗", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "獲取資料失敗：" + e.getMessage()));
        }
    }

    /**
     * 新增會員
     */
    @PostMapping("/members")
    public ResponseEntity<?> createMember(CreateMemberRequest request) {
        log.info("新增會員");

        try {
            // 驗證必要欄位
            if (request.getEmail() == null || request.getPassword() == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "電子郵件和密碼為必填欄位"));
            }

            User newMember = adminService.createMember(request);
            return ResponseEntity.ok(Map.of(
                    "message", "會員新增成功",
                    "memberId", newMember.getId()
            ));
        } catch (Exception e) {
            log.error("新增會員失敗", e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "新增失敗：" + e.getMessage()));
        }
    }

    /**
     * 刪除會員
     */
    @DeleteMapping("/members/{memberId}")
    public ResponseEntity<?> deleteMember(@PathVariable Integer memberId) {
        log.info("刪除會員 - ID: {}", memberId);

        try {
            adminService.deleteMember(memberId);
            return ResponseEntity.ok(Map.of("message", "會員刪除成功"));
        } catch (Exception e) {
            log.error("刪除會員失敗", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "刪除失敗：" + e.getMessage()));
        }
    }

    /**
     * 批量更新會員狀態
     */
    @PostMapping("/members/batch-update")
    public ResponseEntity<?> batchUpdateMembers(@Valid @RequestBody BatchUpdateMembersRequest request) {
        log.info("批量更新會員狀態");

        try {
            adminService.batchUpdateMemberStatus(request);
            return ResponseEntity.ok(Map.of("message", "會員狀態更新成功"));
        } catch (Exception e) {
            log.error("批量更新會員狀態失敗", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "更新失敗：" + e.getMessage()));
        }
    }

    /**
     * 獲取所有商家
     */
//    @GetMapping("/vendors")
//    public ResponseEntity<?> getAllVendors() {
//        logger.info("獲取所有商家資料");
//        
//        try {
//            return ResponseEntity.ok(Map.of("vendors", adminService.getAllVendors()));
//        } catch (Exception e) {
//            logger.error("獲取商家資料失敗", e);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                .body(Map.of("error", "獲取資料失敗：" + e.getMessage()));
//        }
//    }

    /**
     * 檢查管理員登入狀態
     */
    @GetMapping("/status")
    public ResponseEntity<?> checkLoginStatus() {
        log.info("檢查管理員登入狀態");

        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null
                    && authentication.isAuthenticated()
                    && !"anonymousUser".equals(authentication.getPrincipal())) {

                UserDetails userDetails = (UserDetails) authentication.getPrincipal();

                // 檢查是否為管理員
                if (userDetails.getAuthorities()
                        .stream()
                        .noneMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"))) {
                    return ResponseEntity.ok(Map.of("isLoggedIn", false));
                }

                return ResponseEntity.ok(Map.of(
                        "isLoggedIn", true,
                        "adminId", userDetails.getUsername(),
                        "email", userDetails.getUsername(),
                        "role", "ADMIN"
                ));
            }

        } catch (Exception e) {
            log.error("檢查管理員登入狀態失敗", e);
        }

        return ResponseEntity.ok(Map.of("isLoggedIn", false));
    }

    /**
     * 獲取當前管理員資訊
     */
    @GetMapping("/current-admin")
    public ResponseEntity<?> getCurrentAdmin() {
        log.info("獲取當前管理員資訊");

        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null
                    || !authentication.isAuthenticated()
                    || "anonymousUser".equals(authentication.getPrincipal())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "未登入"));
            }

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String email = userDetails.getUsername();

            // 獲取管理員資訊
            Optional<User> userOptional = userRepository.findByEmailAndUserRole(email, User.UserRole.ADMIN);
            if (userOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "找不到管理員資訊"));
            }

            User admin = userOptional.get();

            // 獲取關聯的 Admin 記錄
            Admin adminRecord = adminRepository.findById(admin.getId())
                    .orElseThrow(() -> new EntityNotFoundException("Admin user not found"));


            AdminResponse response = AdminResponse.fromEntity(admin, adminRecord);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("獲取當前管理員資訊失敗", e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "獲取資訊失敗：" + e.getMessage()));
        }
    }

    @GetMapping("/members/{memberId}")
    public ResponseEntity<?> getMember(@PathVariable Integer memberId) {
        log.info("獲取會員資料 - ID: {}", memberId);

        try {
            User user = userRepository.findById(memberId)
                    .orElseThrow(() -> new EntityNotFoundException("找不到該會員"));

            if (user.getUserRole() != User.UserRole.MEMBER) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "該用戶不是會員"));
            }

            Member member = memberRepository.findByUserId(memberId)
                    .orElseThrow(() -> new EntityNotFoundException("找不到會員資料"));

            MemberResponse response = MemberResponse.fromEntity(user, member);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("獲取會員資料失敗", e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "獲取資料失敗：" + e.getMessage()));
        }
    }

    @PutMapping("/members/{memberId}")
    public ResponseEntity<?> updateMember(@PathVariable Integer memberId, @RequestBody UpdateMemberRequest request) {
        try {
            User user = userRepository.findById(memberId)
                    .orElseThrow(() -> new EntityNotFoundException("找不到該會員"));

            if (user.getUserRole() != User.UserRole.MEMBER) {
                return ResponseEntity.badRequest().body("該用戶不是會員");
            }

            // 更新會員資料
            Member member = memberRepository.findByUserId(memberId)
                    .orElseThrow(() -> new EntityNotFoundException("找不到該會員資料"));

            // 更新會員基本資料
            if (request.getName() != null) member.setName(request.getName());
            if (request.getPhone() != null) member.setPhone(request.getPhone());
            if (request.getAddress() != null) member.setAddress(request.getAddress());
            if (request.getEmailVerified() != null) user.setEmailVerified(request.getEmailVerified());

            if (request.getBirthdate() != null && !request.getBirthdate().trim().isEmpty()) {
                String birthdateStr = request.getBirthdate();
                try {
                    LocalDate birthdate = LocalDate.parse(birthdateStr);
                    member.setBirthdate(birthdate);
                } catch (DateTimeParseException e) {
                    return ResponseEntity.badRequest().body("生日日期格式不正確");
                }
            }

            memberRepository.save(member);
            userRepository.save(user);

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("更新會員資料失敗", e);
            return ResponseEntity.badRequest().body("更新會員資料失敗: " + e.getMessage());
        }
    }
} 