package petTopia.service.shop;

import java.io.IOException;
import java.util.*;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import petTopia.dto.shop.ProductDto;
import petTopia.dto.shop.ProductDto2;
import petTopia.dto.shop.request.ShopProductsRequest;
import petTopia.dto.shop.response.ShopProductsResponse;
import petTopia.model.shop.Cart;
import petTopia.model.shop.Product;
import petTopia.model.shop.ProductCategory;
import petTopia.model.shop.ProductColor;
import petTopia.model.shop.ProductDetail;
import petTopia.model.shop.ProductSize;
import petTopia.repository.shop.ProductCategoryRepository;
import petTopia.repository.shop.ProductColorRepository;
import petTopia.repository.shop.ProductDetailRepository;
import petTopia.repository.shop.ProductRepository;
import petTopia.repository.shop.ProductSizeRepository;

@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductDetailRepository productDetailRepository;
    private final ProductCategoryRepository productCategoryRepository;
    private final ProductColorRepository productColorRepository;
    private final ProductSizeRepository productSizeRepository;

    public Product findById(Integer productId) {
        return productRepository.findById(productId).orElse(null);
    }

    public byte[] getPhotoByProductId(Integer productId) {
        return productRepository.findById(productId)
                .map(Product::getPhoto)
                .orElse(null);
    }

    // 獲取有上架的商品
    public List<Product> getAvailableProductByProductDetailId(Integer productDetailId, Boolean status) {
        List<Product> productList = productRepository.findByProductDetailIdAndStatus(productDetailId, status);
        return productList.isEmpty() ? null : productList;
    }

    public List<Product> findByProductDetailIdAndSizeId(Integer productDetailId, Integer productSizeId) {
        List<Product> productList = productRepository.findByProductDetailIdAndProductSizeId(productDetailId, productSizeId);
        return productList.isEmpty() ? null : productList;
    }

    public List<Product> findByProductDetailIdAndColorId(Integer productDetailId, Integer productColorId) {
        List<Product> productList = productRepository.findByProductDetailIdAndProductColorId(productDetailId, productColorId);
        return productList.isEmpty() ? null : productList;
    }

    public Product getConfirmProduct(Integer productDetailId, Integer productSizeId, Integer productColorId) {
        return productRepository.findByProductDetailIdAndProductSizeIdAndProductColorId(productDetailId, productSizeId, productColorId);
    }

    public Product findFirstByProductDetailId(Integer productDetailId) {
        return productRepository.findFirstByProductDetailIdOrderByIdAsc(productDetailId);
    }

    // 批量更新狀態
    public List<Product> updateProductsStatus(List<Integer> productIds, String batchStatus) {
        batchStatus = batchStatus != null ? batchStatus : "";

        List<Product> productList = productRepository.findAllByIdIn(productIds);

        for (Product product : productList) {
            boolean setBatchStatus = "1".equals(batchStatus);
            product.setStatus(setBatchStatus);

            productRepository.save(product);
        }

        return productList;
    }

    // 新增商品
    public Map<String, Object> insertProduct(ProductDto productDto, MultipartFile photo) {
        try {
            productDto.setPhoto(photo.getBytes());
        } catch (IOException e) {
            log.error("Get photo byte array failed", e);
        }

        if (productDto.getProductSize().getName() == null || productDto.getProductSize().getName().isEmpty())
            productDto.getProductSize().setName(null);
        if (productDto.getProductColor().getName() == null || productDto.getProductColor().getName().isEmpty())
            productDto.getProductColor().setName(null);


        Product product = new Product();

        // find ProductCategory
        String categoryName = productDto.getProductDetail().getProductCategory().getName();
        ProductCategory productCategory = productCategoryRepository.findByName(categoryName);

        // set ProductDetail
        ProductDetail productDetail = productDetailRepository.findByName(productDto.getProductDetail().getName());
        if (productDetail == null) {
            productDetail = new ProductDetail();

            productDetail.setName(productDto.getProductDetail().getName());
            productDetail.setDescription(productDto.getProductDetail().getDescription());
            productDetail.setProductCategory(productCategory);

            productDetailRepository.save(productDetail);
        } else {
            productDetail.setDescription(productDto.getProductDetail().getDescription());

            productDetailRepository.save(productDetail);
        }

        // set ProductSize
        ProductSize productSize = null;
        if (productDto.getProductSize().getName() != null) {
            productSize = productSizeRepository.findByName(productDto.getProductSize().getName());
        }
        if (productSize == null && productDto.getProductSize().getName() != null) {
            productSize = new ProductSize();
            productSize.setName(productDto.getProductSize().getName());

            productSizeRepository.save(productSize);
        }

        // set ProductColor
        ProductColor productColor = null;
        if (productDto.getProductColor().getName() != null) {
            productColor = productColorRepository.findByName(productDto.getProductColor().getName());
        }

        if (productColor == null && productDto.getProductColor().getName() != null) {
            productColor = new ProductColor();
            productColor.setName(productDto.getProductColor().getName());

            productColorRepository.save(productColor);
        }

        product.setProductDetail(productDetail);
        product.getProductDetail().setProductCategory(productCategory);
        product.setProductSize(productSize);
        product.setProductColor(productColor);

        // 檢查同個商品是否存在 
        Product existingProduct = productRepository.findByProductDetailIdAndProductSizeIdAndProductColorId(
                product.getProductDetail().getId(),
                product.getProductSize() != null ? product.getProductSize().getId() : null,
                product.getProductColor() != null ? product.getProductColor().getId() : null
        );

        Map<String, Object> response = new HashMap<>();

        // 商品已存在
        if (existingProduct != null) {
            response.put("messages", "同樣商品已存在");
            return response;
        }

        product.setUnitPrice(productDto.getUnitPrice());
        product.setDiscountPrice(productDto.getDiscountPrice());
        product.setStockQuantity(productDto.getStockQuantity());
        product.setStatus(productDto.getStatus() == 1);
        product.setPhoto(productDto.getPhoto());

        productRepository.save(product);

        response.put("messages", "新增商品成功");
        return response;
    }

    // 修改商品
    public Map<String, Object> modifyProduct(ProductDto2 productDto, MultipartFile photo) {
        Map<String, Object> response = new HashMap<>();

        try {
            if (photo != null && !photo.isEmpty()) {
                try {
                    productDto.setPhoto(photo.getBytes());
                } catch (IOException e) {
                    log.error("Get photo byte array failed", e);
                }
            } else {
                Product p = this.findById(productDto.getId());
                productDto.setPhoto(p.getPhoto());
            }

            if (productDto.getProductSize().getName() == null || productDto.getProductSize().getName().isEmpty())
                productDto.getProductSize().setName(null);

            if (productDto.getProductColor().getName() == null || productDto.getProductColor().getName().isEmpty())
                productDto.getProductColor().setName(null);

            Product product = productRepository.findById(productDto.getId())
                    .orElseThrow(() -> new EntityNotFoundException("Product not found"));

            // find ProductCategory
            String categoryName = productDto.getProductDetail().getProductCategory().getName();
            ProductCategory productCategory = productCategoryRepository.findByName(categoryName);

            // set ProductDetail
            ProductDetail productDetail = product.getProductDetail();

            productDetail.setDescription(productDto.getProductDetail().getDescription());
            productDetail.setProductCategory(productCategory);
            product.setProductDetail(productDetail);
            product.setUnitPrice(productDto.getUnitPrice());
            product.setDiscountPrice(productDto.getDiscountPrice());
            product.setStockQuantity(productDto.getStockQuantity());
            product.setStatus(productDto.getStatus() == 1);
            product.setPhoto(productDto.getPhoto());

            Product modifiedProduct = productRepository.save(product);

            // TODO: Should return DTO
            response.put("modifyProduct", modifiedProduct);
            response.put("messages", "修改商品成功");
        } catch (Exception e) {
            response.put("modifyProduct", null);
            response.put("messages", "修改商品失敗");
        }

        return response;
    }

    // 刪除商品
    public Map<String, Object> deleteProduct(Integer productId) {
        Map<String, Object> response = new HashMap<>();

        Optional<Product> productOpt = productRepository.findById(productId);

        if (productOpt.isPresent()) {
            Product product = productOpt.get();
            productRepository.delete(product);

            response.put("messages", "刪除商品成功");
            return response;
        }

        response.put("messages", "刪除商品失敗");
        return response;
    }

    // 更新庫存數量
    public void updateStockLevelsByCartItems(List<Cart> cartItems) {
        for (Cart cart : cartItems) {
            // 呼叫 lockProduct 方法，自動加鎖
            Product product = productRepository.lockProduct(cart.getProduct().getId());

            int quantity = cart.getQuantity();

            // 檢查庫存是否足夠
            if (product.getStockQuantity() < quantity) {
                throw new RuntimeException("商品[" + product.getProductDetail().getName() + "] 庫存不足");
            }

            // 扣庫存
            product.setStockQuantity(product.getStockQuantity() - quantity);

            productRepository.save(product);
        }
    }

    public ShopProductsResponse queryProduct(ShopProductsRequest request) {
        String keyword = request.getKeyword();
        String category = request.getCategory();
        String status = request.getStatus();
        String isProductDiscount = request.getIsProductDiscount();
        String stockQuantityLessThan = request.getStockQuantityLessThan();
        String startDate = request.getStartDate();
        String endDate = request.getEndDate();

        Map<String, Object> filterData = new HashMap<>();

        filterData.put("keyword", keyword != null ? keyword : "");
        filterData.put("category", category != null ? category : "");
        filterData.put("status", status != null ? status : "");
        filterData.put("isProductDiscount", isProductDiscount != null ? isProductDiscount : "");
        filterData.put("stockQuantityLessThan", stockQuantityLessThan != null ? stockQuantityLessThan : "");
        filterData.put("startDate", startDate != null ? startDate : "");
        filterData.put("endDate", endDate != null ? endDate : "");
        filterData.put("start", request.getStart());
        filterData.put("rows", request.getRows());

        Long count = 0L;
        List<Product> products = new ArrayList<>();

        try {
            JSONObject jsonObj = new JSONObject(filterData);

            count = productRepository.count(jsonObj);
            products = productRepository.find(jsonObj);
        } catch (Exception e) {
            log.error("Convert to JSON object failed", e);
        }

        return ShopProductsResponse.builder()
                .count(count)
                .productList(products)
                .build();
    }
}
