package petTopia.controller.vendor;

import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import petTopia.dto.vendor.VendorLikeDto;
import petTopia.service.vendor.VendorLikeService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/vendor")
public class VendorLikeController {
    private final VendorLikeService vendorLikeService;

    @GetMapping("/{vendorId}/like")
    public ResponseEntity<List<VendorLikeDto>> getVendorLikeList(@PathVariable Integer vendorId) {
        List<VendorLikeDto> likeList = vendorLikeService.findMemberListByVendorId(vendorId);
        return ResponseEntity.ok(likeList);
    }

    @GetMapping("/{vendorId}/member/{memberId}/like/status")
    public ResponseEntity<?> getLikeStatus(@PathVariable Integer vendorId, @PathVariable Integer memberId) {
        boolean isLiked = vendorLikeService.getActivityLikeStatus(memberId, vendorId);
        return ResponseEntity.ok(Map.of("action", isLiked));
    }

    @PostMapping("/{vendorId}/like/toggle")
    public ResponseEntity<?> toggleLike(@PathVariable Integer vendorId, @RequestBody Map<String, Integer> data) {
        Integer memberId = data.get("memberId");
        boolean isLiked = vendorLikeService.toggleVendorLike(memberId, vendorId);
        return ResponseEntity.ok(Map.of("action", isLiked));
    }
}
