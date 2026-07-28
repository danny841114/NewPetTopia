package petTopia.controller.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import petTopia.dto.user.request.BindAccountRequest;
import petTopia.dto.user.response.BindAccountResponse;
import petTopia.service.user.UsersService;
import petTopia.service.user.MemberLoginService;
import petTopia.jwt.JwtUtil;
import petTopia.model.user.User;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth/oauth2")
public class OAuth2BindController {
    private final UsersService usersService;
    private final MemberLoginService memberLoginService;
    private final JwtUtil jwtUtil;

    // 獲取綁定資訊
    @GetMapping("/bind-info")
    public ResponseEntity<?> getBindInfo() {
        // This method is no longer used in the new implementation
        return ResponseEntity.badRequest()
                .body(Map.of("error", "This method is no longer used in the new implementation"));
    }

    // 執行 OAuth2 帳號綁定
    @PostMapping("/bind")
    public ResponseEntity<?> bindAccount(@Valid @RequestBody BindAccountRequest request) {
        try {
            // 驗證當前用戶身份
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "請先登入"));
            }

            String currentEmail = authentication.getName();
            User currentUser = memberLoginService.findByEmail(currentEmail);
            if (currentUser == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "當前用戶不存在"));
            }

            Integer localUserId = request.getLocalUserId();
            String provider = request.getProvider();
            String email = request.getEmail();

            if (localUserId == null || provider == null || email == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "缺少綁定所需的資訊"));
            }

            // 驗證本地用戶ID是否與當前登入用戶匹配
            if (!localUserId.equals(currentUser.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "無權限綁定其他用戶的帳號"));
            }

            log.info("執行 OAuth2 帳號綁定 - 用戶ID: {}, 提供者: {}", localUserId, provider);

            // 檢查用戶是否存在
            User user = usersService.findById(localUserId);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "用戶不存在"));
            }

            // 執行綁定
            try {
                usersService.bindOAuth2Account(localUserId, User.Provider.valueOf(provider));
            } catch (Exception e) {
                log.error("OAuth2 帳號綁定過程發生錯誤", e);
                return ResponseEntity.internalServerError()
                        .body(Map.of("error", "帳號綁定失敗：" + e.getMessage()));
            }

            // 重新獲取更新後的用戶資訊
            user = usersService.findById(localUserId);

            // 生成新的 JWT
            String token = jwtUtil.generateToken(
                    user.getEmail(),
                    user.getId(),
                    user.getUserRole().toString()
            );

            log.info("OAuth2 帳號綁定成功 - 用戶ID: {}", localUserId);

            BindAccountResponse response = BindAccountResponse.builder()
                    .success(true)
                    .message("帳號綁定成功")
                    .token(token)
                    .userId(user.getId())
                    .email(user.getEmail())
                    .role(user.getUserRole().toString())
                    .provider(user.getProvider().toString())
                    .build();

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("OAuth2 帳號綁定失敗", e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "帳號綁定失敗：" + e.getMessage()));
        }
    }
}
