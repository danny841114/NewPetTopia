package petTopia.controller.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.access.prepost.PreAuthorize;
import petTopia.dto.user.request.*;
import petTopia.dto.user.response.*;
import petTopia.model.user.User;
import petTopia.service.user.AdminService;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    private final AdminService adminService;

    private static final String saEmail = "sa@pettopia.com";

    /**
     * 初始化超級管理員帳號
     */
    @PostMapping("/init-sa")
    @PreAuthorize("permitAll()")
    public ResponseEntity<?> initSuperAdmin() {
        log.info("初始化超級管理員帳號");

        try {
            adminService.createSuperAdmin(saEmail);

            log.info("超級管理員帳號初始化成功");

            Map<String, Object> response = new HashMap<>();
            response.put("message", "超級管理員帳號初始化成功");
            response.put("email", saEmail);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("超級管理員帳號初始化失敗", e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "初始化失敗：" + e.getMessage()));
        }
    }

    /**
     * 管理員登入
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        log.info("處理管理員登入請求 - 電子郵件: {}", request.getEmail());

        try {
            LoginResponse loginResponse = adminService.adminLogin(request);

            log.info("管理員登入成功 - ID: {}", loginResponse.getAdminId());

            return ResponseEntity.ok(loginResponse);
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
            Dashboard dashboard = adminService.getDashboard();
            return ResponseEntity.ok(dashboard);
        } catch (Exception e) {
            log.error("獲取管理後台資料失敗", e);
            return ResponseEntity.internalServerError()
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
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "獲取資料失敗：" + e.getMessage()));
        }
    }

    /**
     * 新增會員
     */
    // TODO: Check whether parameter is request body
    @PostMapping("/members")
    public ResponseEntity<?> createMember(@RequestBody CreateMemberRequest request) {
        log.info("新增會員");

        try {
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
            return ResponseEntity.internalServerError()
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
            return ResponseEntity.internalServerError()
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
        Map<String, Object> response = adminService.verifyLoginStatus();
        return ResponseEntity.ok(response);
    }

    /**
     * 獲取當前管理員資訊
     */
    @GetMapping("/current-admin")
    public ResponseEntity<?> getCurrentAdmin() {
        log.info("獲取當前管理員資訊");

        try {
            AdminResponse response = adminService.getCurrentAdminInfo();
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
            MemberResponse response = adminService.getMemberById(memberId);
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
            adminService.updateMember(memberId, request);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("更新會員資料失敗", e);
            return ResponseEntity.badRequest().body("更新會員資料失敗: " + e.getMessage());
        }
    }
}