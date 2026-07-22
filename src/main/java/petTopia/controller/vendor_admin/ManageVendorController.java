package petTopia.controller.vendor_admin;

import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
        boolean success = managevendorService.updateVendorStatus(id, request.isStatus());
        return success
                ? ResponseEntity.ok("狀態已更新")
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body("店家不存在");
    }

    @PutMapping("/api/admin/vendors/status/bulk")
    public ResponseEntity<String> bulkUpdateVendorStatus(@RequestBody BulkVendorStatusRequest request) {
        boolean success = managevendorService.bulkUpdateVendorStatus(request.getVendorIds(), request.isStatus());
        return success
                ? ResponseEntity.ok("批量狀態已更新")
                : ResponseEntity.status(HttpStatus.BAD_REQUEST).body("更新失敗");
    }

    @Getter
    public static class VendorStatusRequest {
        private boolean status;
    }

    @Getter
    @Setter
    public static class BulkVendorStatusRequest {
        private List<Integer> vendorIds;
        private boolean status;
    }
}
