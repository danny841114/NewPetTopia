package petTopia.controller.vendor_admin;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import petTopia.model.vendor.ActivityPeopleNumber;
import petTopia.model.vendor.ActivityRegistration;
import petTopia.model.vendor.Notification;
import petTopia.repository.vendor.NotificationRepository;
import petTopia.repository.vendor_admin.ActivityPeopleNumberRepository;
import petTopia.repository.vendor_admin.ActivityRegistrationRepository;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/vendor_admin")
public class VendorActivityRegistrationController {
    private final ActivityRegistrationRepository activityRegistrationRepository;
    private final NotificationRepository notificationRepository;
    private final ActivityPeopleNumberRepository activityPeopleNumberRepository;

    @GetMapping("/activity/registration")
    public ResponseEntity<?> getRegistrationByActivityId(@RequestParam Integer activityId) {
        List<ActivityRegistration> registration = activityRegistrationRepository.findByVendorActivityId(activityId);

        if (registration.isEmpty()) return ResponseEntity.ok(Collections.emptyList());

        return ResponseEntity.ok(registration);
    }

    // TODO: Move to service layer
    @PutMapping("/activity/confirmAll")
    public ResponseEntity<?> confirmAllRegistrations(@RequestParam Integer activityId) {
        List<ActivityRegistration> registrations = activityRegistrationRepository.findByVendorActivityId(activityId);

        if (registrations.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No registrations found.");

        // 批量更新状态为 "confirmed"
        registrations.forEach(registration -> {
            registration.setStatus("confirmed");
            activityRegistrationRepository.save(registration);
        });

        return ResponseEntity.ok("All registrations have been confirmed and notifications sent.");
    }

    // TODO: Move to service layer
    @PutMapping("/registration/update/{id}")
    public ResponseEntity<?> updateRegistrationStatus(@PathVariable Integer id, @RequestBody String status) {
        Optional<ActivityRegistration> optional = activityRegistrationRepository.findById(id);

        if (optional.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No registrations found.");

        ActivityRegistration registration = optional.get();

        registration.setStatus(status);
        ActivityRegistration savedRegistration = activityRegistrationRepository.save(registration);

        return ResponseEntity.ok(savedRegistration);
    }

    // TODO: Move to service layer
    @PutMapping("/registration/confirmById/{registrationId}")
    public ResponseEntity<?> confirmRegistrationById(@PathVariable Integer registrationId) {
        ActivityRegistration registration = activityRegistrationRepository.findById(registrationId).orElse(null);

        if (registration == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Registration not found.");

        Integer activityId = registration.getVendorActivity().getId();
        Optional<ActivityPeopleNumber> current = activityPeopleNumberRepository.findByVendorActivityId(activityId);

        if (current.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("ActivityPeopleNumber not found.");

        registration.setStatus("confirmed");
        activityRegistrationRepository.save(registration);

        ActivityPeopleNumber peopleNumber = current.get();
        peopleNumber.setCurrentParticipants(peopleNumber.getCurrentParticipants() + 1);
        activityPeopleNumberRepository.save(peopleNumber);

        return ResponseEntity.ok("Registration confirmed and notification sent.");
    }

    // TODO: Move to service layer
    @PutMapping("/registration/cancelById/{registrationId}")
    public ResponseEntity<?> cancelRegistrationById(@PathVariable Integer registrationId) {
        ActivityRegistration registration = activityRegistrationRepository.findById(registrationId).orElse(null);

        if (registration == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Registration not found.");

        registration.setStatus("canceled");
        activityRegistrationRepository.save(registration);

        return ResponseEntity.ok("Registration canceled and notification sent.");
    }


    // TODO: Move to service layer
    @DeleteMapping("/registration/deleteById/{registrationId}")
    public ResponseEntity<?> deleteRegistrationById(@PathVariable Integer registrationId) {
        ActivityRegistration registration = activityRegistrationRepository.findById(registrationId).orElse(null);

        if (registration == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Registration not found.");

        Integer activityId = registration.getVendorActivity().getId();
        Optional<ActivityPeopleNumber> current = activityPeopleNumberRepository.findByVendorActivityId(activityId);

        if (current.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("ActivityPeopleNumber not found.");

        if ("confirmed".equals(registration.getStatus())) {
            ActivityPeopleNumber peopleNumber = current.get();
            peopleNumber.setCurrentParticipants(peopleNumber.getCurrentParticipants() - 1);
            activityPeopleNumberRepository.save(peopleNumber);
        }

        Integer memberId = registration.getMember().getId();
        notificationRepository.findByMemberId(memberId).forEach(notificationRepository::delete);

        activityRegistrationRepository.delete(registration);

        return ResponseEntity.ok("Registration canceled and notifications sent.");
    }

    // TODO: Move to service layer
    @PostMapping("/registration/notification/{memberId}/{activityId}")
    public ResponseEntity<?> sendNotification(@PathVariable Integer memberId,
                                              @PathVariable Integer activityId,
                                              @RequestParam String title,
                                              @RequestParam String content) {
        Optional<ActivityRegistration> registrationOpt = activityRegistrationRepository.findByMemberIdAndVendorActivityId(memberId, activityId);

        if (registrationOpt.isPresent()) {
            ActivityRegistration registration = registrationOpt.get();

            Notification notification = new Notification();
            notification.setMember(registration.getMember());
            notification.setVendor(registration.getVendorActivity().getVendor());
            notification.setVendorActivity(registration.getVendorActivity());
            notification.setNotificationTitle(title);
            notification.setNotificationContent(content);

            notificationRepository.save(notification);

            return ResponseEntity.ok("Notification sent.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Activity registration not found.");
        }
    }
}
