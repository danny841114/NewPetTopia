package petTopia.service.shop;

import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import petTopia.dto.shop.response.ProductDetailResponse;
import petTopia.model.shop.ProductDetail;
import petTopia.repository.shop.ProductDetailRepository;

@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class ProductDetailService {
    private final ProductDetailRepository productDetailRepository;

    public List<ProductDetail> findAll() {
        List<ProductDetail> allProductDetail = productDetailRepository.findAll();
        return allProductDetail.isEmpty() ? null : allProductDetail;
    }

    public ProductDetail findByProductDetailId(Integer productDetailId) {
        return productDetailRepository.findById(productDetailId).orElse(null);
    }

    // 根據條件搜尋商品的總數
    public Long getProductsCount(Map<String, Object> filterData) {
        try {
            JSONObject jsonObj = new JSONObject(filterData);
            return productDetailRepository.count(jsonObj);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }

    // 根據條件搜尋商品
    public List<ProductDetail> getProducts(Map<String, Object> filterData) {
        try {
            JSONObject jsonObj = new JSONObject(filterData);
            return productDetailRepository.find(jsonObj);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }

    // 關鍵字搜尋商品(舊版)
    public List<ProductDetail> searchProductByKeywords(String keywordString) {
        List<ProductDetail> productDetailList = productDetailRepository.searchProducts(keywordString);
        return productDetailList.isEmpty() ? null : productDetailList;
    }

    public ProductDetailResponse findByProductDetailName(String productDetailName) {
        ProductDetail productDetail = productDetailRepository.findByName(productDetailName);
        String description = productDetail != null ? productDetail.getDescription() : "";
        return ProductDetailResponse.builder()
                .description(description)
                .build();
    }
}
