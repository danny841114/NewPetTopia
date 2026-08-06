package petTopia.controller.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import petTopia.dto.user.response.CheckEmailResponse;
import petTopia.service.user.RegistrationService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/user")
public class RegistrationController {
    private final RegistrationService registrationService;

    /**
     * 檢查 email 是否已存在
     */
    @GetMapping("/check-email")
    public ResponseEntity<?> checkEmail(@RequestParam String email) {
        CheckEmailResponse response = registrationService.checkEmailIsRegistered(email);
        return ResponseEntity.ok(response);
    }
}