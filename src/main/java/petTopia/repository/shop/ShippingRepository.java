package petTopia.repository.shop;

import org.springframework.data.jpa.repository.JpaRepository;
import petTopia.model.shop.Shipping;

import java.util.Optional;

public interface ShippingRepository extends JpaRepository<Shipping, Integer> {
    Optional<Shipping> findByOrderId(Integer orderId);
}
