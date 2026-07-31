package petTopia.controller.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.http.ResponseEntity;
import petTopia.service.user.RegistrationService;

import java.util.Map;

/**
 * 電子郵件驗證控制器，負責處理用戶的電子郵件驗證請求。
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth") // 設定 API 路徑前綴
public class EmailVerificationController {
    private final RegistrationService registrationService; // 註冊服務，負責驗證電子郵件

    /**
     * 電子郵件驗證 API
     *
     * @param token 用戶收到的驗證令牌
     * @return ResponseEntity，包含驗證結果
     */
    @GetMapping("/verify-email")
    public ResponseEntity<?> verifyEmail(@RequestParam String token) {
        log.info("處理電子郵件驗證請求 - 令牌: {}", token);

        // 檢查 token 是否為空
        if (token == null || token.trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "驗證令牌不能為空"));
        }

        try {
            boolean verified = registrationService.verifyEmail(token); // 驗證令牌

            if (verified) {
                log.info("電子郵件驗證成功 - 令牌: {}", token);
                return ResponseEntity.ok(Map.of(
                        "message", "電子郵件驗證成功",
                        "verified", true
                ));
            } else {
                log.warn("電子郵件驗證失敗 - 令牌: {}", token);
                return ResponseEntity.badRequest()
                        .body(Map.of(
                                "error", "電子郵件驗證失敗",
                                "verified", false
                        ));
            }
        } catch (Exception e) {
            log.error("電子郵件驗證過程發生異常 - 令牌: {}", token, e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "驗證過程發生錯誤：" + e.getMessage()));
        }
    }
}
