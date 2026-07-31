package petTopia.service.shop;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import petTopia.dto.shop.OrderDetailDto;
import petTopia.dto.shop.OrderItemDto;
import petTopia.model.shop.Cart;
import petTopia.model.shop.Order;
import petTopia.model.shop.OrderDetail;
import petTopia.model.shop.Payment;
import petTopia.model.shop.Product;
import petTopia.model.shop.Shipping;
import petTopia.projection.shop.ProductSalesProjection;
import petTopia.repository.shop.OrderDetailRepository;
import petTopia.repository.shop.OrderRepository;
import petTopia.repository.shop.PaymentRepository;
import petTopia.repository.shop.ShippingRepository;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class OrderDetailService {
    private final OrderDetailRepository orderDetailRepo;
    private final OrderRepository orderRepo;
    private final ShippingRepository shippingRepo;
    private final PaymentRepository paymentRepo;

    @Transactional
    public void createOrderDetails(Order order, List<Cart> cartItems) {
        List<OrderDetail> orderDetails = new ArrayList<>();

        for (Cart cartItem : cartItems) {
            Product product = cartItem.getProduct();
            Integer quantity = cartItem.getQuantity();
            BigDecimal unitPrice = product.getUnitPrice();
            BigDecimal discountPrice = product.getDiscountPrice();

            // 如果 discountPrice 是 null，則使用 unitPrice 計算 totalPrice
            BigDecimal totalPrice = (discountPrice == null)
                    ? unitPrice.multiply(BigDecimal.valueOf(quantity))
                    : discountPrice.multiply(BigDecimal.valueOf(quantity));

            // **建立訂單詳情**
            OrderDetail orderDetail = new OrderDetail();

            orderDetail.setOrder(order);
            orderDetail.setProduct(product);
            orderDetail.setQuantity(quantity);
            orderDetail.setUnitPrice(unitPrice);
            orderDetail.setDiscountPrice(discountPrice);
            orderDetail.setTotalPrice(totalPrice);

            orderDetails.add(orderDetail);
        }

        // **批量儲存 OrderDetail**
        orderDetailRepo.saveAll(orderDetails);
    }

    // 查詢訂單的詳情
    public OrderDetailDto getOrderDetailById(Integer orderId) {
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));

        List<OrderDetail> orderDetails = orderDetailRepo.findByOrderId(orderId);

        List<OrderItemDto> orderItemDtos = orderDetails.stream()
                .map(OrderItemDto::convertToDto)
                .collect(Collectors.toList());

        Shipping shipping = shippingRepo.findByOrderId(orderId).orElse(null);
        Payment payment = paymentRepo.findByOrderId(orderId).orElse(null);

        return OrderDetailDto.convertToDto(order, orderItemDtos, shipping, payment);
    }

    // 銷售最好的前五名商品及其商品詳情（只計算已完成的訂單）
    public List<ProductSalesProjection> getTop5BestSellingProductsWithDetails() {
        Pageable top5Page = PageRequest.of(0, 5);
        return orderDetailRepo.findTop5BestSellingProductsWithDetails(top5Page);
    }
}
