package petTopia.service.vendor_admin;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import petTopia.dto.vendor_admin.request.NotificationRequest;
import petTopia.model.vendor.ActivityPeopleNumber;
import petTopia.model.vendor.ActivityRegistration;
import petTopia.model.vendor.Notification;
import petTopia.repository.vendor.NotificationRepository;
import petTopia.repository.vendor_admin.ActivityPeopleNumberRepository;
import petTopia.repository.vendor_admin.ActivityRegistrationRepository;

import java.util.List;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class VendorActivityRegistrationService {
    private final ActivityRegistrationRepository activityRegistrationRepository;
    private final NotificationRepository notificationRepository;
    private final ActivityPeopleNumberRepository activityPeopleNumberRepository;

    public List<ActivityRegistration> getRegistrationsByActivityId(Integer activityId) {
        return activityRegistrationRepository.findByVendorActivityId(activityId);
    }

    @Transactional
    public void confirmAllRegistrations(Integer activityId) {
        List<ActivityRegistration> registrations = activityRegistrationRepository.findByVendorActivityId(activityId);

        if (registrations.isEmpty()) throw new EntityNotFoundException("Registrations not found");

        registrations.forEach(registration -> registration.setStatus("confirmed"));
        activityRegistrationRepository.saveAll(registrations);
    }

    @Transactional
    public ActivityRegistration updateRegistrationStatus(Integer registrationId, String status) {
        ActivityRegistration registration = getActivityRegistrationById(registrationId);

        registration.setStatus(status);

        return activityRegistrationRepository.save(registration);
    }

    @Transactional
    public void confirmRegistrationById(@PathVariable Integer registrationId) {
        ActivityRegistration registration = getActivityRegistrationById(registrationId);

        Integer activityId = registration.getVendorActivity().getId();

        ActivityPeopleNumber peopleNumber = getActivityPeopleNumberById(activityId);

        registration.setStatus("confirmed");
        activityRegistrationRepository.save(registration);

        peopleNumber.setCurrentParticipants(peopleNumber.getCurrentParticipants() + 1);
        activityPeopleNumberRepository.save(peopleNumber);
    }

    @Transactional
    public void cancelRegistrationById(Integer registrationId) {
        ActivityRegistration registration = getActivityRegistrationById(registrationId);

        registration.setStatus("canceled");

        activityRegistrationRepository.save(registration);
    }

    @Transactional
    public void deleteRegistrationById(Integer registrationId) {
        ActivityRegistration registration = getActivityRegistrationById(registrationId);

        Integer activityId = registration.getVendorActivity().getId();

        ActivityPeopleNumber peopleNumber = getActivityPeopleNumberById(activityId);

        if ("confirmed".equals(registration.getStatus())) {
            peopleNumber.setCurrentParticipants(peopleNumber.getCurrentParticipants() - 1);
            activityPeopleNumberRepository.save(peopleNumber);
        }

        Integer memberId = registration.getMember().getId();
        notificationRepository.findByMemberId(memberId).forEach(notificationRepository::delete);

        activityRegistrationRepository.delete(registration);
    }

    @Transactional
    public void sendNotification(Integer memberId, NotificationRequest request) {
        ActivityRegistration registration = activityRegistrationRepository.findByMemberIdAndVendorActivityId(memberId, request.getActivityId())
                .orElseThrow(() -> new EntityNotFoundException("Activity registration not found"));

        Notification notification = new Notification();

        notification.setMember(registration.getMember());
        notification.setVendor(registration.getVendorActivity().getVendor());
        notification.setVendorActivity(registration.getVendorActivity());
        notification.setNotificationTitle(request.getTitle());
        notification.setNotificationContent(request.getContent());

        notificationRepository.save(notification);
    }

    private ActivityRegistration getActivityRegistrationById(Integer registrationId) {
        return activityRegistrationRepository.findById(registrationId)
                .orElseThrow(() -> new EntityNotFoundException("Activity registration not found"));
    }

    private ActivityPeopleNumber getActivityPeopleNumberById(Integer activityId) {
        return activityPeopleNumberRepository.findByVendorActivityId(activityId)
                .orElseThrow(() -> new EntityNotFoundException("Activity people number not found"));
    }
}
