package petTopia.service.vendor_admin;

import java.util.*;
import java.util.stream.Collectors;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import petTopia.dto.vendor_admin.CertificationDTO;
import petTopia.dto.vendor_admin.VendorCertificationDto;
import petTopia.model.vendor.CertificationTag;
import petTopia.model.vendor.Vendor;
import petTopia.model.vendor.VendorCertification;
import petTopia.model.vendor.VendorCertificationTag;
import petTopia.repository.vendor.VendorRepository;
import petTopia.repository.vendor.VendorReviewRepository;
import petTopia.repository.vendor_admin.CertificationTagRepository;
import petTopia.repository.vendor_admin.VendorCertificationRepository;
import petTopia.repository.vendor_admin.VendorCertificationTagRepository;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class VendorCertificationService {
    private final VendorCertificationRepository vendorCertificationRepository;
    private final VendorCertificationTagRepository vendorCertificationTagRepository;
    private final CertificationTagRepository certificationTagRepository;
    private final VendorRepository vendorRepository;
    private final VendorReviewRepository vendorReviewRepository;

    @Transactional
    public void createVendorCertificationWithTag(Integer vendorId, Integer tagId) {
        // 1. 获取 Vendor 和 CertificationTag 实体
        Vendor vendor = vendorRepository.findById(vendorId)
                .orElseThrow(() -> new EntityNotFoundException("Vendor not found"));
        CertificationTag tag = certificationTagRepository.findById(tagId)
                .orElseThrow(() -> new EntityNotFoundException("Certification Tag not found"));

        // 2. 创建 VendorCertification 实体并保存
        VendorCertification certification = new VendorCertification();
        certification.setVendor(vendor);
        certification.setCertificationStatus("申請中");
        certification.setRequestDate(new Date());
        vendorCertificationRepository.save(certification);

        // 3. 创建 VendorCertificationTag 实体并保存
        VendorCertificationTag certificationTag = new VendorCertificationTag();
        certificationTag.setCertification(certification);
        certificationTag.setTag(tag);
        certificationTag.setMeetsStandard(false);
        certificationTag.setVendor(vendor);

        Optional<CertificationTag> tagOpt = certificationTagRepository.findById(tagId);
        if (tagOpt.isPresent()) {
            boolean meetsStandard = checkMeetsStandard(vendorId, tagOpt.get());
            certificationTag.setMeetsStandard(meetsStandard);
        } else {
            throw new EntityNotFoundException("Certification tag not found");
        }

        vendorCertificationTagRepository.save(certificationTag);
    }

    public List<CertificationTag> getAllCertificationTypes() {
        return certificationTagRepository.findAll();
    }

    private boolean checkMeetsStandard(Integer vendorId, CertificationTag tag) {
        String keywordsStr = tag.getKeywords();
        if (keywordsStr == null || keywordsStr.isBlank()) {
            return false;
        }

        List<String> keywords = Arrays.asList(keywordsStr.split(","));
        while (keywords.size() < 5) {
            keywords.add(""); // 避免 SQL 查詢錯誤
        }

        int matchingReviewCount = vendorReviewRepository.countMatchingReviews(
                vendorId,
                keywords.get(0),
                keywords.get(1),
                keywords.get(2),
                keywords.get(3),
                keywords.get(4)
        );

        return matchingReviewCount >= 3; // 例如至少 10 則符合評論才算合格
    }

    public List<CertificationDTO> getAllCertificationsWithTags() {
        List<CertificationDTO> certificationsWithTags = new ArrayList<>();

        List<VendorCertification> certifications = vendorCertificationRepository.findAll();

        for (VendorCertification certification : certifications) {
            List<VendorCertificationTag> certificationTags = vendorCertificationTagRepository.findByCertification(certification);

            List<CertificationDTO.CertificationTagDTO> tagDTOList = new ArrayList<>();
            for (VendorCertificationTag certificationTag : certificationTags) {
                CertificationDTO.CertificationTagDTO tagDTO = new CertificationDTO.CertificationTagDTO();

                tagDTO.setTagName(certificationTag.getTag().getTagName());
                tagDTO.setMeetsStandard(certificationTag.isMeetsStandard());

                tagDTOList.add(tagDTO);
            }

            CertificationDTO certificationDTO = formCertificationDto(certification, tagDTOList);

            certificationsWithTags.add(certificationDTO);
        }

        return certificationsWithTags;
    }

    @Transactional
    public VendorCertification updateCertificationStatus(Integer certificationId, String status, String reason) {
        VendorCertification certification = vendorCertificationRepository.findById(certificationId)
                .orElseThrow(() -> new EntityNotFoundException("Certification not found with id: " + certificationId));

        certification.setCertificationStatus(status);
        certification.setReason(reason);
        certification.setApprovedDate(new Date());

        return vendorCertificationRepository.save(certification);
    }

    @Transactional
    public void cancelCertificationById(Integer certificationId) {
        if (vendorCertificationRepository.existsById(certificationId)) {
            vendorCertificationRepository.deleteById(certificationId);
        }
    }

    public List<VendorCertificationDto> getCertificationByVendorId(Integer vendorId) {
        List<VendorCertificationTag> tags = vendorCertificationTagRepository.findByVendorId(vendorId);

        List<Integer> certificationIds = tags.stream()
                .map(tag -> tag.getCertification().getId())
                .distinct()
                .collect(Collectors.toList());

        List<VendorCertification> vendorCertifications = vendorCertificationRepository.findByIdIn(certificationIds);

        return vendorCertifications.stream()
                .map(certification -> getVendorCertificationDto(certification, tags))
                .collect(Collectors.toList());
    }

    private VendorCertificationDto getVendorCertificationDto(VendorCertification certification,
                                                             List<VendorCertificationTag> tags) {
        VendorCertificationDto.TagDto tagDto = tags.stream()
                .filter(tag -> tag.getCertification().getId().equals(certification.getId()))
                .findFirst()
                .map(this::fromEntity)
                .orElse(null);

        return VendorCertificationDto.builder()
                .id(certification.getId())
                .vendor(certification.getVendor())
                .certificationStatus(certification.getCertificationStatus())
                .reason(certification.getReason())
                .requestDate(certification.getRequestDate())
                .approvedDate(certification.getApprovedDate())
                .tag(tagDto)
                .build();
    }

    private VendorCertificationDto.TagDto fromEntity(VendorCertificationTag tag) {
        return VendorCertificationDto.TagDto.builder()
                .id(tag.getId())
                .tag(tag.getTag())
                .meetsStandard(tag.isMeetsStandard())
                .build();
    }

    private CertificationDTO formCertificationDto(VendorCertification certification,
                                                  List<CertificationDTO.CertificationTagDTO> tagDtoList) {
        return CertificationDTO.builder()
                .vendor(certification.getVendor())
                .certificationId(certification.getId())
                .certificationStatus(certification.getCertificationStatus())
                .reason(certification.getReason())
                .requestDate(certification.getRequestDate())
                .approvedDate(certification.getApprovedDate())
                .certificationTags(tagDtoList)
                .build();
    }
}
