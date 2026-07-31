package petTopia.repository.shop;

import java.util.List;
import java.util.Optional;

import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;
import petTopia.model.shop.Product;

public interface ProductRepository extends JpaRepository<Product, Integer>, ProductRepositoryCustom {
    Optional<Product> findFirstByProductDetailIdOrderByIdAsc(Integer productDetailId);

    List<Product> findByProductDetailIdAndProductSizeId(Integer productDetailId, Integer productSizeId);

    List<Product> findByProductDetailIdAndProductColorId(Integer productDetailId, Integer productColorId);

    Optional<Product> findByProductDetailIdAndProductSizeIdAndProductColorId(Integer productDetailId, Integer productSizeId, Integer productColorId);

    // 獲取有上架的商品
    List<Product> findByProductDetailIdAndStatus(Integer productDetailId, Boolean status);

    // 購買商品更動庫存
    // 使用 @Lock 註解指定悲觀鎖
    // 等待最多 3 秒，否則拋出 TimeoutException
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({@QueryHint(name = "jakarta.persistence.lock.timeout", value = "3000")})
    @Query("SELECT p FROM Product p WHERE p.id = :productId")
    Optional<Product> lockProduct(@Param("productId") Integer productId);
}
