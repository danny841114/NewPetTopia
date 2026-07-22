package petTopia.repository.vendor;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import petTopia.model.vendor.FriendlyShop;
import petTopia.model.vendor.Vendor;
import petTopia.model.vendor.VendorCategory;

public interface FriendlyShopRepository extends JpaRepository<FriendlyShop, Integer> {
    Optional<FriendlyShop> findFirstByVendor(Vendor vendor);

    List<FriendlyShop> findByNameContaining(String name);

    List<FriendlyShop> findByVendor(Vendor vendor);

    List<FriendlyShop> findByVendorCategory(VendorCategory vendorCategory);
}
