package petTopia.service.admin;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import petTopia.dto.admin.GetCouponsRequest;
import petTopia.model.shop.Coupon;
import petTopia.repository.shop.CouponRepository;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class CouponAdminService {
    private final CouponRepository couponRepository;

    public Page<Coupon> getCoupons(GetCouponsRequest request) {
        PageRequest pageRequest = PageRequest.of(
                request.getPage(),
                request.getSize(),
                Sort.by("id").descending()
        );

        Page<Coupon> couponPage;

        if (request.getKeyword() != null && !request.getKeyword().isBlank()) {
            couponPage = couponRepository.findByNameContaining(request.getKeyword(), pageRequest);
        } else {
            couponPage = couponRepository.findAll(pageRequest);
        }

        return couponPage;
    }

    public Coupon getCoupon(Integer id) {
        return couponRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Coupon not found"));
    }

    @Transactional
    public Coupon createCoupon(Coupon coupon) {
        return couponRepository.save(coupon);
    }

    @Transactional
    public Coupon updateCoupon(Integer id, Coupon coupon) {
        if (!couponRepository.existsById(id)) throw new EntityNotFoundException("Coupon not found");

        coupon.setId(id);

        return couponRepository.save(coupon);
    }

    @Transactional
    public void deleteCoupon(Integer id) {
        couponRepository.findById(id)
                .ifPresent(couponRepository::delete);
    }
}
