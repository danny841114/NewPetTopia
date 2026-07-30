package petTopia.repository.shop;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;
import petTopia.model.shop.Product;

public interface ProductRepository extends JpaRepository<Product, Integer>, ProductRepositoryCustom {
    Product findFirstByProductDetailIdOrderByIdAsc(Integer productDetailId);

    List<Product> findByProductDetailIdAndProductSizeId(Integer productDetailId, Integer productSizeId);

    List<Product> findByProductDetailIdAndProductColorId(Integer productDetailId, Integer productColorId);

    Product findByProductDetailIdAndProductSizeIdAndProductColorId(Integer productDetailId, Integer productSizeId, Integer productColorId);

    List<Product> findAllByIdIn(List<Integer> productIds);

    // 獲取有上架的商品
    List<Product> findByProductDetailIdAndStatus(Integer productDetailId, Boolean status);

    //購買商品更動庫存
    // 使用 @Lock 註解指定悲觀鎖
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Product p WHERE p.id = :productId")
    Product lockProduct(@Param("productId") Integer productId);
}
