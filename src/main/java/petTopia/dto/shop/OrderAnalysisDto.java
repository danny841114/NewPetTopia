package petTopia.dto.shop;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;
import petTopia.model.shop.Order;
import petTopia.model.shop.Payment;
import petTopia.model.shop.Shipping;

@Getter
@Setter
public class OrderAnalysisDto {
    private Integer orderId; //訂單編號
    private Date createdTime; //訂單日期
    private String orderStatus; //訂單狀態
    private Integer memberId; //會員編號
    private String memberName; //會員姓名
    private String memberPhone; //會員電話
    private Double subtotal; // 商品總金額
    private Double discountAmount;    //折扣金額
    private Double shippingFee; //運費
    private Double totalAmount; //訂單總金額(應付金額)
    private Double paymentAmount; //實際付款金額
    private String paymentCategory;  //付款方式
    private String paymentStatus; //付款狀態
    private Date paymentDate; //付款時間
    private String shippingCategory; //配送方式
    private Date lastModifiedDate; //訂單更新時間

    public static OrderAnalysisDto convertToDto(Order order, Shipping shipping, Payment payment) {
        OrderAnalysisDto orderAnalysisDto = new OrderAnalysisDto();

        orderAnalysisDto.setOrderId(order.getId());
        orderAnalysisDto.setCreatedTime(order.getCreatedTime());
        orderAnalysisDto.setOrderStatus(order.getOrderStatus().getName());
        orderAnalysisDto.setMemberId(order.getMember().getId());
        orderAnalysisDto.setMemberName(order.getMember().getName() != null ? order.getMember().getName() : "無");
        orderAnalysisDto.setMemberPhone(order.getMember().getPhone() != null ? order.getMember().getPhone() : "無");
        orderAnalysisDto.setSubtotal((order.getSubtotal() != null) ? order.getSubtotal().doubleValue() : 0.0);
        orderAnalysisDto.setDiscountAmount((order.getDiscountAmount() != null) ? order.getDiscountAmount().doubleValue() : 0.0);
        orderAnalysisDto.setShippingFee((order.getShippingFee() != null) ? order.getShippingFee().doubleValue() : 0.0);
        orderAnalysisDto.setTotalAmount((order.getTotalAmount() != null) ? order.getTotalAmount().doubleValue() : 0.0);

        if (shipping != null) {
            orderAnalysisDto.setShippingCategory(shipping.getShippingCategory() != null ? shipping.getShippingCategory().getName() : "無");
            orderAnalysisDto.setLastModifiedDate(shipping.getUpdatedTime() != null ? shipping.getUpdatedTime() : null);
        } else {
            orderAnalysisDto.setShippingCategory("無");
            orderAnalysisDto.setLastModifiedDate(null);
        }

        if (payment != null) {
            orderAnalysisDto.setPaymentCategory(payment.getPaymentCategory() != null ? payment.getPaymentCategory().getName() : "無");
            orderAnalysisDto.setPaymentStatus(payment.getPaymentStatus() != null ? payment.getPaymentStatus().getName() : "無");
            orderAnalysisDto.setPaymentDate(payment.getPaymentDate() != null ? payment.getPaymentDate() : null);
            orderAnalysisDto.setPaymentAmount((payment.getPaymentAmount() != null) ? payment.getPaymentAmount().doubleValue() : 0.0);
        } else {
            orderAnalysisDto.setPaymentCategory("無");
            orderAnalysisDto.setPaymentStatus("無");
            orderAnalysisDto.setPaymentDate(null);
            orderAnalysisDto.setPaymentAmount(0.0);
        }

        return orderAnalysisDto;
    }
}
