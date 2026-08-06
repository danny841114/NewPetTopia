package petTopia.controller.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import petTopia.service.user.VendorRegistrationService;

import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/vendor")
public class VendorConversionController {
    private final VendorRegistrationService vendorRegistrationService;

    /**
     * 會員轉換為商家
     */
    @PostMapping("/convert")
    public ResponseEntity<?> convertToVendor(@RequestBody Map<String, Boolean> request,
                                             @RequestHeader(value = "Authorization", required = false) String authHeader) {
        ResponseEntity<Map<String, String>> UNAUTHORIZED = checkAuthHeader(authHeader);
        if (UNAUTHORIZED != null) return UNAUTHORIZED;

        Boolean confirm = request.get("confirm");
        String token = authHeader.substring(7);
        Map<String, Object> response = vendorRegistrationService.switchRoleToVendor(token, confirm);
        return ResponseEntity.ok(response);
    }

    /**
     * 檢查當前用戶是否可以轉換為商家
     */
    @GetMapping("/convert/check")
    public ResponseEntity<?> checkConversionEligibility(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        ResponseEntity<Map<String, String>> UNAUTHORIZED = checkAuthHeader(authHeader);
        if (UNAUTHORIZED != null) return UNAUTHORIZED;

        String token = authHeader.substring(7);
        Map<String, Object> response = vendorRegistrationService.checkConversionEligibility(token);
        return ResponseEntity.ok(response);
    }

    /**
     * 商家切換回會員
     */
    @PostMapping("/switch-back")
    public ResponseEntity<?> switchBackToMember(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        ResponseEntity<Map<String, String>> UNAUTHORIZED = checkAuthHeader(authHeader);
        if (UNAUTHORIZED != null) return UNAUTHORIZED;

        String token = authHeader.substring(7);
        Map<String, Object> response = vendorRegistrationService.switchRoleBackToMember(token);
        return ResponseEntity.ok(response);
    }

    /**
     * 檢查登入
     */
    private ResponseEntity<Map<String, String>> checkAuthHeader(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "請先登入"));
        }

        return null;
    }
}
