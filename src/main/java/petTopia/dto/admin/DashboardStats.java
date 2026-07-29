package petTopia.dto.admin;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStats {
    private Long totalOrders;
    private Long totalMembers;
    private Long totalProducts;
    private Long totalVendors;
    private Long totalActivities;
    private Long totalRevenue;
}
