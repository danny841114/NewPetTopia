package petTopia.controller.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import petTopia.dto.user.request.LoginRequest;
import petTopia.dto.user.response.LoginResponse;
import petTopia.dto.user.response.LoginStatus;
import petTopia.service.user.MemberLoginService;
import petTopia.jwt.JwtUtil;
import petTopia.service.user.MemberService;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class MemberLoginController {
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final MemberLoginService memberLoginService;
    private final MemberService memberService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest credentials) {
        try {
            LoginResponse loginResult = memberLoginService.memberLogin(credentials);
            if (loginResult.getSuccess()) {
                return ResponseEntity.ok(loginResult);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", loginResult.getMessage()));
            }
        } catch (Exception e) {
            log.error("登入失敗", e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "登入失敗: " + e.getMessage()));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok(Map.of("message", "登出成功"));
    }

    @GetMapping("/status")
    public ResponseEntity<?> getLoginStatus(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.ok(Map.of("isLoggedIn", false));
        }

        String token = authHeader.substring(7);
        try {
            LoginStatus loginStatus = memberLoginService.getLoginStatus(token);
            return ResponseEntity.ok(loginStatus);
        } catch (Exception e) {
            log.error("獲取登入狀態失敗", e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "獲取登入狀態失敗: " + e.getMessage()));
        }
    }
}
