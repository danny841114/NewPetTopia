package petTopia.controller.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import petTopia.dto.admin.GetCouponsRequest;
import petTopia.model.shop.Coupon;
import petTopia.service.admin.CouponAdminService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/admin/coupons")
@CrossOrigin(origins = {"http://localhost:5173"}, allowCredentials = "true")
public class CouponController {
    private final CouponAdminService couponAdminService;

    @GetMapping
    public ResponseEntity<?> getCoupons(@ModelAttribute GetCouponsRequest request) {
        Page<Coupon> couponPage = couponAdminService.getCoupons(request);
        return ResponseEntity.ok(couponPage);
    }

    @PostMapping
    public ResponseEntity<?> createCoupon(@RequestBody Coupon coupon) {
        Coupon savedCoupon = couponAdminService.createCoupon(coupon);
        return ResponseEntity.ok(savedCoupon);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCoupon(@PathVariable Integer id) {
        Coupon coupon = couponAdminService.getCoupon(id);
        return ResponseEntity.ok(coupon);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCoupon(@PathVariable Integer id, @RequestBody Coupon coupon) {
        Coupon updatedCoupon = couponAdminService.updateCoupon(id, coupon);
        return ResponseEntity.ok(updatedCoupon);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCoupon(@PathVariable Integer id) {
        couponAdminService.deleteCoupon(id);
        return ResponseEntity.noContent().build();
    }
}