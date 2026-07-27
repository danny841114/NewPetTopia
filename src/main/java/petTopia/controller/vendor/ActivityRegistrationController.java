package petTopia.controller.vendor;

import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import petTopia.model.vendor.ActivityPeopleNumber;
import petTopia.model.vendor.ActivityRegistration;
import petTopia.service.vendor.ActivityRegistrationUserService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/activity")
public class ActivityRegistrationController {
	private final ActivityRegistrationUserService activityRegistrationUserService;
	
	@GetMapping("/{activityId}/registration/people/number")
	public ResponseEntity<ActivityPeopleNumber> getPeopleNumber(@PathVariable Integer activityId){
		ActivityPeopleNumber peopleNumber = activityRegistrationUserService.getPeopleNumber(activityId);
		return ResponseEntity.ok(peopleNumber);
	}

	@GetMapping("/{activityId}/registration/pending")
	public ResponseEntity<List<ActivityRegistration>> getPendingMembers(@PathVariable Integer activityId) {
		List<ActivityRegistration> pendingList = activityRegistrationUserService.getActivityPendingList(activityId);
		return ResponseEntity.ok(pendingList);
	}

	@GetMapping("/{activityId}/registration/confirmed")
	public ResponseEntity<List<ActivityRegistration>> getConfirmedMembers(@PathVariable Integer activityId) {
		List<ActivityRegistration> confirmedList = activityRegistrationUserService.getActivityConfirmedList(activityId);
		return ResponseEntity.ok(confirmedList);
	}

	@GetMapping("/{activityId}/member/{memberId}/register/status")
	public ResponseEntity<?> getRegistrationCondition(@PathVariable Integer activityId, @PathVariable Integer memberId) {
		Boolean isRegistered = activityRegistrationUserService.getRegistrationStatus(memberId, activityId);
		return ResponseEntity.ok(Map.of("action", isRegistered));
	}
	
	@PostMapping("/{activityId}/register")
	public ResponseEntity<?> registerActivity(@PathVariable Integer activityId, @RequestBody Map<String, Integer> data) {
		Integer memberId = data.get("memberId");
		Boolean isRegistered = activityRegistrationUserService.toggleRegistration(memberId, activityId);
		return ResponseEntity.ok(Map.of("action", isRegistered));
	}
	
	@GetMapping("/{activityId}/registration/status")
	public ResponseEntity<Boolean> isActivityAvailable(@PathVariable Integer activityId) {
		Boolean isAvailable = activityRegistrationUserService.isActivityAvailable(activityId);
		return ResponseEntity.ok(isAvailable);
	}
}
