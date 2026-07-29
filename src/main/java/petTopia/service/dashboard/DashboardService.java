package petTopia.service.dashboard;

import petTopia.dto.admin.DashboardStats;

public interface DashboardService {
    long getTotalOrders();

    long getTotalMembers();

    long getTotalProducts();

    long getTotalVendors();

    long getTotalActivities();

    long getTotalRevenue();

    DashboardStats getCurrentCounts();
} 