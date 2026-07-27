package petTopia.controller.vendor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import petTopia.dto.vendor.FriendlyShopDto;
import petTopia.dto.vendor.request.AddFriendlyShopRequest;
import petTopia.dto.vendor.request.ModifyFriendlyShopRequest;
import petTopia.model.vendor.FriendlyShop;
import petTopia.model.vendor.VendorCategory;
import petTopia.service.vendor.FriendlyShopService;
import petTopia.service.vendor.VendorCategoryService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/vendor")
public class FriendlyShopController {
    private final FriendlyShopService friendlyShopService;
    private final VendorCategoryService vendorCategoryService;

    @GetMapping("/{vendorId}/coordinate")
    public ResponseEntity<FriendlyShopDto> getFriendlyShopByVendorId(@PathVariable Integer vendorId) {
        FriendlyShopDto friendlyShop = friendlyShopService.findFirstByVendorId(vendorId);
        return ResponseEntity.ok(friendlyShop);
    }

    @GetMapping("/all/coordinate")
    public ResponseEntity<List<FriendlyShopDto>> getAllFriendlyShops() {
        List<FriendlyShopDto> friendlyShops = friendlyShopService.findAll();
        return ResponseEntity.ok(friendlyShops);
    }

    @PostMapping("/coordinate/find")
    public ResponseEntity<List<FriendlyShopDto>> findByKeyword(@RequestBody Map<String, String> data) {
        String keyword = data.get("keyword");
        List<FriendlyShopDto> friendlyShops = friendlyShopService.findByKeyword(keyword);
        return ResponseEntity.ok(friendlyShops);
    }

    // TODO: similar API
    @GetMapping("/coordinate/find/vendor/{vendorId}")
    public ResponseEntity<List<FriendlyShopDto>> findByVendorId(@PathVariable Integer vendorId) {
        List<FriendlyShopDto> friendlyShops = friendlyShopService.findByVendorId(vendorId);
        return ResponseEntity.ok(friendlyShops);
    }

    @GetMapping("/coordinate/find/category/{categoryId}")
    public ResponseEntity<List<FriendlyShopDto>> findByVendorCategory(@PathVariable Integer categoryId) {
        List<FriendlyShopDto> friendlyShops = friendlyShopService.findByCategoryId(categoryId);
        return ResponseEntity.ok(friendlyShops);
    }

    @PostMapping("/coordinate")
    public ResponseEntity<?> findByAddress(@RequestParam String address) {
        BigDecimal[] coordinate = friendlyShopService.getLatLng(address);
        return ResponseEntity.ok(coordinate);
    }

    @PostMapping("/friendly_shop/add")
    public ResponseEntity<FriendlyShopDto> addFriendlyShop(@Valid @RequestBody AddFriendlyShopRequest request) {
        FriendlyShopDto friendlyShop = friendlyShopService.addFriendlyShop(request);
        return ResponseEntity.ok(friendlyShop);
    }

    @PutMapping("/friendly_shop/{id}/modify")
    public ResponseEntity<FriendlyShopDto> modifyFriendlyShop(@PathVariable Integer id,
                                                              @Valid @RequestBody ModifyFriendlyShopRequest request) {
        FriendlyShopDto friendlyShop = friendlyShopService.modifyFriendlyShop(id, request);
        return ResponseEntity.ok(friendlyShop);
    }

    @DeleteMapping("/friendly_shop/{id}/delete")
    public ResponseEntity<?> addFriendlyShop(@PathVariable Integer id) {
        friendlyShopService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/friendly_shop/{id}")
    public ResponseEntity<FriendlyShopDto> getFriendlyShop(@PathVariable Integer id) {
        FriendlyShopDto friendlyShop = friendlyShopService.getById(id);
        return ResponseEntity.ok(friendlyShop);
    }

    @GetMapping("/category/for/friendly_shop")
    public ResponseEntity<?> getAllCategories() {
        List<VendorCategory> categoryList = vendorCategoryService.findAll();
        return ResponseEntity.ok(categoryList);
    }
}
