package petTopia.controller.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.core.context.SecurityContextHolder;
import petTopia.dto.user.request.UpdateProfile;
import petTopia.dto.user.response.MemberResponse;
import petTopia.model.user.Member;
import petTopia.model.user.User;
import petTopia.service.user.MemberService;
import petTopia.service.user.MemberLoginService;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/member")
public class MemberController {
    private final MemberService memberService;
    private final MemberLoginService memberLoginService;

    // 透過 Email 獲得 User，再透過 User 獲得 Member
    private Member getMemberViaUserViaEmail() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        User user = memberLoginService.findByEmail(email);

        return memberService.getMemberById(user.getId());
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile() {
        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = memberLoginService.findByEmail(email);
            Member member = memberService.getMemberById(user.getId());

            // 檢查名稱是否為郵箱格式，如果是則嘗試使用更友好的格式
            memberService.checkIsRegisteredViaEmail(user, member, email);

            MemberResponse memberData = MemberResponse.getMemberData(user, member);

            return ResponseEntity.ok(memberData);
        } catch (Exception e) {
            log.error("獲取會員資料失敗", e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "獲取會員資料失敗：" + e.getMessage()));
        }
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(@RequestBody UpdateProfile request) {
        try {
            Member existingMember = getMemberViaUserViaEmail();
            memberService.updateProfile(existingMember, request);
            return ResponseEntity.ok(Map.of("message", "會員資料更新成功"));
        } catch (Exception e) {
            log.error("更新會員資料失敗", e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "更新失敗：" + e.getMessage()));
        }
    }

    @PostMapping("/upload-photo")
    public ResponseEntity<?> uploadProfilePhoto(@RequestParam("photo") MultipartFile photo) {
        try {
            Member member = getMemberViaUserViaEmail();
            memberService.uploadPhoto(member, photo);
            return ResponseEntity.ok(Map.of("message", "頭像更新成功"));
        } catch (Exception e) {
            log.error("更新頭像失敗", e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "更新失敗：" + e.getMessage()));
        }
    }

    @GetMapping("/address")
    public ResponseEntity<?> getAddress() {
        try {
            Member member = getMemberViaUserViaEmail();
            return ResponseEntity.ok(Map.of("address", member.getAddress()));
        } catch (Exception e) {
            log.error("獲取地址失敗", e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "獲取地址失敗：" + e.getMessage()));
        }
    }

    @GetMapping("/profile-photo")
    public ResponseEntity<byte[]> getProfilePhoto() {
        try {
            Member member = getMemberViaUserViaEmail();

            if (member.getProfilePhoto() != null) return ResponseEntity.notFound().build();

            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(member.getProfilePhoto());
        } catch (Exception e) {
            log.error("獲取頭像失敗", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
