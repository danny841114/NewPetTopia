package petTopia.repository.shop;

import org.springframework.data.jpa.repository.JpaRepository;
import petTopia.model.shop.ProductDetail;

import java.util.Optional;

public interface ProductDetailRepository extends JpaRepository<ProductDetail, Integer>, ProductDetailRepositoryCustom {
    Optional<ProductDetail> findByName(String name);
}
