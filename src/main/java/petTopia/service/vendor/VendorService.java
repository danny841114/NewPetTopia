package petTopia.service.vendor;

import java.util.List;
import java.util.stream.Collectors;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import petTopia.dto.vendor.ActivityDetail;
import petTopia.dto.vendor.VendorDto;
import petTopia.model.vendor.Vendor;
import petTopia.repository.vendor.VendorRepository;

import static petTopia.constant.ImageUrl.LOGO_IMG_URL_PREFIX;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class VendorService {
    private final VendorRepository vendorRepository;
    private final VendorReviewService vendorReviewService;

    public List<VendorDto> findAllVendor() {
        return vendorRepository.findAll()
                .stream()
                .map(VendorDto::fromEntity)
                .toList();
    }

    public VendorDto findVendorById(Integer vendorId) {
        return vendorRepository.findById(vendorId)
                .map(VendorDto::fromEntity)
                .orElseThrow(() -> new EntityNotFoundException("Vendor not found"));
    }

    // TODO: Change to JPQL method
    public byte[] findVendorLogoImgById(Integer vendorId) {
        return vendorRepository.findById(vendorId)
                .map(Vendor::getLogoImg)
                .orElse(null);
    }

    // TODO: JPQL
    public List<VendorDto> findAllVendorExceptOne(Integer vendorId) {
        List<Vendor> vendorList = vendorRepository.findAll();

        Vendor vendorToRemove = vendorRepository.findById(vendorId)
                .orElseThrow(() -> new EntityNotFoundException("Vendor not found"));

        vendorList.removeIf(v -> v.getId().equals(vendorToRemove.getId()));

        return vendorList.stream()
                .map(VendorDto::fromEntity)
                .toList();
    }

    public List<VendorDto> findVendorByCategoryId(Integer categoryId) {
        return vendorRepository.findByVendorCategoryId(categoryId)
                .stream()
                .map(VendorDto::fromEntity)
                .toList();
    }

    // TODO: JPQL
    public List<VendorDto> findVendorByCategoryIdExceptOne(Integer categoryId, Integer vendorId) {
        List<Vendor> vendorList = vendorRepository.findByVendorCategoryId(categoryId);

        Vendor vendorToRemove = vendorRepository.findById(vendorId)
                .orElseThrow(() -> new EntityNotFoundException("Vendor not found"));

        vendorList.removeIf(v -> v.getId().equals(vendorToRemove.getId()));

        return vendorList.stream()
                .map(VendorDto::fromEntity)
                .toList();
    }

    public List<VendorDto> findVendorByNameOrDescription(String keyword) {
        return vendorRepository.findByNameContainingOrDescriptionContaining(keyword, keyword)
                .stream()
                .map(VendorDto::fromEntity)
                .toList();
    }

    public List<VendorDto> getAllVendorDto() {
        return vendorRepository.findAll()
                .stream()
                .map(this::convertVendorToDto)
                .collect(Collectors.toList());
    }

    private VendorDto convertVendorToDto(Vendor vendor) {
        Float currentAvgRating = vendorReviewService.setAverageRating(vendor.getId()).getTotalRating();

        String logoImgUrl = LOGO_IMG_URL_PREFIX.replace("{vendorId}", String.valueOf(vendor.getId()));

        List<ActivityDetail> activityDtoList = vendor.getActivities()
                .stream()
                .map(ActivityDetail::fromEntity)
                .collect(Collectors.toList());

        return VendorDto.builder()
                .id(vendor.getId())
                .name(vendor.getName())
                .description(vendor.getDescription())
                .totalRating(currentAvgRating)
                .logoImgUrl(logoImgUrl)
                .activityDtoList(activityDtoList)
                .build();
    }
}