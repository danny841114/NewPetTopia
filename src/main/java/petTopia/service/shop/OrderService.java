package petTopia.service.shop;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.transaction.annotation.Transactional;
import petTopia.dto.shop.OrderHistoryDto;
import petTopia.dto.shop.OrderItemDto;
import petTopia.dto.shop.OrderSummaryAmountDto;
import petTopia.dto.shop.request.OrderHistoryRequest;
import petTopia.model.shop.Cart;
import petTopia.model.shop.Coupon;
import petTopia.model.shop.Order;
import petTopia.model.shop.OrderDetail;
import petTopia.model.shop.OrderStatus;
import petTopia.model.shop.Payment;
import petTopia.model.shop.PaymentCategory;
import petTopia.model.shop.Product;
import petTopia.model.shop.Shipping;
import petTopia.model.shop.ShippingAddress;
import petTopia.model.shop.ShippingCategory;
import petTopia.model.user.Member;
import petTopia.repository.shop.CartRepository;
import petTopia.repository.shop.CouponRepository;
import petTopia.repository.shop.OrderDetailRepository;
import petTopia.repository.shop.OrderRepository;
import petTopia.repository.shop.OrderStatusRepository;
import petTopia.repository.shop.PaymentCategoryRepository;
import petTopia.repository.shop.PaymentRepository;
import petTopia.repository.shop.ShippingCategoryRepository;
import petTopia.repository.shop.ShippingRepository;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class OrderService {
    private final CouponRepository couponRepo;
    private final CartService cartService;
    private final CartRepository cartRepo;
    private final ShippingCategoryRepository shippingCategoryRepo;
    private final CouponService couponService;
    private final PaymentCategoryRepository paymentCategoryRepo;
    private final OrderRepository orderRepo;
    private final ShippingRepository shippingRepo;
    private final ProductService productService;
    private final PaymentService paymentService;
    private final OrderStatusRepository orderStatusRepo;
    private final ShippingService shippingService;
    private final OrderDetailService orderDetailService;
    private final OrderDetailRepository orderDetailRepo;
    private final PaymentRepository paymentRepo;
    private final EntityManager entityManager;

//	================================================

    public OrderSummaryAmountDto calculateOrderSummary(Integer memberId,
                                                       Integer couponId,
                                                       Integer shippingCategoryId,
                                                       List<Integer> productIds) {
        // 計算商品總金額
        BigDecimal subtotal = cartService.calculateTotalPrice(memberId, productIds);

        // 計算折扣
        BigDecimal discountAmount = BigDecimal.ZERO;
        if (couponId != null) {
            Coupon coupon = couponRepo.findById(couponId)
                    .orElseThrow(() -> new EntityNotFoundException("找不到對應的優惠券"));

            discountAmount = couponService.getDiscountAmountByCoupon(coupon, subtotal);

            // 四捨五入到整數
            discountAmount = discountAmount.setScale(0, RoundingMode.HALF_UP);
        }

        // 取得運費（允許 null，預設 0）
        BigDecimal shippingFee = BigDecimal.ZERO;
        if (shippingCategoryId != null) {
            shippingFee = shippingCategoryRepo.findById(shippingCategoryId)
                    .map(ShippingCategory::getShippingCost)
                    .orElse(BigDecimal.ZERO);
        }

        // 計算最終總金額
        BigDecimal orderTotal = subtotal.subtract(discountAmount).add(shippingFee);

        // 四捨五入到整數
        orderTotal = orderTotal.setScale(0, RoundingMode.HALF_UP);

        // 回傳 DTO
        return new OrderSummaryAmountDto(subtotal, discountAmount, shippingFee, orderTotal);
    }

    //新增訂單
    @Transactional
    public Order createOrder(Member member,
                                           Integer memberId,
                                           Integer couponId,
                                           Integer shippingCategoryId,
                                           Integer paymentCategoryId,
                                           BigDecimal paymentAmount,
                                           String street,
                                           String city,
                                           String receiverName,
                                           String receiverPhone,
                                           List<Integer> productIds) {

        // 查詢會員購物車中這些商品
        List<Cart> cartItems = cartRepo.findByMemberIdAndProductIdIn(memberId, productIds);

        // 更新庫存
        try {
            productService.updateStockLevelsByCartItems(cartItems);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("庫存不足，無法完成訂單", e);
        }

        // 計算購物車 subtotal 金額
        BigDecimal subtotal = cartService.calculateTotalPrice(memberId, productIds);

        Coupon coupon = null;
        BigDecimal discountAmount = BigDecimal.ZERO;

        // 若有使用優惠券，計算折扣金額
        if (couponId != null) {
            coupon = couponRepo.findById(couponId)
                    .orElseThrow(() -> new EntityNotFoundException("Coupon not found"));

            discountAmount = couponService.getDiscountAmountByCoupon(coupon, subtotal);
        }

        // 透過運送方式 ID 拿到對應的運費
        BigDecimal shippingFee = shippingCategoryRepo.findById(shippingCategoryId)
                .map(ShippingCategory::getShippingCost)
                .orElse(BigDecimal.ZERO);

        // 計算最終金額：商品總金額 - 折扣 + 運費
        BigDecimal orderTotal = subtotal.subtract(discountAmount).add(shippingFee);

        // 四捨五入到整數
        orderTotal = orderTotal.setScale(0, RoundingMode.HALF_UP);

        // ==================建立訂單==================
        // 設定訂單狀態 (預設為 "待付款")
        OrderStatus orderStatus = orderStatusRepo.findById(1)  // "待付款" 狀態
                .orElseThrow(() -> new EntityNotFoundException("Order status not found"));

        // 建立新訂單
        Order order = new Order();

        order.setMember(member);
        order.setSubtotal(subtotal);
        order.setCoupon(coupon);
        order.setDiscountAmount(discountAmount);
        order.setShippingFee(shippingFee);
        order.setTotalAmount(orderTotal);
        order.setOrderStatus(orderStatus);
        order.setCreatedTime(new Date());
        order.setUpdatedDate(new Date());

        orderRepo.save(order);

        // 訂單詳情
        orderDetailService.createOrderDetails(order, cartItems);

        // 更新優惠券使用次數
        if (couponId != null) couponService.updateCouponUsageCount(memberId);

        // ==================建立運送資訊================
        ShippingAddress shippingAddress = shippingService.createShippingAddress(member, city, street);

        ShippingCategory shippingCategory = shippingCategoryRepo.findById(shippingCategoryId)
                .orElseThrow(() -> new EntityNotFoundException("Shipping category not found"));

        Shipping shipping = shippingService.createShipping(order, shippingAddress, shippingCategory, receiverName, receiverPhone);

        shippingRepo.save(shipping);

        // ==============建立付款資訊==============
        PaymentCategory paymentCategory = paymentCategoryRepo.findById(paymentCategoryId)
                .orElseThrow(() -> new EntityNotFoundException("Payment category not found"));

        // **貨到付款**
        if (paymentCategory.getId() == 2) {
            if (!paymentService.createCashOnDeliveryPayment(order, paymentCategory)) {
                throw new RuntimeException("貨到付款處理失敗，請重新操作");
            }

            // 直接將訂單狀態設為 "待出貨"
            order.setOrderStatus(orderStatusRepo.findById(2)
                    .orElseThrow(() -> new IllegalArgumentException("找不到待出貨狀態")));
        } else {
            // **信用卡支付或其他支付方式處理**
            if (paymentCategory.getId() == 1) {  // 若是信用卡付款
                // 直接將訂單狀態設為 "待處理"    //等付款後再變成待出貨
                order.setOrderStatus(orderStatusRepo.findById(1)
                        .orElseThrow(() -> new IllegalArgumentException("找不到待處理狀態")));

                if (!paymentService.createCreditCardPayment(order, paymentCategory)) {
                    throw new RuntimeException("信用卡付款處理失敗，請重新操作");
                }
            }
        }

        Order savedOrder = orderRepo.save(order);

        // 清空購物車
        cartService.clearCart(memberId, productIds);

        return savedOrder;
    }

    //把 order 轉成 orderHistoryDto
    private OrderHistoryDto convertToOrderHistoryDto(Order order) {
        OrderHistoryDto orderHistory = new OrderHistoryDto();

        orderHistory.setOrderId(order.getId());
        orderHistory.setOrderStatus(order.getOrderStatus().getName()); // 設定訂單狀態
        orderHistory.setCreatedTime(new java.sql.Date(order.getCreatedTime().getTime()));
        orderHistory.setTotalAmount(order.getTotalAmount());

        // 查詢付款狀態
        Payment payment = paymentRepo.findByOrderId(order.getId()).orElse(null);

        if (payment != null && payment.getPaymentStatus() != null) {
            orderHistory.setPaymentStatus(payment.getPaymentStatus().getName());
        } else {
            orderHistory.setPaymentStatus("待付款");
        }

        if (payment != null && payment.getPaymentCategory() != null) {
            orderHistory.setPaymentCategory(payment.getPaymentCategory().getName());
        }

        Shipping shipping = shippingRepo.findByOrderId(order.getId()).orElse(null);

        if (shipping != null && shipping.getShippingCategory() != null) {
            orderHistory.setShippingCategory(shipping.getShippingCategory().getName());
        }

        // 查詢該訂單的商品明細
        List<OrderDetail> orderDetails = orderDetailRepo.findByOrderId(order.getId());

        // 轉換商品明細
        List<OrderItemDto> orderItemDtos = orderDetails.stream()
                .map(OrderItemDto::convertToDto)
                .collect(Collectors.toList());

        orderHistory.setOrderItems(orderItemDtos);

        return orderHistory;
    }

    public Page<OrderHistoryDto> getOrderHistoryFilter(OrderHistoryRequest request) {
        int page = (request.getPage() < 1) ? 1 : request.getPage();
        int size = (request.getSize() < 1) ? 5 : request.getSize();

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Order> query = criteriaBuilder.createQuery(Order.class);
        Root<Order> root = query.from(Order.class);

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(criteriaBuilder.equal(root.get("member").get("id"), request.getMemberId()));

        // 訂單狀態 or 付款狀態篩選
        if (request.getOrderStatus() != null && !request.getOrderStatus().isEmpty()) {
            Predicate statusPredicate;
            Join<Order, Payment> paymentJoin = root.join("payment", JoinType.LEFT);
            if (request.getOrderStatus().equals("已付款") || request.getOrderStatus().equals("待付款")) {
                statusPredicate = criteriaBuilder.equal(paymentJoin.get("paymentStatus").get("name"), request.getOrderStatus());
            } else {
                statusPredicate = criteriaBuilder.equal(root.get("orderStatus").get("name"), request.getOrderStatus());
            }
            predicates.add(statusPredicate);
        }

        // 付款方式篩選
        if (request.getPaymentCategory() != null && !request.getPaymentCategory().isEmpty()) {
            Join<Order, Payment> paymentJoin = root.join("payment", JoinType.LEFT);
            predicates.add(criteriaBuilder.equal(paymentJoin.get("paymentCategory").get("name"), request.getPaymentCategory()));
        }

        // 配送方式篩選
        if (request.getShippingCategory() != null && !request.getShippingCategory().isEmpty()) {
            Join<Order, Shipping> shippingJoin = root.join("shipping", JoinType.LEFT);
            predicates.add(criteriaBuilder.equal(shippingJoin.get("shippingCategory").get("name"), request.getShippingCategory()));
        }

        // 訂單日期範圍
        if (request.getStartDate() != null) {
            predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createdTime"), request.getStartDate()));
        }
        if (request.getEndDate() != null) {
            predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("createdTime"), request.getEndDate()));
        }

        // 搜尋關鍵字（訂單編號 or 商品名稱）
        if (request.getKeyword() != null && !request.getKeyword().isEmpty()) {
            Predicate orderIdPredicate = criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("id").as(String.class)),
                    "%" + request.getKeyword().toLowerCase() + "%"
            );

            // 商品名稱搜尋
            Join<Order, OrderDetail> orderDetailsJoin = root.join("orderDetails", JoinType.LEFT);
            Join<OrderDetail, Product> productJoin = orderDetailsJoin.join("product", JoinType.LEFT);
            Predicate productNamePredicate = criteriaBuilder.like(
                    criteriaBuilder.lower(productJoin.get("productDetail").get("name")),
                    "%" + request.getKeyword().toLowerCase() + "%"
            );

            predicates.add(criteriaBuilder.or(orderIdPredicate, productNamePredicate));
        }

        // 設定查詢條件
        query.where(predicates.toArray(new Predicate[0]));
        query.orderBy(criteriaBuilder.desc(root.get("id")));

        // 執行查詢，得到所有符合條件的資料
        List<Order> orders = entityManager.createQuery(query).getResultList();

        // 計算總記錄數
        long totalRecords = orders.size();

        // 分頁
        int startIndex = (page - 1) * size;
        int endIndex = Math.min(startIndex + size, orders.size());
        List<Order> paginatedOrders = orders.subList(startIndex, endIndex);

        // 轉換 DTO
        List<OrderHistoryDto> orderDtos = paginatedOrders.stream()
                .map(this::convertToOrderHistoryDto)
                .collect(Collectors.toList());

        return new PageImpl<>(orderDtos, PageRequest.of(page - 1, size), totalRecords);
    }


    @Transactional
    public void cancelOrder(Integer orderId, Integer memberId) {
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("找不到訂單"));

        // **標記訂單為取消**
        orderStatusRepo.findById(6)
                .ifPresent(order::setOrderStatus);
        orderRepo.save(order);

        // **恢復優惠券使用次數**
        if (order.getCoupon() != null) {
            couponService.updateCouponUsageCount(memberId);
        }
    }

    // 查詢該會員的所有訂單
