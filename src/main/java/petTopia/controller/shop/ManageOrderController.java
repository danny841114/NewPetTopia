package petTopia.controller.shop;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import petTopia.dto.shop.ManageAllOrdersDto;
import petTopia.dto.shop.OrderAnalysisDto;
import petTopia.dto.shop.OrderDetailDto;
import petTopia.dto.shop.OrderItemAnalysisDto;
import petTopia.dto.shop.SalesDto;
import petTopia.dto.shop.UpdateOneOrderDto;
import petTopia.projection.shop.ProductCategorySalesProjection;
import petTopia.projection.shop.ProductSalesProjection;
import petTopia.repository.shop.OrderRepository;
import petTopia.repository.shop.OrderStatusRepository;
import petTopia.repository.shop.PaymentCategoryRepository;
import petTopia.repository.shop.PaymentStatusRepository;
import petTopia.repository.shop.ProductRepository;
import petTopia.repository.shop.ProductReviewRepository;
import petTopia.repository.shop.ShippingCategoryRepository;
import petTopia.service.shop.ManageOrderService;
import petTopia.service.shop.OrderAnalysisExcelService;
import petTopia.service.shop.OrderDetailService;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/manage/shop")
public class ManageOrderController {
    private final ManageOrderService manageOrderService;
    private final OrderDetailService orderDetailService;
    private final OrderAnalysisExcelService excelService;

    private final PaymentCategoryRepository paymentCategoryRepo;
    private final ShippingCategoryRepository shippingCategoryRepo;
    private final ProductReviewRepository productReviewRepo;
    private final ProductRepository productRepo;
    private final OrderRepository orderRepo;
    private final PaymentStatusRepository paymentStatusRepo;
    private final OrderStatusRepository orderStatusRepo;

    @GetMapping("/orders/options")
    public ResponseEntity<Map<String, Object>> getOrderOptions() {
        Map<String, Object> response = new HashMap<>();

        response.put("paymentStatusList", paymentStatusRepo.findAllPaymentStatus());
        response.put("orderStatusList", orderStatusRepo.findAllOrderStatus());
        response.put("paymentCategoryList", paymentCategoryRepo.findAllPaymentCategory());
        response.put("shippingCategoryList", shippingCategoryRepo.findAllShippingCategory());

        return ResponseEntity.ok(response);
    }

