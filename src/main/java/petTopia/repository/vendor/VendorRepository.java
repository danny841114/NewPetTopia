package petTopia.repository.vendor;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import petTopia.model.vendor.Vendor;

public interface VendorRepository extends JpaRepository<Vendor, Integer> {
    List<Vendor> findByVendorCategoryId(Integer vendorCategoryId);

    List<Vendor> findByNameContainingOrDescriptionContaining(String name, String description);

    @Modifying
    @Query("UPDATE Vendor v SET v.status = :status WHERE v.id IN :vendorIds")
    void updateVendorStatusByIds(List<Integer> vendorIds, boolean status);

    @Modifying
    @Query("UPDATE Vendor v SET v.status = :status")
    void updateAllVendorStatus(boolean status);

    List<Vendor> findByStatus(boolean status);

    List<Vendor> findByVendorCategoryIdAndStatus(Integer categoryId, boolean status);

    Optional<Vendor> findByUserId(Integer userId);

    // 使用LEFT JOIN查詢，確保即使某些欄位為null也能查到
    @Query("SELECT v FROM Vendor v LEFT JOIN v.user u WHERE u.id = :userId")
    Optional<Vendor> findByUserIdWithJoin(@Param("userId") Integer userId);

    // 根據email查找商家，使用LEFT JOIN確保即使某些欄位為null也能查到
    @Query("SELECT v FROM Vendor v LEFT JOIN v.user u WHERE u.email = :email")
    Vendor findByEmail(@Param("email") String email);

    Optional<Vendor> findStatusById(Integer vendorId);
}
