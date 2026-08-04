package petTopia.controller.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import petTopia.dto.user.request.RegisterRequest;
import petTopia.service.user.VendorRegistrationService;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/vendor/auth")
public class VendorRegisterController {
    private final VendorRegistrationService vendorRegistrationService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        try {
            // 使用註冊服務處理註冊
            vendorRegistrationService.register(request);

            log.info("商家註冊成功 - 電子郵件: {}", request.getEmail());
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of(
                            "message", "註冊成功，請查收驗證郵件後登入",
                            "email", request.getEmail()
                    ));

        } catch (Exception e) {
            log.error("註冊過程發生異常 - 電子郵件: {}", request.getEmail(), e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "註冊失敗：" + e.getMessage()));
        }
    }

    @GetMapping("/verify-email")
    public ResponseEntity<?> verifyEmail(@RequestParam String token) {
        log.info("處理商家電子郵件驗證請求 - 令牌: {}", token);

        if (token == null || token.trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "驗證令牌不能為空"));
        }

        try {
            boolean verified = vendorRegistrationService.verifyEmail(token);

            if (verified) {
                log.info("商家電子郵件驗證成功 - 令牌: {}", token);
                return ResponseEntity.ok(Map.of(
                        "message", "驗證成功，請登入",
                        "verified", true
                ));
            } else {
                log.warn("商家電子郵件驗證失敗 - 令牌: {}", token);
                return ResponseEntity.badRequest()
                        .body(Map.of(
                                "error", "驗證失敗，請重新註冊",
                                "verified", false
                        ));
            }
        } catch (Exception e) {
            log.error("商家電子郵件驗證過程發生異常 - 令牌: {}", token, e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "驗證過程發生錯誤：" + e.getMessage()));
        }
    }
} 