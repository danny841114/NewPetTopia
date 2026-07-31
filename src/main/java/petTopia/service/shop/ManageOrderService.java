package petTopia.service.shop;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
import petTopia.dto.shop.ManageAllOrdersDto;
import petTopia.dto.shop.ManageOrderItemDto;
import petTopia.dto.shop.OrderAnalysisDto;
import petTopia.dto.shop.OrderItemAnalysisDto;
import petTopia.dto.shop.SalesDto;
import petTopia.dto.shop.UpdateOneOrderDto;
import petTopia.dto.shop.request.OrderHistoryRequest;
import petTopia.dto.shop.response.OrderDashboardSummary;
import petTopia.dto.shop.response.OrderOptions;
import petTopia.model.shop.Order;
import petTopia.model.shop.OrderDetail;
import petTopia.model.shop.OrderStatus;
import petTopia.model.shop.Payment;
import petTopia.model.shop.PaymentCategory;
import petTopia.model.shop.PaymentStatus;
import petTopia.model.shop.Product;
import petTopia.model.shop.Shipping;
import petTopia.model.shop.ShippingCategory;
import petTopia.projection.shop.ProductCategorySalesProjection;
import petTopia.repository.shop.OrderDetailRepository;
import petTopia.repository.shop.OrderRepository;
import petTopia.repository.shop.OrderStatusRepository;
import petTopia.repository.shop.PaymentCategoryRepository;
import petTopia.repository.shop.PaymentRepository;
import petTopia.repository.shop.PaymentStatusRepository;
import petTopia.repository.shop.ProductRepository;
import petTopia.repository.shop.ProductReviewRepository;
import petTopia.repository.shop.ShippingCategoryRepository;
import petTopia.repository.shop.ShippingRepository;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class ManageOrderService {
    private final ShippingCategoryRepository shippingCategoryRepo;
    private final PaymentCategoryRepository paymentCategoryRepo;
    private final OrderRepository orderRepo;
    private final ShippingRepository shippingRepo;
    private final OrderStatusRepository orderStatusRepo;
    private final OrderDetailService orderDetailService;
    private final OrderDetailRepository orderDetailRepo;
    private final PaymentRepository paymentRepo;
    private final PaymentStatusRepository paymentStatusRepo;
    private final ProductRepository productRepo;
    private final ProductReviewRepository productReviewRepo;
    private final OrderAnalysisExcelService excelService;
    private final EntityManager entityManager;

//	================================================

    // 把 order 轉成 manageAllOrdersDto
    private ManageAllOrdersDto convertToManageAllOrdersDto(Order order) {
        ManageAllOrdersDto managedOrder = new ManageAllOrdersDto();

        managedOrder.setMemberId(order.getMember().getId());
        managedOrder.setOrderId(order.getId());
        managedOrder.setOrderStatus(order.getOrderStatus().getName());
        managedOrder.setOrderDate(new java.sql.Date(order.getCreatedTime().getTime()));
        managedOrder.setTotalAmount(order.getTotalAmount());
        managedOrder.setNote(order.getNote());

        // 查詢付款狀態
        Payment payment = paymentRepo.findByOrderId(order.getId()).orElse(null);
        if (payment != null && payment.getPaymentStatus() != null) {
            managedOrder.setPaymentStatus(payment.getPaymentStatus().getName());
        } else {
            managedOrder.setPaymentStatus("待付款");
        }

        if (payment != null && payment.getPaymentCategory() != null) {
            managedOrder.setPaymentCategory(payment.getPaymentCategory().getName());
        }

        // 配送狀態
        Shipping shipping = shippingRepo.findByOrderId(order.getId()).orElse(null);
        if (shipping != null && shipping.getShippingCategory() != null) {
            managedOrder.setShippingCategory(shipping.getShippingCategory().getName());
        }

        // 查詢該訂單的商品明細
        List<OrderDetail> orderDetails = orderDetailRepo.findByOrderId(order.getId());

        // 使用 getManagedOrderItemDto 方法來轉換商品明細
        List<ManageOrderItemDto> manageOrderItemDtos = orderDetails.stream()
                .map(ManageOrderItemDto::covertToDto)
                .collect(Collectors.toList());

        managedOrder.setManagedOrderItems(manageOrderItemDtos);

        return managedOrder;
    }

    public Page<ManageAllOrdersDto> getManageOrderHistoryFilter(OrderHistoryRequest request) {
        int page = (request.getPage() < 1) ? 1 : request.getPage();
        int size = (request.getSize() < 1) ? 10 : request.getSize();

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Order> query = criteriaBuilder.createQuery(Order.class);
        Root<Order> root = query.from(Order.class);

        List<Predicate> predicates = new ArrayList<>();

        // 會員編號
        if (request.getMemberId() != null && !request.getMemberId().isEmpty()) {
            Predicate memberIdPredicate = criteriaBuilder.equal(
                    root.get("member").get("id"),
                    request.getMemberId()
            );
            predicates.add(memberIdPredicate);
        }

        // 訂單編號篩選
        if (request.getOrderId() != null && !request.getOrderId().isEmpty()) {
            Predicate orderIdPredicate = criteriaBuilder.equal(
                    root.get("id"),
                    request.getOrderId()
            );
            predicates.add(orderIdPredicate);
        }

        // 訂單狀態篩選
        if (request.getOrderStatus() != null && !request.getOrderStatus().isEmpty()) {
            Predicate statusPredicate = criteriaBuilder.equal(
                    root.get("orderStatus").get("name"),
                    request.getOrderStatus()
            );
            predicates.add(statusPredicate);
        }

        // 付款狀態篩選
        if (request.getPaymentStatus() != null && !request.getPaymentStatus().isEmpty()) {
            Join<Order, Payment> paymentJoin = root.join("payment", JoinType.LEFT);
            Predicate paymentStatusPredicate = criteriaBuilder.equal(
                    paymentJoin.get("paymentStatus").get("name"),
                    request.getPaymentStatus()
            );
            predicates.add(paymentStatusPredicate);
        }

        // 付款方式篩選
        if (request.getPaymentCategory() != null && !request.getPaymentCategory().isEmpty()) {
            Join<Order, Payment> paymentJoin = root.join("payment", JoinType.LEFT);
            Predicate paymentPredicate = criteriaBuilder.equal(
                    paymentJoin.get("paymentCategory").get("name"),
                    request.getPaymentCategory()
            );
            predicates.add(paymentPredicate);
        }

        // 配送方式篩選
        if (request.getShippingCategory() != null && !request.getShippingCategory().isEmpty()) {
            Join<Order, Shipping> shippingJoin = root.join("shipping", JoinType.LEFT);
            Predicate shippingPredicate = criteriaBuilder.equal(
                    shippingJoin.get("shippingCategory").get("name"),
                    request.getShippingCategory()
            );
            predicates.add(shippingPredicate);
        }

        // 訂單日期範圍
        if (request.getStartDate() != null) {
            Predicate createdTimePredicate = criteriaBuilder.greaterThanOrEqualTo(
                    root.get("createdTime"),
                    request.getStartDate()
            );
            predicates.add(createdTimePredicate);
        }

        if (request.getEndDate() != null) {
            Predicate endTimePredicate = criteriaBuilder.lessThanOrEqualTo(
                    root.get("endTime"),
                    request.getEndDate()
            );
            predicates.add(endTimePredicate);
        }

        // 搜尋關鍵字（訂單編號 or 商品名稱）
        if (request.getProductKeyword() != null && !request.getProductKeyword().isEmpty()) {
            Predicate orderIdPredicate = criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("id").as(String.class)),
                    "%" + request.getProductKeyword().toLowerCase() + "%"
            );

            // 商品名稱搜尋
            Join<Order, OrderDetail> orderDetailsJoin = root.join("orderDetails", JoinType.LEFT);
            Join<OrderDetail, Product> productJoin = orderDetailsJoin.join("product", JoinType.LEFT);
            Predicate productNamePredicate = criteriaBuilder.like(
                    criteriaBuilder.lower(productJoin.get("productDetail").get("name")),
                    "%" + request.getProductKeyword().toLowerCase() + "%"
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
        List<ManageAllOrdersDto> orderDtos = paginatedOrders.stream()
                .map(this::convertToManageAllOrdersDto)
                .collect(Collectors.toList());

        return new PageImpl<>(orderDtos, PageRequest.of(page - 1, size), totalRecords);
    }

    //更新單一訂單
    @Transactional
    public Order updateOrder(Integer orderId, UpdateOneOrderDto updatedOrderRequest) {
        String orderStatus = updatedOrderRequest.getOrderStatus();
        String paymentStatus = updatedOrderRequest.getPaymentStatus();
        String paymentCategory = updatedOrderRequest.getPaymentCategory();
        String shippingCategory = updatedOrderRequest.getShippingCategory();
        BigDecimal totalAmount = updatedOrderRequest.getTotalAmount();
        String note = updatedOrderRequest.getNote();

        // 根據訂單 ID 查詢訂單
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));

        //更新訂單備註
        if (note != null) {
            order.setNote(note);
            order.setUpdatedDate(new Date());
        }

        // 更新訂單狀態(order>orderStatus)
        if (orderStatus != null) {
            OrderStatus existOrderStatus = orderStatusRepo.findByName(orderStatus)
                    .orElseThrow(() -> new EntityNotFoundException("Order status not found"));

            // 如果狀態是配送中就更新配送日期
            if ("配送中".equals(orderStatus)) {
                Shipping shipping = shippingRepo.findByOrderId(orderId)
                        .orElseThrow(() -> new EntityNotFoundException("Shipping not found"));

                shipping.setShippingDate(new Date());
                shipping.setUpdatedTime(new Date());
            }

            order.setOrderStatus(existOrderStatus);
            order.setUpdatedDate(new Date());
        }

        // 更新付款狀態(payment>paymentStatus)
        if (paymentStatus != null) {
            PaymentStatus existPaymentStatus = paymentStatusRepo.findByName(paymentStatus)
                    .orElseThrow(() -> new EntityNotFoundException("Payment status not found"));

            Payment payment = paymentRepo.findByOrderId(orderId)
                    .orElseThrow(() -> new EntityNotFoundException("Payment not found"));

            // 只有在付款狀態為 "已付款" 時，才設置付款日期
            if ("已付款".equals(paymentStatus)) {
                payment.setPaymentDate(new Date());
            }

            payment.setPaymentStatus(existPaymentStatus);
            payment.setUpdatedDate(new Date());

            paymentRepo.save(payment);
        }

        // 更新付款方式 (payment>paymentCategory)
        if (paymentCategory != null) {
            PaymentCategory existPaymentCategory = paymentCategoryRepo.findByName(paymentCategory)
                    .orElseThrow(() -> new EntityNotFoundException("Payment category not found"));

            Payment payment = paymentRepo.findByOrderId(orderId)
                    .orElseThrow(() -> new EntityNotFoundException("Payment not found"));

            payment.setPaymentCategory(existPaymentCategory);
            payment.setUpdatedDate(new Date());

            paymentRepo.save(payment);
        }

        // 更新配送方式 (shipping>shippingCategory)
        if (shippingCategory != null) {
            ShippingCategory existShippingCategory = shippingCategoryRepo.findByName(shippingCategory)
                    .orElseThrow(() -> new EntityNotFoundException("Shipping category not found"));

            Shipping shipping = shippingRepo.findByOrderId(orderId)
                    .orElseThrow(() -> new EntityNotFoundException("Shipping not found"));

            shipping.setShippingCategory(existShippingCategory);
            shipping.setUpdatedTime(new Date());

            shippingRepo.save(shipping);
        }

        if (totalAmount != null) {
            order.setTotalAmount(totalAmount);
        }

        return orderRepo.save(order);
    }

    //批量更新訂單狀態或是付款狀態
    @Transactional
    public void updateBatchOrders(List<Integer> orderIds, String batchStatus) {
        if (orderIds == null || orderIds.isEmpty()) throw new EntityNotFoundException("Order ID list is empty");

        List<Order> orders = orderRepo.findAllById(orderIds);

        for (Order order : orders) {
            if ("已付款".equals(batchStatus)) {
                Payment payment = paymentRepo.findByOrderId(order.getId()).orElse(null);

                if (payment != null) {
                    PaymentStatus existPaymentStatus = paymentStatusRepo.findByName("已付款")
                            .orElseThrow(() -> new EntityNotFoundException("Payment status not found"));

                    payment.setPaymentStatus(existPaymentStatus);
                    payment.setPaymentDate(new Date());
                    payment.setUpdatedDate(new Date());

                    paymentRepo.save(payment);
                }
            } else if ("配送中".equals(batchStatus)) {
                OrderStatus existOrderStatus = orderStatusRepo.findByName("配送中")
                        .orElseThrow(() -> new EntityNotFoundException("Order status not found"));

                order.setOrderStatus(existOrderStatus);
                order.setUpdatedDate(new Date());

                Shipping shipping = shippingRepo.findByOrderId(order.getId()).orElse(null);

                if (shipping != null) {
                    shipping.setShippingDate(new Date());
                    shipping.setUpdatedTime(new Date());
                    shippingRepo.save(shipping);
                }
            } else if ("待收貨".equals(batchStatus)) {
                OrderStatus existOrderStatus = orderStatusRepo.findByName("待收貨")
                        .orElseThrow(() -> new EntityNotFoundException("Status not found"));

                order.setOrderStatus(existOrderStatus);
                order.setUpdatedDate(new Date());
            } else if ("已完成".equals(batchStatus)) {
                OrderStatus existOrderStatus = orderStatusRepo.findByName("已完成")
                        .orElseThrow(() -> new EntityNotFoundException("Status not found"));

                order.setOrderStatus(existOrderStatus);
                order.setUpdatedDate(new Date());
            } else if ("已取消".equals(batchStatus)) {
                OrderStatus existOrderStatus = orderStatusRepo.findByName("已取消")
                        .orElseThrow(() -> new EntityNotFoundException("Status not found"));

                order.setOrderStatus(existOrderStatus);
                order.setUpdatedDate(new Date());
            }

            orderRepo.save(order);
        }
    }

    //刪除訂單(包含訂單細節、配送資訊、付款資訊)
    @Transactional
    public void deleteOrder(Integer orderId) {
        orderRepo.findById(orderId)
                .ifPresent(orderRepo::delete);
    }

    // 獲取銷售數據（總銷售額、每日銷售趨勢、每月銷售趨勢）
    public SalesDto getSalesData() {
        // 取得每日銷售額趨勢
        List<Object[]> dailySalesData = orderRepo.calculateDailySalesTrend();
        Map<String, BigDecimal> dailySalesMap = new HashMap<>();
        for (Object[] data : dailySalesData) {
            String date = data[0].toString();
            BigDecimal sales = data[1] != null ? new BigDecimal(data[1].toString()) : BigDecimal.ZERO;
            dailySalesMap.put(date, sales);
        }

        // 生成每日銷售額趨勢
        List<Map<String, Object>> dailySalesTrend = new ArrayList<>();
        for (int month = 1; month <= 12; month++) {
            YearMonth yearMonth = YearMonth.of(2025, month);
            int daysInMonth = yearMonth.lengthOfMonth();

            for (int day = 1; day <= daysInMonth; day++) {
                String date = String.format("2025-%02d-%02d", month, day);
                BigDecimal sales = dailySalesMap.getOrDefault(date, BigDecimal.ZERO);
                Map<String, Object> dailySales = new HashMap<>();
                dailySales.put("date", date);
                dailySales.put("sales", sales);
                dailySalesTrend.add(dailySales);
            }
        }

        // 計算每月銷售額
        List<Object[]> monthlySalesData = orderRepo.calculateMonthlySalesTrend();
        Map<Integer, BigDecimal> monthlySalesMap = new HashMap<>();

        // 先放入 SQL 查詢結果
        for (Object[] data : monthlySalesData) {
            int month = (Integer) data[1];
            BigDecimal sales = data[2] != null ? (BigDecimal) data[2] : BigDecimal.ZERO;
            monthlySalesMap.put(month, sales);
        }

        // 1~12 月的銷售額
        List<Map<String, Object>> monthlySalesTrend = new ArrayList<>();
        for (int month = 1; month <= 12; month++) {
            Map<String, Object> monthlySales = new HashMap<>();
            monthlySales.put("year", 2025);
            monthlySales.put("month", month);
            monthlySales.put("sales", monthlySalesMap.getOrDefault(month, BigDecimal.ZERO));
            monthlySalesTrend.add(monthlySales);
        }

        // 總銷售額
        BigDecimal totalSales = orderRepo.calculateTotalSales();

        // 回傳 SaleDto 物件
        return new SalesDto(totalSales, dailySalesTrend, monthlySalesTrend);
    }

    //商品種類比例
    public List<ProductCategorySalesProjection> getProductCategorySales() {
        return orderDetailRepo.findProductCategorySales();
    }

    //=====orderItems=====

    public OrderDashboardSummary getDashboardSummary() {
        return OrderDashboardSummary.builder()
                .totalOrders(orderRepo.count())
                .totalReviews(productReviewRepo.count())
                .lowStockProducts(productRepo.count())
                .build();
    }

    public OrderOptions getOrderOptions() {
        return OrderOptions.builder()
                .paymentStatusList(paymentStatusRepo.findAllPaymentStatus())
                .orderStatusList(orderStatusRepo.findAllOrderStatus())
                .paymentCategoryList(paymentCategoryRepo.findAllPaymentCategory())
                .shippingCategoryList(shippingCategoryRepo.findAllShippingCategoryNames())
                .build();
    }

    public byte[] getOrderReport(String orderStartDate, String orderEndDate) throws ParseException, IOException {
        // 指定日期格式並轉換為 Date
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date startDate = dateFormat.parse(orderStartDate);
        Date endDate = dateFormat.parse(orderEndDate);

        // 查詢分析數據
        List<OrderAnalysisDto> orders = orderRepo.findOrdersByDateRange(startDate, endDate)
                .stream()
                .map(order -> this.getOrderAnalysisById(order.getId()))
                .collect(Collectors.toList());

        // 查詢商品明細
        List<OrderItemAnalysisDto> orderItems = orderDetailRepo.findOrderDetailsByDateRange(startDate, endDate)
                .stream()
                .map(OrderItemAnalysisDto::convertToDto)
                .collect(Collectors.toList());

        // 產生 Excel
        return excelService.generateOrdersAndItemsExcel(orders, orderItems);
    }

    //財務報表分析
    private OrderAnalysisDto getOrderAnalysisById(Integer orderId) {
        // 查詢訂單
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with ID: " + orderId));

        // 查詢配送資訊
        Shipping shipping = shippingRepo.findByOrderId(orderId).orElse(null);

        // 查詢付款資訊
        Payment payment = paymentRepo.findByOrderId(orderId).orElse(null);

        // 組合為 OrderAnalysisDto
        return OrderAnalysisDto.convertToDto(order, shipping, payment);
    }
}
