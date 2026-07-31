package petTopia.repository.shop;

import org.springframework.data.jpa.repository.JpaRepository;
import petTopia.model.shop.ProductSize;

import java.util.Optional;

public interface ProductSizeRepository extends JpaRepository<ProductSize, Integer> {
    Optional<ProductSize> findByName(String name);
}
