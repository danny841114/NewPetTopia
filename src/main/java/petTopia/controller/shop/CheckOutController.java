package petTopia.controller.shop;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import petTopia.dto.shop.PaymentResponseDto;
import petTopia.dto.shop.request.ProcessCheckout;
import petTopia.dto.shop.response.CheckoutInfo;
import petTopia.model.shop.Cart;
import petTopia.model.shop.Coupon;
import petTopia.model.shop.Order;
import petTopia.model.shop.PaymentCategory;
import petTopia.model.shop.ShippingAddress;
import petTopia.model.shop.ShippingCategory;
import petTopia.model.user.Member;
import petTopia.service.shop.CartService;
import petTopia.service.shop.CouponService;
import petTopia.service.shop.OrderService;
import petTopia.service.shop.PaymentService;
import petTopia.service.user.MemberService;
import petTopia.repository.shop.PaymentCategoryRepository;
import petTopia.repository.shop.ShippingCategoryRepository;
import petTopia.repository.shop.ShippingAddressRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RequiredArgsConstructor
@RestController
@RequestMapping("/shop")
public class CheckOutController {
    private final ShippingAddressRepository shippingAddressRepo;
    private final ShippingCategoryRepository shippingCategoryRepo;
    private final PaymentCategoryRepository paymentCategoryRepo;

    private final CartService cartService;
    private final CouponService couponService;
    private final OrderService orderService;
    private final PaymentService paymentService;
    private final MemberService memberService;

    @GetMapping("/checkout")
    public ResponseEntity<CheckoutInfo> getCheckoutInfo(@RequestParam List<Integer> productIds, @RequestParam Integer memberId) {
        List<Cart> cartItems = cartService.getCartByMemberIdAndProductIds(memberId, productIds);
        BigDecimal subtotal = cartService.calculateTotalPrice(memberId, productIds);
        List<ShippingCategory> shippingCategories = shippingCategoryRepo.findAll();
        List<PaymentCategory> paymentCategories = paymentCategoryRepo.findAll();

        CheckoutInfo checkoutInfo = CheckoutInfo.builder()
                .cartItems(cartItems)
                .subtotal(subtotal)
                .shippingCategories(shippingCategories)
                .paymentCategories(paymentCategories)
                .build();

        return ResponseEntity.ok(checkoutInfo);
    }

    // TODO: Use @PathVariable
    @GetMapping("/member")
    public ResponseEntity<Object> getMemberInfo(@RequestParam Integer memberId) {
        Member member = memberService.getMemberById(memberId);
        return ResponseEntity.ok(member);
    }

    @GetMapping("/shipping/address")
    public ResponseEntity<Object> getShippingAddress(@RequestParam Integer memberId) {
        Member member = memberService.getMemberById(memberId);

        ShippingAddress lastShippingAddress = shippingAddressRepo.findByMemberAndIsCurrent(member, true);
        if (lastShippingAddress == null) {
            lastShippingAddress = new ShippingAddress();  // 避免前端渲染錯誤
        }

        return ResponseEntity.ok(lastShippingAddress);
    }

    // TODO: get memberId by credential
    @GetMapping("/coupons")
    public ResponseEntity<Object> getCoupons(@RequestParam List<Integer> productIds, @RequestParam Integer memberId) {
        BigDecimal subtotal = cartService.calculateTotalPrice(memberId, productIds);

        // 更新優惠券使用次數
        couponService.updateCouponUsageCount(memberId);
        Map<String, List<Coupon>> couponsMap = couponService.getCouponsByAmount(memberId, subtotal);

        List<Coupon> availableCoupons = couponsMap.get("available");
        List<Coupon> notMeetCoupons = couponsMap.get("notMeet");

        // 獲取選取的優惠券
        Map<String, Object> response = new HashMap<>();
        response.put("availableCoupons", availableCoupons);
        response.put("notMeetCoupons", notMeetCoupons);

        return ResponseEntity.ok(response);
    }

    // TODO: get memberId by credential
    @PostMapping("/checkout")
    public ResponseEntity<?> processCheckout(@RequestBody ProcessCheckout checkoutData, @RequestParam Integer memberId) {
        Member member = memberService.getMemberById(memberId);

        // 從 checkoutData 取得各種資料
        try {
            // 從 checkoutData 取得各種資料
            Integer couponId = checkoutData.getCouponId();
            Integer shippingCategoryId = checkoutData.getShippingCategoryId();
            Integer paymentCategoryId = checkoutData.getPaymentCategoryId();

            // 取得購物車內的商品 ID 清單
            List<Integer> productIdList = checkoutData.getCartItems()
                    .stream()
                    .map(ProcessCheckout.CartItem::getProductId)
                    .collect(Collectors.toList());

            // 收件人資訊
            String receiverName = checkoutData.getReceiverName();
            String receiverPhone = checkoutData.getReceiverPhone();
            String street = checkoutData.getStreet();
            String city = checkoutData.getCity();
            String amount = checkoutData.getPaymentAmount();

            BigDecimal paymentAmount = (amount != null) ? new BigDecimal(amount) : null;

            // 建立訂單
            Map<String, Object> orderResponse = orderService.createOrder(
                    member, memberId, couponId, shippingCategoryId,
                    paymentCategoryId, paymentAmount, street, city,
                    receiverName, receiverPhone, productIdList
            );
            Order order = (Order) orderResponse.get("order");

            // 信用卡付款
            if (paymentCategoryId == 1) {
                PaymentResponseDto paymentResponse = paymentService.processCreditCardPayment(order, paymentCategoryId);

                if (paymentResponse != null) {
                    return ResponseEntity.ok(Map.of(
                            "message", "訂單建立成功，請前往付款",
                            "paymentData", paymentResponse
                    ));
                } else {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "付款頁面生成失敗"));
                }
            }

            // 非信用卡付款
            return ResponseEntity.ok(Map.of(
                    "message", "訂單建立成功，請查看訂單詳情",
                    "orderId", order.getId()
            ));

        } catch (RuntimeException ex) {
            // 處理例如庫存不足、訂單無效等錯誤
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "商品庫存不足，無法完成訂單",
                    "message", ex.getMessage()
            ));
        } catch (Exception ex) {
            // 處理其他未預期的錯誤
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "error", "系統發生錯誤，請稍後再試",
                    "message", ex.getMessage()
            ));
        }
    }

    @PostMapping("/payment/ecpay/callback")
    public ResponseEntity<String> handleEcPayCallback(@RequestParam Map<String, String> callbackParams) {
        try {
            // 呼叫 Service 層處理回調邏輯
            String response = paymentService.handleEcPayCallback(callbackParams);

            return ResponseEntity.ok(response); // 確保回應是 "1|OK" 或 "0|Error: XXX"
        } catch (Exception e) {
            return ResponseEntity.ok("0|Error: " + e.getMessage()); // 發生錯誤時，仍符合 ECPay 格式
        }
    }
}
