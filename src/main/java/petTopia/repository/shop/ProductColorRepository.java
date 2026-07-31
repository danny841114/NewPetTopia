package petTopia.repository.shop;

import org.springframework.data.jpa.repository.JpaRepository;
import petTopia.model.shop.ProductColor;

import java.util.Optional;

public interface ProductColorRepository extends JpaRepository<ProductColor, Integer> {
    Optional<ProductColor> findByName(String name);
}
