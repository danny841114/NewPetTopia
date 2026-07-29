package petTopia.service.dashboard.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import petTopia.dto.admin.DashboardStats;
import petTopia.repository.shop.OrderRepository;
import petTopia.repository.shop.ProductRepository;
import petTopia.repository.user.MemberRepository;
import petTopia.repository.vendor.VendorActivityRepository;
import petTopia.repository.vendor.VendorRepository;
import petTopia.service.dashboard.DashboardService;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class DashboardServiceImpl implements DashboardService {
    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;
    private final VendorRepository vendorRepository;
    private final VendorActivityRepository vendorActivityRepository;

    @Override
    public long getTotalOrders() {
        return orderRepository.count();
    }

    @Override
    public long getTotalMembers() {
        return memberRepository.count();
    }

    @Override
    public long getTotalProducts() {
        return productRepository.count();
    }

    @Override
    public long getTotalVendors() {
        return vendorRepository.count();
    }

    @Override
    public long getTotalActivities() {
        return vendorActivityRepository.count();
    }

    @Override
    public long getTotalRevenue() {
        return orderRepository.count();
    }

    @Override
    public DashboardStats getCurrentCounts() {
        return DashboardStats.builder()
                .totalOrders(getTotalOrders())
                .totalMembers(getTotalMembers())
                .totalProducts(getTotalProducts())
                .totalVendors(getTotalVendors())
                .totalActivities(getTotalActivities())
                .totalRevenue(getTotalRevenue())
                .build();
    }
}