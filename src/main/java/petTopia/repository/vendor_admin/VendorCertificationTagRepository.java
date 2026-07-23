package petTopia.repository.vendor_admin;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import petTopia.model.vendor.VendorCertification;
import petTopia.model.vendor.VendorCertificationTag;

public interface VendorCertificationTagRepository extends JpaRepository<VendorCertificationTag, Integer> {
    boolean existsByVendorIdAndTagId(int vendorId, int certificationTagId);

    List<VendorCertificationTag> findByVendorId(Integer vendorId);

    List<VendorCertificationTag> findByVendorIdAndCertificationCertificationStatus(Integer vendorId, String status);

    @Query("SELECT vc.tag.tagName FROM VendorCertificationTag vc WHERE vc.vendor.id IN :vendorIds")
    List<String> findSlogansByVendorIds(@Param("vendorIds") List<Integer> vendorIds);

    @Query("""
            SELECT DISTINCT vct.tag.tagName
            FROM VendorCertificationTag vct
            WHERE vct.vendor.id = :vendorId
            AND vc.certificationStatus = '已認證'
            """)
    List<String> findCertifiedSlogansByVendorId(@Param("vendorId") Integer vendorId);

    List<VendorCertificationTag> findByCertification(VendorCertification certification);
}
