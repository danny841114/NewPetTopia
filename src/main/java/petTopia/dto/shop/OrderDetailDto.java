package petTopia.dto.shop;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import petTopia.model.shop.Order;
import petTopia.model.shop.Payment;
import petTopia.model.shop.Shipping;

@Getter
@Setter
public class OrderDetailDto {
    private Integer memberId;
    private Integer orderId;
    private BigDecimal subtotal;
    private BigDecimal discountAmount;
    private BigDecimal shippingFee;
    private BigDecimal totalAmount;
    private String orderStatus;
    private Date createdTime;
    private Date updatedDate;
    private List<OrderItemDto> orderItems;
    private ShippingInfoDto shippingInfo;
    private PaymentInfoDto paymentInfo;

    public static OrderDetailDto convertToDto(Order order, List<OrderItemDto> orderItemDtos, Shipping shipping, Payment payment) {
        OrderDetailDto orderDetailDto = new OrderDetailDto();

        orderDetailDto.setMemberId(order.getMember().getId());
        orderDetailDto.setOrderId(order.getId());
        orderDetailDto.setSubtotal(order.getSubtotal());
        orderDetailDto.setDiscountAmount(order.getDiscountAmount());
        orderDetailDto.setShippingFee(order.getShippingFee());
        orderDetailDto.setTotalAmount(order.getTotalAmount());
        orderDetailDto.setOrderStatus(order.getOrderStatus().getName());
        orderDetailDto.setCreatedTime(new java.sql.Date(order.getCreatedTime().getTime()));
        orderDetailDto.setUpdatedDate(order.getUpdatedDate() != null ? new java.sql.Date(order.getUpdatedDate().getTime()) : null);
        orderDetailDto.setOrderItems(orderItemDtos);

        ShippingInfoDto shippingInfoDto = new ShippingInfoDto();
        if (shipping != null) {
            shippingInfoDto.setReceiverName(shipping.getReceiverName() != null ? shipping.getReceiverName() : "無");
            shippingInfoDto.setReceiverPhone(shipping.getReceiverPhone() != null ? shipping.getReceiverPhone() : "無");
            shippingInfoDto.setStreet(shipping.getShippingAddress() != null ? shipping.getShippingAddress().getStreet() : "無");
            shippingInfoDto.setCity(shipping.getShippingAddress() != null ? shipping.getShippingAddress().getCity() : "");
            shippingInfoDto.setShippingCategory(shipping.getShippingCategory() != null ? shipping.getShippingCategory().getName() : "無");
        }

        PaymentInfoDto paymentInfoDto = new PaymentInfoDto();
        if (payment != null) {
            paymentInfoDto.setPaymentCategory(payment.getPaymentCategory().getName());
            paymentInfoDto.setPaymentAmount(payment.getPaymentAmount());
            paymentInfoDto.setPaymentStatus(payment.getPaymentStatus().getName());
        } else {
            paymentInfoDto.setPaymentCategory("待確認");
            paymentInfoDto.setPaymentAmount(new BigDecimal(0));
            paymentInfoDto.setPaymentStatus("待付款");
        }

        orderDetailDto.setShippingInfo(shippingInfoDto);
        orderDetailDto.setPaymentInfo(paymentInfoDto);

        return orderDetailDto;
    }
}
