package petTopia.dto.shop;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;
import petTopia.model.shop.OrderDetail;
import petTopia.model.shop.ProductColor;
import petTopia.model.shop.ProductSize;

@Getter
@Setter
public class OrderItemDto {
    private Integer productId;
    private Integer productDetailId;
    private byte[] productPhoto;
    private String productSize;
    private String productColor;
    private String productName;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal discountPrice;
    private BigDecimal totalPrice;

    public static OrderItemDto convertToDto(OrderDetail orderDetail) {
        OrderItemDto orderItemDto = new OrderItemDto();

        orderItemDto.setProductId(orderDetail.getProduct().getId());
        orderItemDto.setProductDetailId(orderDetail.getId());
        orderItemDto.setProductPhoto(orderDetail.getProduct().getPhoto());
        orderItemDto.setProductName(orderDetail.getProduct().getProductDetail().getName());

        ProductSize productSize = orderDetail.getProduct().getProductSize();
        orderItemDto.setProductSize(productSize != null ? productSize.getName() : null);

        ProductColor productColor = orderDetail.getProduct().getProductColor();
        orderItemDto.setProductColor(productColor != null ? productColor.getName() : null);

        orderItemDto.setQuantity(orderDetail.getQuantity());
        orderItemDto.setUnitPrice(orderDetail.getUnitPrice());
        orderItemDto.setDiscountPrice(orderDetail.getDiscountPrice());
        orderItemDto.setTotalPrice(orderDetail.getTotalPrice());

        return orderItemDto;
    }
}
