package petTopia.repository.shop;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import petTopia.model.shop.ShippingCategory;

public interface ShippingCategoryRepository extends JpaRepository<ShippingCategory, Integer> {
    @Query("SELECT s.name FROM ShippingCategory s")
    List<String> findAllShippingCategoryNames();

    Optional<ShippingCategory> findByName(String name);
}
