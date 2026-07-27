package petTopia.service.vendor;

import java.util.Base64;
import java.util.List;
import java.util.Optional;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import petTopia.model.user.Member;
import petTopia.model.vendor.ActivityPeopleNumber;
import petTopia.model.vendor.ActivityRegistration;
import petTopia.model.vendor.VendorActivity;
import petTopia.repository.user.MemberRepository;
import petTopia.repository.vendor.VendorActivityRepository;
import petTopia.repository.vendor_admin.ActivityPeopleNumberRepository;
import petTopia.repository.vendor_admin.ActivityRegistrationRepository;
import petTopia.util.ImageConverter;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class ActivityRegistrationUserService {
    private final MemberRepository memberRepository;
    private final VendorActivityRepository vendorActivityRepository;
    private final ActivityRegistrationRepository activityRegistrationRepository;
    private final ActivityPeopleNumberRepository activityPeopleNumberRepository;

    @Transactional
    public Boolean toggleRegistration(Integer memberId, Integer activityId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("Member not found"));

        VendorActivity activity = vendorActivityRepository.findById(activityId)
                .orElseThrow(() -> new EntityNotFoundException("Member not found"));

        Optional<ActivityRegistration> registrationOptional = activityRegistrationRepository.findByMemberAndVendorActivity(member, activity);

        Integer count = activityRegistrationRepository.countByVendorActivityId(activityId);

        ActivityPeopleNumber peopleNumber = activityPeopleNumberRepository.findByVendorActivityId(activityId)
                .orElseThrow(() -> new EntityNotFoundException("Activity people number not found"));

        if (registrationOptional.isEmpty()) {
            ActivityRegistration newRegistration = new ActivityRegistration();
            newRegistration.setMember(member);
            newRegistration.setVendorActivity(activity);
            activityRegistrationRepository.save(newRegistration);

            peopleNumber.setCurrentParticipants(count + 1);
            activityPeopleNumberRepository.save(peopleNumber);

            return true;
        } else {
            activityRegistrationRepository.delete(registrationOptional.get());

            peopleNumber.setCurrentParticipants(count - 1);
            activityPeopleNumberRepository.save(peopleNumber);

            return false;
        }
    }

    public Boolean getRegistrationStatus(Integer memberId, Integer activityId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("Member not found"));

        VendorActivity activity = vendorActivityRepository.findById(activityId)
                .orElseThrow(() -> new EntityNotFoundException("Activity not found"));

        Optional<ActivityRegistration> registrationOptional = activityRegistrationRepository.findByMemberAndVendorActivity(member, activity);

        return registrationOptional.isPresent();
    }

    // TODO: Should not return base64 string
    public List<ActivityRegistration> getActivityPendingList(Integer activityId) {
        String status = "pending";
        List<ActivityRegistration> pendingList = activityRegistrationRepository.findByVendorActivityIdAndStatus(activityId, status);

        for (ActivityRegistration registration : pendingList) {
            byte[] photoByte = registration.getMember().getProfilePhoto();
            if (photoByte != null) {
                String mimeType = ImageConverter.getMimeType(photoByte);
                String base64 = "data:%s;base64,".formatted(mimeType) + Base64.getEncoder().encodeToString(photoByte);
                registration.getMember().setProfilePhotoBase64(base64);
            }
        }

        return pendingList;
    }

    // TODO: Should not return base64 string
    public List<ActivityRegistration> getActivityConfirmedList(Integer activityId) {
        String status = "confirmed";
        List<ActivityRegistration> confirmedList = activityRegistrationRepository.findByVendorActivityIdAndStatus(activityId, status);

        for (ActivityRegistration registration : confirmedList) {
            byte[] photoByte = registration.getMember().getProfilePhoto();
            if (photoByte != null) {
                String mimeType = ImageConverter.getMimeType(photoByte);
                String base64 = "data:%s;base64,".formatted(mimeType) + Base64.getEncoder().encodeToString(photoByte);
                registration.getMember().setProfilePhotoBase64(base64);
            }
        }

        return confirmedList;
    }

    @Transactional
    public ActivityPeopleNumber getPeopleNumber(Integer activityId) {
        ActivityPeopleNumber peopleNumber = activityPeopleNumberRepository.findByVendorActivityId(activityId)
                .orElseThrow(() -> new EntityNotFoundException("Activity people number not found"));

        Integer number = activityRegistrationRepository.countByVendorActivityId(activityId);
        peopleNumber.setCurrentParticipants(number);
        activityPeopleNumberRepository.save(peopleNumber);

        return peopleNumber;
    }

    public Boolean isActivityAvailable(Integer activityId) {
        ActivityPeopleNumber peopleNumber = activityPeopleNumberRepository.findById(activityId)
                .orElseThrow(() -> new EntityNotFoundException("Activity people number not found"));

        Integer current = peopleNumber.getCurrentParticipants();
        Integer max = peopleNumber.getMaxParticipants();

        if (current == null || max == null) {
            return false;
        }

        return current <= max;
    }

    public List<ActivityRegistration> findRegistrationListByMemberId(Integer memberId) {
        return activityRegistrationRepository.findAllByMemberId(memberId);
    }

    @Transactional
    public Boolean deleteByRegistrationId(Integer likeId) {
        if (activityRegistrationRepository.existsById(likeId)) {
            activityRegistrationRepository.deleteById(likeId);
            return true;
        }

        return false;
    }
}
