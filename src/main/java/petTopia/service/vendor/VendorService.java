package petTopia.service.vendor;

import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import petTopia.dto.vendor.ActivityDto;
import petTopia.dto.vendor.VendorDto;
import petTopia.model.vendor.Vendor;
import petTopia.model.vendor.VendorActivity;
import petTopia.repository.vendor.VendorRepository;
import petTopia.util.ImageConverter;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class VendorService {
    private final VendorRepository vendorRepository;
    private final VendorReviewService vendorReviewService;

    /* 所有店家清單 */
    public List<Vendor> findAllVendor() {
        List<Vendor> vendorList = vendorRepository.findAll();

        // TODO: change base64 to byte[]
        for (Vendor v : vendorList) {
            byte[] logoImg = v.getLogoImg();
            if (logoImg != null) {
                String mimeType = ImageConverter.getMimeType(logoImg);
                String base64 = "data:%s;base64,".formatted(mimeType) + Base64.getEncoder().encodeToString(logoImg);
                v.setLogoImgBase64(base64);
            }
        }

        return vendorList;
    }

    /* 單一店家 */
    public Vendor findVendorById(Integer vendorId) {
        Optional<Vendor> optional = vendorRepository.findById(vendorId);

        if (optional.isPresent()) {
            Vendor vendor = optional.get();

            // TODO: change base64 to byte[]
            byte[] logoImg = vendor.getLogoImg();
            String mimeType = ImageConverter.getMimeType(logoImg);

            if (logoImg == null) {
                vendor.setLogoImgBase64(null);
            } else {
                String base64 = "data:%s;base64,".formatted(mimeType) + Base64.getEncoder().encodeToString(logoImg);
                vendor.setLogoImgBase64(base64);
            }

            return vendor;
        }

        return null;
    }

    // TODO: Change to JPQL method
    public byte[] findVendorLogoImgById(Integer vendorId) {
        return vendorRepository.findById(vendorId)
                .map(Vendor::getLogoImg)
                .orElseThrow(() -> new EntityNotFoundException("Vendor logo image not found"));
    }

    /* 排除特定店家之清單 */
    // TODO: JPQL
    public List<Vendor> findAllVendorExceptOne(Integer vendorId) {
        List<Vendor> vendorList = vendorRepository.findAll();

        Vendor vendorToRemove = vendorRepository.findById(vendorId)
                .orElseThrow(() -> new EntityNotFoundException("Vendor not found"));

        vendorList.removeIf(v -> v.getId().equals(vendorToRemove.getId()));

        // TODO: change base64 to byte[]
        for (Vendor v : vendorList) {
            byte[] logoImg = v.getLogoImg();
            if (logoImg != null) {
                String mimeType = ImageConverter.getMimeType(logoImg);
                String base64 = "data:%s;base64,".formatted(mimeType) + Base64.getEncoder().encodeToString(logoImg);
                v.setLogoImgBase64(base64);
            }
        }

        return vendorList;
    }

    /* 藉類別來找店家 */
    public List<Vendor> findVendorByCategoryId(Integer categoryId) {
        List<Vendor> vendorList = vendorRepository.findByVendorCategoryId(categoryId);

        // TODO: change base64 to byte[]
        for (Vendor v : vendorList) {
            byte[] logoImg = v.getLogoImg();
            if (logoImg != null) {
                String mimeType = ImageConverter.getMimeType(logoImg);
                String base64 = "data:%s;base64,".formatted(mimeType) + Base64.getEncoder().encodeToString(logoImg);
                v.setLogoImgBase64(base64);
            }
        }

        return vendorList;
    }

    /* 藉類別來找店家 */
    // TODO: JPQL
    public List<Vendor> findVendorByCategoryIdExceptOne(Integer categoryId, Integer vendorId) {
        List<Vendor> vendorList = vendorRepository.findByVendorCategoryId(categoryId);

        Vendor vendorToRemove = vendorRepository.findById(vendorId)
                .orElseThrow(() -> new EntityNotFoundException("Vendor not found"));

        vendorList.removeIf(v -> v.getId().equals(vendorToRemove.getId()));

        // TODO: change base64 to byte[]
        for (Vendor v : vendorList) {
            byte[] logoImg = v.getLogoImg();
            if (logoImg != null) {
                String mimeType = ImageConverter.getMimeType(logoImg);
                String base64 = "data:%s;base64,".formatted(mimeType) + Base64.getEncoder().encodeToString(logoImg);
                v.setLogoImgBase64(base64);
            }
        }

        return vendorList;
    }

    /* 模糊搜尋店家 */
    public List<Vendor> findVendorByNameOrDescription(String keyword) {
        List<Vendor> vendors = vendorRepository.findByNameContainingOrDescriptionContaining(keyword, keyword);

        // TODO: change base64 to byte[]
        for (Vendor vendor : vendors) {
            byte[] imageByte = vendor.getLogoImg();
            if (imageByte != null) {
                String mimeType = ImageConverter.getMimeType(imageByte);
                String base64 = "data:%s;base64,".formatted(mimeType) + Base64.getEncoder().encodeToString(imageByte);
                vendor.setLogoImgBase64(base64);
            }
        }

        return vendors;
    }

    /* 取得所有 Vendor DTO */
    public List<VendorDto> getAllVendorDto() {
        return vendorRepository.findAll()
                .stream()
                .map(this::convertVendorToDto)
                .collect(Collectors.toList());
    }

    private VendorDto convertVendorToDto(Vendor vendor) {
        VendorDto vendorDto = new VendorDto();
        vendorDto.setId(vendor.getId());
        vendorDto.setName(vendor.getName());
        vendorDto.setDescription(vendor.getDescription());

        /* 確保載入時有最新評分 */
        Vendor vendorForAvgRating = vendorReviewService.setAverageRating(vendor.getId());
        vendorDto.setTotalRating(vendorForAvgRating.getTotalRating());

        // TODO: change base64 to byte[]
        byte[] logoImg = vendor.getLogoImg();
        if (logoImg != null) {
            String mimeType = ImageConverter.getMimeType(logoImg);
            String base64 = "data:%s;base64,".formatted(mimeType) + Base64.getEncoder().encodeToString(logoImg);
            vendorDto.setLogoImgBase64(base64);
        }

        List<ActivityDto> activityDtoList = vendor.getActivities()
                .stream()
                .map(this::convertActivityToDto)
                .collect(Collectors.toList());

        vendorDto.setActivityDtoList(activityDtoList);

        return vendorDto;
    }

    private ActivityDto convertActivityToDto(VendorActivity activity) {
        return ActivityDto.builder()
                .activityId(activity.getId())
                .activityName(activity.getName())
                .activityDescription(activity.getDescription())
                .build();
    }
}