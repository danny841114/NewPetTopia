package petTopia.controller.shop;

import java.util.Objects;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import petTopia.dto.shop.OrderDetailDto;
import petTopia.dto.shop.OrderHistoryDto;
import petTopia.dto.shop.request.OrderHistoryRequest;
import petTopia.repository.shop.OrderRepository;
import petTopia.service.shop.OrderDetailService;
import petTopia.service.shop.OrderService;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/shop")
public class OrderController {
    private final OrderService orderService;
    private final OrderDetailService orderDetailService;

    private final OrderRepository orderRepo;

    // TODO: Similar API existing (ManageOrderController class)
    //訂單詳情頁
    @GetMapping("/orders/{orderId}")
    public ResponseEntity<OrderDetailDto> getOrderDetail(@RequestParam Integer memberId, @PathVariable Integer orderId) {
        try {
            // 使用 Service 層方法查詢訂單詳情
            OrderDetailDto orderDetailDto = orderDetailService.getOrderDetailById(orderId);

            Integer memberLogin = orderDetailDto.getMemberId();

            if (!Objects.equals(memberLogin, memberId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            return ResponseEntity.ok(orderDetailDto);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // 查詢會員訂單歷史紀錄
    @GetMapping("/orderHistory")
    public ResponseEntity<Page<OrderHistoryDto>> getOrderHistory(@ModelAttribute OrderHistoryRequest request) {
        try {
            Page<OrderHistoryDto> orderHistoryPage = orderService.getOrderHistoryFilter(request);
            return ResponseEntity.ok(orderHistoryPage);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    // 取消訂單的 API
    @PutMapping("/orders/{orderId}/cancel")
    public ResponseEntity<String> cancelOrder(@RequestParam Integer memberId, @PathVariable Integer orderId) {
        try {
            orderService.cancelOrder(orderId, memberId); // 呼叫服務層的 cancelOrder 方法

            return ResponseEntity.ok("訂單已成功取消");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("訂單不存在或取消失敗");
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("伺服器錯誤，請稍後再試");
        }
    }
}
