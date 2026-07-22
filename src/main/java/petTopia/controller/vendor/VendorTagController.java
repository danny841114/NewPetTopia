package petTopia.controller.vendor;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import petTopia.model.vendor.VendorCertificationTag;
import petTopia.service.vendor.VendorTagService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/vendor")
public class VendorTagController {
    private final VendorTagService vendorTagService;

    @GetMapping("/{vendorId}/tag")
    public ResponseEntity<List<VendorCertificationTag>> getVendorTagList(@PathVariable Integer vendorId) {
        List<VendorCertificationTag> tagList = vendorTagService.findConfirmedTagByVendorId(vendorId);
        return ResponseEntity.ok(tagList);
    }
}
