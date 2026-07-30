package petTopia.dto.shop.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDashboardSummary {
    private Long totalOrders;
    private Long totalReviews;
    private Long lowStockProducts;
}
