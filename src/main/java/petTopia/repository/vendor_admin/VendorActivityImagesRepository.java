package petTopia.repository.vendor_admin;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import petTopia.model.vendor.VendorActivity;
import petTopia.model.vendor.VendorActivityImages;

public interface VendorActivityImagesRepository extends JpaRepository<VendorActivityImages, Integer> {
    List<VendorActivityImages> findByVendorActivity(VendorActivity vendorActivity);
}
