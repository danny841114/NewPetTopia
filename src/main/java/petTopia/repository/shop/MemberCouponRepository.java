package petTopia.repository.shop;

import org.springframework.data.jpa.repository.JpaRepository;
import petTopia.model.shop.MemberCoupon;
import petTopia.model.shop.MemberCouponId;

import java.util.List;

public interface MemberCouponRepository extends JpaRepository<MemberCoupon, MemberCouponId> {
    List<MemberCoupon> findByMemberId(Integer memberId);
}
