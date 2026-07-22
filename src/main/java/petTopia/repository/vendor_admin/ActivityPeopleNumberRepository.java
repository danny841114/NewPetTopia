package petTopia.repository.vendor_admin;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import petTopia.model.vendor.ActivityPeopleNumber;

public interface ActivityPeopleNumberRepository extends JpaRepository<ActivityPeopleNumber, Integer> {
    Optional<ActivityPeopleNumber> findByVendorActivityId(Integer activityId);
}
