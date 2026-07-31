package petTopia.repository.shop;

import org.springframework.data.jpa.repository.JpaRepository;
import petTopia.model.shop.ProductCategory;

import java.util.Optional;

public interface ProductCategoryRepository extends JpaRepository<ProductCategory, Integer> {
    Optional<ProductCategory> findByName(String name);
}
