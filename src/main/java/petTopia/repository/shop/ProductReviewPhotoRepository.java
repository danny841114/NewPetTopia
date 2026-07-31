package petTopia.repository.shop;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import petTopia.model.shop.ProductReviewPhoto;

public interface ProductReviewPhotoRepository extends JpaRepository<ProductReviewPhoto, Integer> {
    @Modifying
    @Query("DELETE FROM ProductReviewPhoto p WHERE p.id IN :ids")
    void deleteByIdIn(@Param("ids") List<Integer> ids);
}
