package petTopia.repository.shop;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import petTopia.model.shop.Coupon;

public interface CouponRepository extends JpaRepository<Coupon, Integer> {
    // 查詢某會員所有訂單中，每個優惠券的使用次數
    @Query("""
            SELECT o.coupon.id, COUNT(o) FROM Order o
            WHERE o.member.id = :memberId
            AND o.orderStatus.id != 6
            GROUP BY o.coupon.id
            """)
    List<Object[]> countCouponsUsageByMemberId(Integer memberId);

    // 按名稱搜尋優惠券（分頁）
    Page<Coupon> findByNameContaining(String name, Pageable pageable);
}
