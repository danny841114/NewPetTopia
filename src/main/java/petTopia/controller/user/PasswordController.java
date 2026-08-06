package petTopia.controller.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import petTopia.dto.user.request.ChangePasswordRequest;
import petTopia.service.user.MemberService;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class PasswordController {
    private final MemberService memberService;

    // TODO: change to PutMapping
    @PostMapping("/change-password")
    public ResponseEntity<Map<String, Object>> changePassword(@RequestBody ChangePasswordRequest request) {
        memberService.changePassword(request);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "密碼更改成功");
        response.put("email", request.getEmail());

        return ResponseEntity.ok(response);
    }
} 