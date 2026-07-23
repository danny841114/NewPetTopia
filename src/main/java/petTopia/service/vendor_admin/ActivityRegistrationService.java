package petTopia.service.vendor_admin;

import java.util.ArrayList;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import petTopia.dto.vendor_admin.MemberDTO;
import petTopia.model.vendor.ActivityRegistration;
import petTopia.repository.vendor_admin.ActivityRegistrationRepository;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class ActivityRegistrationService {
    private final ActivityRegistrationRepository activityRegistrationRepository;

    public List<MemberDTO> getRegisteredMembersByActivity(Integer vendorActivityId) {
        List<ActivityRegistration> registrations = activityRegistrationRepository.findByVendorActivityId(vendorActivityId);

        List<MemberDTO> memberDTOList = new ArrayList<>();

        for (ActivityRegistration reg : registrations) {
            MemberDTO dto = MemberDTO.fromEntity(reg.getMember());
            memberDTOList.add(dto);
        }

        return memberDTOList;
    }
}
