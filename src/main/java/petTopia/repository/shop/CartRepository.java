package petTopia.repository.shop;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.transaction.Transactional;
import petTopia.model.shop.Cart;

public interface CartRepository extends JpaRepository<Cart, Integer> {
    List<Cart> findByMemberId(Integer memberId);  // 根據使用者ID查找購物車

    // 自定義刪除方法，根據 memberId 刪除購物車內容
    @Modifying
    @Transactional
    @Query("DELETE FROM Cart c WHERE c.member.id = :memberId AND c.product.id IN :productIds")
    void deleteByMemberIdAndProductIds(@Param("memberId") Integer memberId, @Param("productIds") List<Integer> productIds);

    Optional<Cart> findByMemberIdAndProductId(Integer memberId, Integer productId);

    List<Cart> findByMemberIdAndProductIdIn(Integer memberId, List<Integer> productIds);

    Integer countByMemberId(Integer memberId);
}
