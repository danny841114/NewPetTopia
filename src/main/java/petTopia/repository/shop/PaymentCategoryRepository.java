package petTopia.repository.shop;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import petTopia.model.shop.PaymentCategory;

public interface PaymentCategoryRepository extends JpaRepository<PaymentCategory, java.lang.Integer> {
    @Query("SELECT p.name FROM PaymentCategory p")
    List<String> findAllPaymentCategory();

    Optional<PaymentCategory> findByName(String name);
}
