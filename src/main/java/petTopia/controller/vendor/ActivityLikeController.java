package petTopia.controller.vendor;

import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import petTopia.dto.vendor.ActivityLikeDto;
import petTopia.service.vendor.ActivityLikeService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/activity")
public class ActivityLikeController {
    @Autowired
    private final ActivityLikeService activityLikeService;

    @GetMapping("/{activityId}/like")
    public ResponseEntity<List<ActivityLikeDto>> getActivityLikeList(@PathVariable Integer activityId) {
        List<ActivityLikeDto> likeList = activityLikeService.finLikesByActivityId(activityId);
        return ResponseEntity.ok(likeList);
    }

    @GetMapping("/{activityId}/member/{memberId}/like/status")
    public ResponseEntity<?> getLikeStatus(@PathVariable Integer activityId, @PathVariable Integer memberId) {
        boolean isLiked = activityLikeService.getActivityLikeStatus(memberId, activityId);
        return ResponseEntity.ok(Map.of("action", isLiked));
    }

    @PostMapping("/{activityId}/like/toggle")
    public ResponseEntity<?> toggleLike(@PathVariable Integer activityId, @RequestBody Map<String, Integer> data) {
        Integer memberId = data.get("memberId");
        boolean isLiked = activityLikeService.toggleActivityLike(memberId, activityId);
        return ResponseEntity.ok(Map.of("action", isLiked));
    }
}
