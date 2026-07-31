package petTopia.dto.shop;

import lombok.Getter;
import lombok.Setter;
import petTopia.model.shop.OrderDetail;

@Getter
@Setter
public class OrderItemAnalysisDto {
    private Integer orderId;
    private Integer productId;
    private Integer productDetailId;
    private String productName;
    private String productColor;
    private String productSize;
    private Integer quantity;
    private Double unitPrice;
    private Double discountPrice;
    private Double totalPrice;

    public static OrderItemAnalysisDto convertToDto(OrderDetail orderDetail) {
        OrderItemAnalysisDto orderItemAnalysisDto = new OrderItemAnalysisDto();

        // orderId
        if (orderDetail.getOrder() != null) orderItemAnalysisDto.setOrderId(orderDetail.getOrder().getId());

        // productId
        if (orderDetail.getProduct() != null) orderItemAnalysisDto.setProductId(orderDetail.getProduct().getId());

        // productDetailId
        if (orderDetail.getProduct() != null && orderDetail.getProduct().getProductDetail() != null)
            orderItemAnalysisDto.setProductDetailId(orderDetail.getProduct().getProductDetail().getId());

        // productName
        if (orderDetail.getProduct() != null && orderDetail.getProduct().getProductDetail() != null)
            orderItemAnalysisDto.setProductName(orderDetail.getProduct().getProductDetail().getName());
        else orderItemAnalysisDto.setProductName("無");

        // productColor
        if (orderDetail.getProduct() != null && orderDetail.getProduct().getProductColor() != null)
            orderItemAnalysisDto.setProductColor(orderDetail.getProduct().getProductColor().getName());
        else orderItemAnalysisDto.setProductColor("無");

        // productSize
        if (orderDetail.getProduct() != null && orderDetail.getProduct().getProductSize() != null)
            orderItemAnalysisDto.setProductSize(orderDetail.getProduct().getProductSize().getName());
        else orderItemAnalysisDto.setProductSize("無");

        // quantity
        orderItemAnalysisDto.setQuantity(orderDetail.getQuantity() != null ? orderDetail.getQuantity() : 0);

        // unitPrice
        orderItemAnalysisDto.setUnitPrice((orderDetail.getUnitPrice() != null) ? orderDetail.getUnitPrice().doubleValue() : 0.0);

        // discountPrice
        orderItemAnalysisDto.setDiscountPrice((orderDetail.getDiscountPrice() != null) ? orderDetail.getDiscountPrice().doubleValue() : 0.0);

        // totalPrice
        orderItemAnalysisDto.setTotalPrice((orderDetail.getTotalPrice() != null) ? orderDetail.getTotalPrice().doubleValue() : 0.0);

        return orderItemAnalysisDto;
    }
}
