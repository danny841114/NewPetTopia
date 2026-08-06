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
import petTopia.model.user.User;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth/oauth2")
public class OAuth2BindController {
    private final UsersService usersService;
    private final MemberLoginService memberLoginService;

    // 執行 OAuth2 帳號綁定
    @PostMapping("/bind")
    public ResponseEntity<?> bindAccount(@Valid @RequestBody BindAccountRequest request) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "請先登入"));
            }

            Integer localUserId = request.getLocalUserId();
            String provider = request.getProvider();
            String email = request.getEmail();

            if (localUserId == null || provider == null || email == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "缺少綁定所需的資訊"));
            }

            User currentUser = memberLoginService.findByEmail(authentication.getName());
            if (currentUser == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "當前用戶不存在"));
            }

            if (!localUserId.equals(currentUser.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "無權限綁定其他用戶的帳號"));
            }

            BindAccountResponse response = usersService.bindOAuth2Account(localUserId, User.Provider.valueOf(provider));

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("OAuth2 帳號綁定失敗", e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "帳號綁定失敗：" + e.getMessage()));
        }
    }
}
