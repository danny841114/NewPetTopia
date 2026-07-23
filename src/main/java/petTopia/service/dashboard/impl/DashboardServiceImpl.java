package petTopia.service.dashboard.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import petTopia.repository.shop.OrderRepository;
import petTopia.repository.shop.ProductRepository;
import petTopia.repository.user.MemberRepository;
import petTopia.repository.vendor.VendorActivityRepository;
import petTopia.repository.vendor.VendorRepository;
import petTopia.service.dashboard.DashboardService;

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
        return orderRepository.countTotalOrders();
    }

    @Override
    public long getTotalMembers() {
        return memberRepository.countTotalMembers();
    }

    @Override
    public long getTotalProducts() {
        return productRepository.countTotalProducts();
    }

    @Override
    public long getTotalVendors() {
        return vendorRepository.countTotalVendors();
    }

    @Override
    public long getTotalActivities() {
        return vendorActivityRepository.countTotalActivities();
    }

    @Override
    public long getTotalRevenue() {
        return orderRepository.getTotalRevenue();
    }
}