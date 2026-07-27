package petTopia.controller.vendor;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import petTopia.dto.vendor.ActivityReviewDto;
import petTopia.model.vendor.ActivityLike;
import petTopia.model.vendor.ActivityRegistration;
import petTopia.model.vendor.VendorActivityReview;
import petTopia.service.vendor.ActivityLikeService;
import petTopia.service.vendor.ActivityRegistrationUserService;
import petTopia.service.vendor.VendorActivityReviewService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/activity")
public class MemberActivityController {
	private final ActivityLikeService activityLikeService;
	private final VendorActivityReviewService vendorActivityReviewService;
	private final ActivityRegistrationUserService activityRegistrationUserService;

	@GetMapping("/member/{memberId}/like")
	public ResponseEntity<List<ActivityLike>> getLikeList(@PathVariable Integer memberId) {
		List<ActivityLike> likeList = activityLikeService.findLikeListByMemberId(memberId);
		return ResponseEntity.ok(likeList);
	}

	@GetMapping("/member/{memberId}/review")
	public ResponseEntity<List<ActivityReviewDto>> getReviews(@PathVariable Integer memberId) {
		List<ActivityReviewDto> reviews = vendorActivityReviewService.findReviewsByMemberId(memberId);
		return ResponseEntity.ok(reviews);
	}

	@GetMapping("/member/{memberId}/registration")
	public ResponseEntity<List<ActivityRegistration>> getRegistrationList(@PathVariable Integer memberId) {
		List<ActivityRegistration> registrationList = activityRegistrationUserService.findRegistrationListByMemberId(memberId);
		return ResponseEntity.ok(registrationList);
	}
	
	@DeleteMapping("/like/{likeId}/delete")
	public ResponseEntity<?> deleteLike(@PathVariable Integer likeId){
		Boolean result = activityLikeService.deleteByLikeId(likeId);
		return ResponseEntity.ok(result);
	}
	
	@DeleteMapping("/registration/{registrationId}/delete")
	public ResponseEntity<?> deleteRegistration(@PathVariable Integer registrationId){
		Boolean result = activityRegistrationUserService.deleteByRegistrationId(registrationId);
		return ResponseEntity.ok(result);
	}
}
