package petTopia.controller.vendor_admin;

import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import petTopia.dto.vendor_admin.request.BulkVendorStatusRequest;
import petTopia.dto.vendor_admin.request.VendorStatusRequest;
import petTopia.model.vendor.Vendor;
import petTopia.model.vendor.VendorCategory;
import petTopia.service.vendor_admin.ManageVendorService;
import petTopia.service.vendor_admin.VendorServiceAdmin;

@RequiredArgsConstructor
@RestController
public class ManageVendorController {
    private final ManageVendorService managevendorService;
    private final VendorServiceAdmin vendorServiceAdmin;

    @GetMapping("/api/admin/vendors/allcategory")
    public ResponseEntity<?> getAllCategory() {
        List<VendorCategory> categories = vendorServiceAdmin.getAllVendorCategories();
        return ResponseEntity.ok(Map.of("allcategory", categories));
    }

    @GetMapping("/api/admin/vendors")
    public ResponseEntity<List<Vendor>> getAllVendors(@RequestParam(required = false) Integer categoryId,
                                                      @RequestParam(required = false) Boolean status) {
        List<Vendor> vendors = managevendorService.getAllVendors(categoryId, status);
        return ResponseEntity.ok(vendors);
    }

    @PutMapping("/api/admin/vendors/status/{id}")
    public ResponseEntity<String> updateVendorStatus(@PathVariable Integer id,
                                                     @RequestBody VendorStatusRequest request) {
        return managevendorService.updateVendorStatus(id, request.isStatus())
                ? ResponseEntity.ok("狀態已更新")
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body("店家不存在");
    }

    @PutMapping("/api/admin/vendors/status/bulk")
    public ResponseEntity<String> bulkUpdateVendorStatus(@RequestBody BulkVendorStatusRequest request) {
        return managevendorService.bulkUpdateVendorStatus(request.getVendorIds(), request.isStatus())
                ? ResponseEntity.ok("批量狀態已更新")
                : ResponseEntity.status(HttpStatus.BAD_REQUEST).body("更新失敗");
    }
}