    // 後台訂單管理>>查詢會員訂單歷史紀錄
    @GetMapping("/orders")
    public ResponseEntity<Map<String, Object>> getManageOrderHistory(@RequestParam(required = false) String memberId,
                                                                     @RequestParam(required = false) String orderStatus,
                                                                     @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
                                                                     @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate,
                                                                     @RequestParam(required = false) String orderId, // 訂單編號篩選
                                                                     @RequestParam(required = false) String paymentStatus, // 付款狀態篩選
                                                                     @RequestParam(required = false) String productKeyword, // 商品名稱或訂單編號關鍵字篩選
                                                                     @RequestParam(required = false) String paymentCategory, // 付款方式篩選
                                                                     @RequestParam(required = false) String shippingCategory, // 配送方式篩選
                                                                     @RequestParam(defaultValue = "0") int page,
                                                                     @RequestParam(defaultValue = "10") int size) {
        try {
            Page<ManageAllOrdersDto> manageOrdersPage = manageOrderService.getManageOrderHistoryFilter(
                    memberId,
                    orderStatus,
                    startDate,
                    endDate,
                    orderId,
                    paymentStatus,
                    productKeyword,
                    paymentCategory,
                    shippingCategory,
                    page,
                    size
            );

            if (manageOrdersPage.isEmpty()) return ResponseEntity.noContent().build();

            Map<String, Object> response = new HashMap<>();
            response.put("manageOrders", manageOrdersPage);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Get manage order history failed", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    //訂單詳情頁
    @GetMapping("/orders/{orderId}")
    public ResponseEntity<OrderDetailDto> getOrderDetail(@PathVariable("orderId") Integer orderId) {
        try {
            OrderDetailDto orderDetailDto = orderDetailService.getOrderDetailById(orderId);
            return ResponseEntity.ok(orderDetailDto);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    //更新單一訂單狀態/各種方式
    @PutMapping("/orders/{orderId}/update")
    public ResponseEntity<String> updateOrder(@PathVariable Integer orderId,
                                              @RequestBody UpdateOneOrderDto updatedOrderRequest) {
        try {
            manageOrderService.updateOrder(orderId, updatedOrderRequest);
            return ResponseEntity.ok("訂單已更新");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("更新失敗: " + e.getMessage());
        }
    }

    //批量更新訂單狀態 或 付款狀態
    @PutMapping("/orders/updateBatch")
    public ResponseEntity<String> updateBatchOrders(@RequestParam List<Integer> orderIds,
                                                    @RequestParam String batchStatus) {
        try {
            manageOrderService.updateBatchOrders(orderIds, batchStatus);
            return ResponseEntity.ok("批量更新成功");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("批量更新失敗: " + e.getMessage());
        }
    }

    //刪除訂單
    @DeleteMapping("/orders/{orderId}/delete")
    public ResponseEntity<String> deleteOrder(@PathVariable Integer orderId) {
        try {
            manageOrderService.deleteOrder(orderId);  // 呼叫服務層刪除訂單
            return ResponseEntity.ok("訂單已成功刪除");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("訂單未找到");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("刪除訂單時發生錯誤");
        }
    }

    // 獲取銷售最高的前 5 名商品詳情
    @GetMapping("/orders/top5BestSellingProducts")
    public ResponseEntity<List<ProductSalesProjection>> getTop5BestSellingProductsWithDetails() {
        try {
            List<ProductSalesProjection> top5Products = orderDetailService.getTop5BestSellingProductsWithDetails();

            if (top5Products.isEmpty()) return ResponseEntity.noContent().build();

            return ResponseEntity.ok(top5Products);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/orders/sales")
    public ResponseEntity<Map<String, Object>> getSalesData() {
        try {
            SalesDto salesData = manageOrderService.getSalesData();

            Map<String, Object> formattedData = salesData.getFormattedSalesData();

            return ResponseEntity.ok(formattedData);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/orders/category-sales")
    public ResponseEntity<Map<String, Object>> getCategorySales() {
        try {
            List<ProductCategorySalesProjection> salesData = manageOrderService.getProductCategorySales();

            Map<String, Object> response = new HashMap<>();
            response.put("data", salesData);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "伺服器內部錯誤，請稍後再試");

            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    // 生成訂單財務報表
    @GetMapping("/orders/generateReport")
    public ResponseEntity<byte[]> generateReport(@RequestParam String orderStartDate,
                                                 @RequestParam String orderEndDate) throws IOException, ParseException {
        // 指定日期格式
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        // 轉換 String 為 Date
        Date startDate = dateFormat.parse(orderStartDate);
        Date endDate = dateFormat.parse(orderEndDate);

        // 查詢數據
        List<OrderAnalysisDto> orders = manageOrderService.getOrdersAnalysisByDateRange(startDate, endDate);
        List<OrderItemAnalysisDto> orderItems = manageOrderService.getOrderItemsByDateRange(startDate, endDate);

        // 產生 Excel
        byte[] excelData = excelService.generateOrdersAndItemsExcel(orders, orderItems);

        // 設定回應 Headers
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=orders_and_items_report.xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .body(excelData);
    }

    // 取得統計資料：訂單數量、評論數量、低庫存商品數量
    @GetMapping("/dashboard/summary")
    public ResponseEntity<?> getDashboardSummary() {
        try {
            long orderCount = orderRepo.count();
            long reviewCount = productReviewRepo.countTotalProductReviews();
            long lowStockCount = productRepo.countLowStockProducts();

            return ResponseEntity.ok(Map.of(
                    "totalOrders", orderCount,
                    "totalReviews", reviewCount,
                    "lowStockProducts", lowStockCount
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("無法取得統計資料，請稍後再試：" + e.getMessage());
        }
    }
}
