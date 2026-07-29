package petTopia.controller.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import petTopia.dto.admin.GetCouponsRequest;
import petTopia.model.shop.Coupon;
import petTopia.service.admin.CouponService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/admin/coupons")
@CrossOrigin(origins = {"http://localhost:5173"}, allowCredentials = "true")
public class CouponController {
    private final CouponService couponService;

    @GetMapping
    public ResponseEntity<?> getCoupons(@ModelAttribute GetCouponsRequest request) {
        Page<Coupon> couponPage = couponService.getCoupons(request);
        return ResponseEntity.ok(couponPage);
    }

    @PostMapping
    public ResponseEntity<?> createCoupon(@RequestBody Coupon coupon) {
        Coupon savedCoupon = couponService.createCoupon(coupon);
        return ResponseEntity.ok(savedCoupon);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCoupon(@PathVariable Integer id) {
        Coupon coupon = couponService.getCoupon(id);
        return ResponseEntity.ok(coupon);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCoupon(@PathVariable Integer id, @RequestBody Coupon coupon) {
        Coupon updatedCoupon = couponService.updateCoupon(id, coupon);
        return ResponseEntity.ok(updatedCoupon);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCoupon(@PathVariable Integer id) {
        couponService.deleteCoupon(id);
        return ResponseEntity.noContent().build();
    }
}