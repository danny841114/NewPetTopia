package petTopia.controller.vendor;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import petTopia.dto.vendor.VendorDto;
import petTopia.model.vendor.Vendor;
import petTopia.model.vendor.VendorCategory;
import petTopia.model.vendor.VendorImages;
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
    public ResponseEntity<Vendor> getVendorDetail(@PathVariable Integer vendorId) {
        Vendor vendor = vendorService.findVendorById(vendorId);
        return ResponseEntity.ok(vendor);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Vendor>> getAllVendors() {
        List<Vendor> vendorList = vendorService.findAllVendor();
        return ResponseEntity.ok(vendorList);
    }

    @GetMapping("/all/except/{vendorId}")
    public ResponseEntity<List<Vendor>> getAllVendorsExceptOne(@PathVariable Integer vendorId) {
        List<Vendor> vendorList = vendorService.findAllVendorExceptOne(vendorId);
        return ResponseEntity.ok(vendorList);
    }

    @GetMapping("/{vendorId}/image")
    public ResponseEntity<List<VendorImages>> getVendorImages(@PathVariable Integer vendorId) {
        List<VendorImages> imageList = vendorImagesService.findImageListByVendorId(vendorId);
        return ResponseEntity.ok(imageList);
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<Vendor>> getVendorsByCategory(@PathVariable Integer categoryId) {
        List<Vendor> vendorList = vendorService.findVendorByCategoryId(categoryId);
        return ResponseEntity.ok(vendorList);
    }

    @GetMapping("/category/{categoryId}/except/vendor/{vendorId}")
    public ResponseEntity<List<Vendor>> getVendorsByCategoryExceptOne(@PathVariable Integer categoryId,
                                                                      @PathVariable Integer vendorId) {
        List<Vendor> vendorList = vendorService.findVendorByCategoryIdExceptOne(categoryId, vendorId);
        return ResponseEntity.ok(vendorList);
    }

    @PostMapping(value = "/find", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<List<Vendor>> getVendorsByNameOrDescription(@RequestParam String keyword) {
        List<Vendor> vendorList = vendorService.findVendorByNameOrDescription(keyword);
        return ResponseEntity.ok(vendorList);
    }

    @GetMapping("/category/show")
    public ResponseEntity<List<VendorCategory>> getAllCategories() {
        List<VendorCategory> categoryList = vendorCategoryService.findAllVendorCategoriesWithVendors();
        return ResponseEntity.ok(categoryList);
    }

    @GetMapping("/all/for/swiper")
    public ResponseEntity<List<VendorDto>> getAllVendorsForSwiper() {
        List<VendorDto> dtoList = vendorService.getAllVendorDto();
        return ResponseEntity.ok(dtoList);
    }
}
