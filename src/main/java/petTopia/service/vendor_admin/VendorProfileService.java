package petTopia.service.vendor_admin;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import petTopia.dto.vendor_admin.request.UpdateVendorRequest;
import petTopia.dto.vendor_admin.response.VendorProfile;
import petTopia.model.vendor.Vendor;
import petTopia.model.vendor.VendorActivity;
import petTopia.model.vendor.VendorCategory;
import petTopia.model.vendor.VendorImages;
import petTopia.repository.vendor.VendorActivityRepository;
import petTopia.repository.vendor.VendorCategoryRepository;
import petTopia.repository.vendor.VendorImagesRepository;
import petTopia.repository.vendor.VendorRepository;

import java.io.IOException;
import java.util.*;

@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class VendorProfileService {
    private final VendorRepository vendorRepository;
    private final VendorCategoryRepository vendorCategoryRepository;
    private final VendorActivityRepository vendorActivityRepository;
    private final VendorImagesRepository vendorImagesRepository;

    public Vendor getVendorById(Integer id) {
        return vendorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Vendor not found"));
    }

    public VendorProfile getVendorProfile(@RequestParam Integer vendorId) {
        Vendor vendor = vendorRepository.findById(vendorId)
                .orElseThrow(() -> new EntityNotFoundException("Vendor not found"));

        String imageBase64 = null;
        if (vendor.getLogoImg() != null) {
            imageBase64 = "data:image/jpeg;base64," + Base64.getEncoder().encodeToString(vendor.getLogoImg());
        }

        List<VendorCategory> categories = vendorCategoryRepository.findAll();
        List<VendorActivity> activities = vendorActivityRepository.findByVendorId(vendorId);

        return VendorProfile.builder()
                .vendor(vendor)
                .allcategory(categories)
                .vendorLogoImgBase64(imageBase64)
                .activityCount(activities.size())
                .build();
    }

    @Transactional
    public Vendor updateVendor(Integer vendorId, UpdateVendorRequest request) throws IOException {
        Vendor vendor = this.getVendorById(vendorId);

        if (request.getVendorName() != null) vendor.setName(request.getVendorName());
        if (request.getContactEmail() != null) vendor.setContactEmail(request.getContactEmail());
        if (request.getVendorPhone() != null) vendor.setPhone(request.getVendorPhone());
        if (request.getContactPerson() != null) vendor.setContactPerson(request.getContactPerson());
        if (request.getVendorAddress() != null) vendor.setAddress(request.getVendorAddress());
        if (request.getVendorDescription() != null) vendor.setDescription(request.getVendorDescription());
        if (request.getVendorTaxIdNumber() != null) vendor.setTaxIdNumber(request.getVendorTaxIdNumber());

        if (request.getVendorLogoImg() != null && !request.getVendorLogoImg().isEmpty()) {
            vendor.setLogoImg(request.getVendorLogoImg().getBytes());
        }

        if (request.getCategoryId() != null) {
            VendorCategory vendorCategory = vendorCategoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new EntityNotFoundException("Category not found"));
            vendor.setVendorCategory(vendorCategory);
        }

        Vendor updatedVendor = vendorRepository.save(vendor);

        if (request.getDeletedImageIds() != null && !request.getDeletedImageIds().isEmpty()) {
            vendorImagesRepository.deleteAllById(request.getDeletedImageIds());
        }

        if (request.getFiles() != null && request.getFiles().length > 0) {
            List<VendorImages> vendorImagesList = new ArrayList<>();
            for (MultipartFile file : request.getFiles()) {
                if (!file.isEmpty()) {
                    VendorImages vendorImage = new VendorImages();

                    vendorImage.setImage(file.getBytes());
                    vendorImage.setVendor(vendor);

                    vendorImagesList.add(vendorImage);
                }
            }

            vendor.getVendorImages().addAll(vendorImagesList);
            vendorImagesRepository.saveAll(vendorImagesList);
        }

        return updatedVendor;
    }

    public byte[] downloadPhotoById(@RequestParam Integer photoId) {
        return vendorImagesRepository.findById(photoId)
                .map(VendorImages::getImage)
                .orElseThrow(() -> new EntityNotFoundException("Photo byte array not found"));
    }

    public List<Integer> findPhotoIdsByVendorId(Integer vendorId) {
        List<Integer> imageIds = new ArrayList<>();

        vendorRepository.findById(vendorId)
                .map(Vendor::getVendorImages)
                .stream()
                .flatMap(List::stream)
                .forEach(image -> imageIds.add(image.getId()));

        return imageIds;
    }
}
