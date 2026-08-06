package petTopia.controller.vendor_admin;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import petTopia.dto.vendor_admin.request.NotificationRequest;
import petTopia.model.vendor.ActivityRegistration;
import petTopia.service.vendor_admin.VendorActivityRegistrationService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/vendor_admin")
public class VendorActivityRegistrationController {
    private final VendorActivityRegistrationService vendorActivityRegistrationService;

    @GetMapping("/activity/registration")
    public ResponseEntity<List<ActivityRegistration>> getRegistrations(@RequestParam Integer activityId) {
        List<ActivityRegistration> registrations = vendorActivityRegistrationService.getRegistrationsByActivityId(activityId);
        return ResponseEntity.ok(registrations);
    }

    // TODO: Response body should be modified
    @PutMapping("/activity/confirmAll")
    public ResponseEntity<String> confirmAllRegistrations(@RequestParam Integer activityId) {
        vendorActivityRegistrationService.confirmAllRegistrations(activityId);
        return ResponseEntity.ok("All registrations have been confirmed and notifications sent.");
    }

    @PutMapping("/registration/update/{id}")
    public ResponseEntity<ActivityRegistration> updateRegistrationStatus(@PathVariable Integer id, @RequestBody String status) {
        ActivityRegistration registration = vendorActivityRegistrationService.updateRegistrationStatus(id, status);
        return ResponseEntity.ok(registration);
    }

    // TODO: Response body should be modified
    @PutMapping("/registration/confirmById/{registrationId}")
    public ResponseEntity<String> confirmRegistration(@PathVariable Integer registrationId) {
        vendorActivityRegistrationService.confirmRegistrationById(registrationId);
        return ResponseEntity.ok("Registration confirmed and notification sent.");
    }

    // TODO: Response body should be modified
    @PutMapping("/registration/cancelById/{registrationId}")
    public ResponseEntity<?> cancelRegistration(@PathVariable Integer registrationId) {
        vendorActivityRegistrationService.cancelRegistrationById(registrationId);
        return ResponseEntity.ok("Registration canceled and notification sent.");
    }


    // TODO: Response body should be modified
    @DeleteMapping("/registration/deleteById/{registrationId}")
    public ResponseEntity<String> deleteRegistrationById(@PathVariable Integer registrationId) {
        vendorActivityRegistrationService.deleteRegistrationById(registrationId);
        return ResponseEntity.ok("Registration canceled and notifications sent.");
    }

    // TODO: 2026-08-06 Modify API spec, front-end not fixed
    //  REQUEST PARAM TO BODY
    //  Change activityId from PathVariable to RequestBody
    @PostMapping("/registration/notification/{memberId}")
    public ResponseEntity<?> sendNotification(@PathVariable Integer memberId,
                                              @RequestBody NotificationRequest request) {
        vendorActivityRegistrationService.sendNotification(memberId, request);
        return ResponseEntity.ok("Notification sent.");
    }
}