//    public List<OrderHistoryDto> getOrderHistoryByMemberId(Integer memberId) {
//        List<Order> orders = orderRepo.findByMemberId(memberId);
//
//        return orders.stream().map(order -> {
//            OrderHistoryDto orderHistory = new OrderHistoryDto();
//            orderHistory.setOrderId(order.getId());
//            orderHistory.setOrderStatus(order.getOrderStatus().getName());
//            orderHistory.setCreatedTime(new java.sql.Date(order.getCreatedTime().getTime()));
//            orderHistory.setTotalAmount(order.getTotalAmount());
//
//            // 查詢付款狀態
//            Payment payment = paymentRepo.findByOrderId(order.getId());
//            orderHistory.setPaymentStatus(payment.getPaymentStatus().getName());
//
//            // 查詢該訂單的商品明細
//            List<OrderDetail> orderDetails = orderDetailRepo.findByOrderId(order.getId());
//
//            // **這裡改用 getOrderItemDto 方法**
//            List<OrderItemDto> orderItemDtos = orderDetails.stream()
//                    .map(orderDetailService::getOrderItemDto) // 呼叫已經寫好的方法
//                    .collect(Collectors.toList());
//
//            orderHistory.setOrderItems(orderItemDtos);
//
//            return orderHistory;
//        }).collect(Collectors.toList());
//    }
}
