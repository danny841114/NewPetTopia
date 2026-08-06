package petTopia.repository.vendor;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import petTopia.model.vendor.VendorLike;

public interface VendorLikeRepository extends JpaRepository<VendorLike, Integer> {
    Optional<VendorLike> findByMemberIdAndVendorId(Integer memberId, Integer vendorId);

    List<VendorLike> findByVendorId(Integer vendorId);

    List<VendorLike> findByMemberId(Integer memberId);

    void deleteByMemberIdAndVendorId(Integer memberId, Integer vendorId);
}
