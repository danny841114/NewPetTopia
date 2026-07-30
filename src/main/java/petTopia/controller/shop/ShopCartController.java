package petTopia.controller.shop;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import petTopia.model.shop.Cart;
import petTopia.model.shop.Coupon;
import petTopia.service.shop.CartService;
import petTopia.service.shop.CouponService;
import petTopia.service.shop.ProductService;
import petTopia.service.user.MemberService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/shop/cart")
public class ShopCartController {
    private final MemberService memberService;
    private final ProductService productService;
    private final CartService cartService;
    private final CouponService couponService;

    // 會員購物車頁面
    @PostMapping
    public ResponseEntity<?> showMemberCart(@RequestParam Integer memberId) {
        List<Cart> cartList = cartService.getCartByMemberId(memberId);
        return ResponseEntity.ok(cartList);
    }

    // 會員購物車頁面 => 更改數量直接更新購物車
    @PostMapping("/api/updateCartProductQuantity")
    public ResponseEntity<?> updateCartProductQuantity(@RequestParam Integer memberId,
                                                       @RequestParam Integer productId,
                                                       @RequestParam Integer quantity) {
        Cart cart = cartService.updateCartProductQuantity(memberId, productId, quantity);
        return cart != null
                ? ResponseEntity.ok(cart)
                : ResponseEntity.notFound().build();
    }

    // TODO: Similar API exists
    // 會員購物車頁面 => 獲取商品圖片
    @GetMapping("/api/getPhoto")
    public ResponseEntity<?> getPhoto(@RequestParam Integer productId) {
        byte[] photo = productService.getPhotoByProductId(productId);

        if (photo == null || photo.length == 0) return ResponseEntity.notFound().build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);

        return ResponseEntity.ok()
                .headers(headers)
                .body(photo);
    }

    // 會員購物車頁面 => 刪除購物車
    @GetMapping("/api/deleteCartById")
    public ResponseEntity<?> deleteCartById(@RequestParam Integer cartId) {
        // 獲取該商品的購物車
        Integer deleteCartId = cartService.deleteCartById(cartId);

        if (deleteCartId != null) {
            return new ResponseEntity<>(deleteCartId, HttpStatus.OK);
        }

        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

    // TODO: NOT MODIFIED
    // 會員購物車頁面 => 獲取會員優惠券
    @GetMapping("/coupons")
    public ResponseEntity<Object> getCoupons(@RequestParam Integer selectedCouponId,
                                             @RequestParam Integer memberId) {
        Map<String, Object> response = new HashMap<>();

        // 更新優惠券使用次數
        couponService.updateCouponUsageCount(memberId);
        Map<String, List<Coupon>> couponsMap = couponService.getCoupons(memberId);

        List<Coupon> availableCoupons = couponsMap.get("available");
        List<Coupon> expiredCoupons = couponsMap.get("expired");

        // 獲取選取的優惠券
        Coupon selectedCoupon = null;
        if (selectedCouponId != null) selectedCoupon = couponService.getCouponById(selectedCouponId);

        if (availableCoupons != null) response.put("availableCoupons", availableCoupons);
        if (expiredCoupons != null) response.put("expiredCoupons", expiredCoupons);
        if (selectedCoupon != null) response.put("selectedCoupon", selectedCoupon);

        return ResponseEntity.ok(response);
    }

    // HeaderShop => 更新會員購物車顯示數量
    @GetMapping("/api/getMemberCartCount")
    public ResponseEntity<Integer> getMemberCartCount(@RequestParam Integer memberId) {
        Integer cartCount = cartService.getMemeberCartCount(memberId);
        return ResponseEntity.ok(cartCount);
    }
}
