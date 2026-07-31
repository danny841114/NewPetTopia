package petTopia.dto.shop;

import lombok.*;
import petTopia.model.shop.OrderDetail;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ManageOrderItemDto {
    private Integer productId;
    private String productName;

    public static ManageOrderItemDto covertToDto(OrderDetail orderDetail) {
        return ManageOrderItemDto.builder()
                .productId(orderDetail.getProduct().getId())
                .productName(orderDetail.getProduct().getProductDetail().getName())
                .build();
    }
}
