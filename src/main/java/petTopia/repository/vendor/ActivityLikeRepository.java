package petTopia.repository.vendor;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import petTopia.model.user.Member;
import petTopia.model.vendor.ActivityLike;
import petTopia.model.vendor.VendorActivity;

public interface ActivityLikeRepository extends JpaRepository<ActivityLike, Integer> {
    Optional<ActivityLike> findByMemberIdAndVendorActivityId(Integer memberId, Integer activityId);

    List<ActivityLike> findByVendorActivity(VendorActivity vendorActivity);

    List<ActivityLike> findByMember(Member member);
}
