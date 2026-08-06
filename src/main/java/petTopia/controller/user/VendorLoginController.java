package petTopia.controller.user;

import java.util.Map;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import petTopia.dto.user.request.LoginRequest;
import petTopia.dto.user.response.VendorEligibilityResponse;
import petTopia.service.user.VendorLoginService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/vendor/auth")
public class VendorLoginController {
    private final VendorLoginService vendorService;

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest request) {
        Map<String, Object> response = vendorService.vendorLogin(request);
        return (Boolean) response.get("success")
                ? ResponseEntity.ok(response)
                : ResponseEntity.internalServerError().body(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout() {
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok(Map.of("message", "商家登出成功"));
    }

    @GetMapping("/profile")
    public ResponseEntity<Map<String, Object>> getVendorProfile() {
        Authentication authentication = getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "請先登入"));
        }

        Map<String, Object> vendorInfo = vendorService.getVendorInfo(authentication.getName());
        return ResponseEntity.ok(vendorInfo);
    }

    @PostMapping("/oauth2/login")
    public ResponseEntity<Map<String, Object>> handleOAuth2Login(@AuthenticationPrincipal OAuth2User oauth2User) {
        Map<String, Object> loginResult = vendorService.vendorOAuth2Login(oauth2User);
        return (Boolean) loginResult.get("success")
                ? ResponseEntity.ok(loginResult)
                : ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(loginResult);
    }

    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getLoginStatus() {
        Authentication authentication = getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.ok(Map.of("isLoggedIn", false));
        }

        Map<String, Object> vendorInfo = vendorService.getVendorInfo(authentication.getName());
        return ResponseEntity.ok(vendorInfo);
    }

    /**
     * 檢查用戶是否有資格成為商家
     */
    @GetMapping("/convert/check")
    public ResponseEntity<?> checkVendorEligibility() {
        Authentication authentication = getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "請先登入"));
        }

        VendorEligibilityResponse response = vendorService.checkVendorEligibility(authentication.getName());
        return ResponseEntity.ok(response);
    }

    /**
     * 將普通用戶轉換為商家
     */
    @PostMapping("/convert")
    public ResponseEntity<Map<String, Object>> convertToVendor() {
        Authentication authentication = getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "請先登入"));
        }

        Map<String, Object> result = vendorService.convertToVendor(authentication.getName());
        return (Boolean) result.get("success")
                ? ResponseEntity.ok(result)
                : ResponseEntity.internalServerError().body(result);
    }

    private Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }
}
