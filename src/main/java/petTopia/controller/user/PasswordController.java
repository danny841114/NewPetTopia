package petTopia.controller.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import petTopia.dto.user.request.ChangePasswordRequest;
import petTopia.model.user.User;
import petTopia.service.user.MemberLoginService;
import petTopia.repository.user.UserRepository;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class PasswordController {
    private static final Logger logger = LoggerFactory.getLogger(PasswordController.class);

    private final MemberLoginService memberLoginService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // TODO: change to PutMapping
    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest request) {
        String email = request.getEmail();
        String newPassword = request.getNewPassword();

        if (email == null || newPassword == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "電子郵件和密碼不能為空"));
        }

        try {
            // 獲取當前用戶
            User user = memberLoginService.findByEmail(email);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "找不到用戶"));
            }

            // 更新密碼
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);

            logger.info("用戶 {} 密碼更改成功", email);
            return ResponseEntity.ok(Map.of(
                    "message", "密碼更改成功",
                    "email", email
            ));
        } catch (Exception e) {
            logger.error("更改密碼失敗", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "更改密碼失敗：" + e.getMessage()));
        }
    }
} 