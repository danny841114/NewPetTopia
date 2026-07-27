package petTopia.controller.vendor;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import petTopia.dto.vendor.VendorDto;
import petTopia.dto.vendor.VendorImageDto;
import petTopia.model.vendor.VendorCategory;
import petTopia.service.vendor.VendorCategoryService;
import petTopia.service.vendor.VendorImagesService;
import petTopia.service.vendor.VendorService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/vendor")
public class VendorController {
    private final VendorService vendorService;
    private final VendorImagesService vendorImagesService;
    private final VendorCategoryService vendorCategoryService;

    @GetMapping("/{vendorId}")
    public ResponseEntity<VendorDto> getVendorDetail(@PathVariable Integer vendorId) {
        VendorDto dto = vendorService.findVendorById(vendorId);
        return ResponseEntity.ok(dto);
    }

    @GetMapping(path = "/{vendorId}/logImg", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> getVendorLogoImage(@PathVariable Integer vendorId) {
        byte[] logoImg = vendorService.findVendorLogoImgById(vendorId);
        return ResponseEntity.ok(logoImg);
    }

    @GetMapping("/all")
    public ResponseEntity<List<VendorDto>> getAllVendors() {
        List<VendorDto> dtos = vendorService.findAllVendor();
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/all/except/{vendorId}")
    public ResponseEntity<List<VendorDto>> getAllVendorsExceptOne(@PathVariable Integer vendorId) {
        List<VendorDto> dtos = vendorService.findAllVendorExceptOne(vendorId);
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{vendorId}/image")
    public ResponseEntity<List<VendorImageDto>> getVendorImages(@PathVariable Integer vendorId) {
        List<VendorImageDto> dtos = vendorImagesService.findImageListByVendorId(vendorId);
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<VendorDto>> getVendorsByCategory(@PathVariable Integer categoryId) {
        List<VendorDto> dtos = vendorService.findVendorByCategoryId(categoryId);
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/category/{categoryId}/except/vendor/{vendorId}")
    public ResponseEntity<List<VendorDto>> getVendorsByCategoryExceptOne(@PathVariable Integer categoryId,
                                                                         @PathVariable Integer vendorId) {
        List<VendorDto> dtos = vendorService.findVendorByCategoryIdExceptOne(categoryId, vendorId);
        return ResponseEntity.ok(dtos);
    }

    @PostMapping("/find")
    public ResponseEntity<List<VendorDto>> getVendorsByNameOrDescription(@RequestParam String keyword) {
        List<VendorDto> dtos = vendorService.findVendorByNameOrDescription(keyword);
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/category/show")
    public ResponseEntity<List<VendorCategory>> getAllCategories() {
        List<VendorCategory> categories = vendorCategoryService.findAllCategoriesWithVendors();
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/all/for/swiper")
    public ResponseEntity<List<VendorDto>> getAllVendorsForSwiper() {
        List<VendorDto> dtos = vendorService.getAllVendorDto();
        return ResponseEntity.ok(dtos);
    }

    @GetMapping(path = "/img/{id}", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> getVendorImg(@PathVariable Integer id) {
        byte[] vendorImg = vendorImagesService.getVendorImageById(id);
        return ResponseEntity.ok(vendorImg);
    }
}
