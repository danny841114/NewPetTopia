package petTopia.repository.vendor;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import petTopia.model.vendor.VendorLike;

public interface VendorLikeRepository extends JpaRepository<VendorLike, Integer> {
    /* 尋找單一會員對單一店家是否有收藏 */
    Optional<VendorLike> findByMemberIdAndVendorId(Integer memberId, Integer vendorId);

    /* 尋找單一店家被哪些會員收藏 */
    List<VendorLike> findByVendorId(Integer vendorId);

    /* 尋找單一店家被哪些會員收藏 */
    List<VendorLike> findByMemberId(Integer memberId);
}
