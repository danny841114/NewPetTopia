package petTopia.repository.shop;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import petTopia.model.shop.OrderStatus;

public interface OrderStatusRepository extends JpaRepository<OrderStatus, Integer> {
    @Query("SELECT os.name FROM OrderStatus os")
    List<String> findAllOrderStatus();

    Optional<OrderStatus> findByName(String name);
}
