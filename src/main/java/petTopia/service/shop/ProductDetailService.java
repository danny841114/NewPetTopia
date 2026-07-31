package petTopia.service.shop;

import java.util.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import petTopia.dto.shop.ProductDetailDto;
import petTopia.dto.shop.request.ShopProductsRequest;
import petTopia.dto.shop.response.ProductDetailDescription;
import petTopia.model.shop.Product;
import petTopia.model.shop.ProductDetail;
import petTopia.repository.shop.ProductDetailRepository;
import petTopia.repository.shop.ProductRepository;
import petTopia.repository.shop.ProductReviewRepository;

@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class ProductDetailService {
    private final ProductRepository productRepository;
    private final ProductDetailRepository productDetailRepository;
    private final ProductReviewRepository productReviewRepository;

    public Map<String, Object> getFilteredProducts(ShopProductsRequest request) {
        Map<String, Object> filterData = new HashMap<>();

        filterData.put("category", request.getCategory());
        filterData.put("keyword", request.getKeyword() != null ? request.getKeyword() : "");
        filterData.put("start", request.getStart());
        filterData.put("rows", request.getRows());

        List<ProductDetail> productDetailList = this.getProducts(filterData);

        List<ProductDetailDto> productDetailDtoList = new ArrayList<>();

        for (ProductDetail productDetail : productDetailList) {
            ProductDetailDto productDetailDto = new ProductDetailDto();

            List<Product> productList = productRepository.findByProductDetailIdAndStatus(productDetail.getId(), true);

            productDetailDto.setMinPriceProduct(getMinPriceProduct(productList));
            productDetailDto.setProductDetail(productDetail);
            productDetailDto.setAvgRating(productReviewRepository.findAverageRatingByProductDetailId(productDetail.getId()));

            productDetailDtoList.add(productDetailDto);
        }

        Long count = this.getProductsCount(filterData);

        Map<String, Object> responseBody = new HashMap<>();

        responseBody.put("count", count);
        responseBody.put("productDetailDtoList", productDetailDtoList);

        return responseBody;
    }

    public ProductDetailDescription findByProductDetailName(String productDetailName) {
        Optional<ProductDetail> optional = productDetailRepository.findByName(productDetailName);
        String description = optional.isPresent() ? optional.get().getDescription() : "";
        return ProductDetailDescription.builder()
                .description(description)
                .build();
    }

    private Product getMinPriceProduct(List<Product> products) {
        return products.stream()
                .min(Comparator.comparing(p -> p.getDiscountPrice() != null
                                ? p.getUnitPrice().min(p.getDiscountPrice())
                                : p.getUnitPrice(),
                        Comparator.naturalOrder()
                ))
                .orElse(null);
    }

    // 根據條件搜尋商品的總數
    private Long getProductsCount(Map<String, Object> filterData) {
        try {
            JSONObject jsonObj = new JSONObject(filterData);
            return productDetailRepository.count(jsonObj);
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
    }

    // 根據條件搜尋商品
    private List<ProductDetail> getProducts(Map<String, Object> filterData) {
        try {
            JSONObject jsonObj = new JSONObject(filterData);
            return productDetailRepository.find(jsonObj);
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ArrayList<>();
        }
    }
}
