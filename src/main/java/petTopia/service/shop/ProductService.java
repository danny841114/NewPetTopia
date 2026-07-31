package petTopia.service.shop;

import java.io.IOException;
import java.util.*;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import petTopia.dto.shop.ProductDto;
import petTopia.dto.shop.ProductDto2;
import petTopia.dto.shop.request.OptionProductRequest;
import petTopia.dto.shop.request.ShopProductsRequest;
import petTopia.dto.shop.response.ProductDetailResponse;
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

    // TODO: Use JPQL
    public byte[] getPhotoByProductId(Integer productId) {
        return productRepository.findById(productId)
                .map(Product::getPhoto)
                .orElse(null);
    }

    public ProductDetailResponse getAvailableProductsByDetailId(Integer productDetailId) {
        List<Product> products = productRepository.findByProductDetailIdAndStatus(productDetailId, true);

        Integer totalStockQuantity = 0;

        for (Product product : products) {
            totalStockQuantity += product.getStockQuantity();
        }

        Product minPriceProduct = getMinPriceProduct(products);
        Product maxPriceProduct = getMaxPriceProduct(products);

        return ProductDetailResponse.builder()
                .productList(products)
                .minPriceProduct(minPriceProduct)
                .maxPriceProduct(maxPriceProduct)
                .totalStockQuantity(totalStockQuantity)
                .build();
    }

    public ProductDetailResponse getAvailableProducts(Integer productDetailId) {
        ProductDetail productDetail = productDetailRepository.findById(productDetailId).orElse(null);

        List<Product> products = productRepository.findByProductDetailIdAndStatus(productDetailId, true);

        List<ProductSize> productSizes = new ArrayList<>();
        List<ProductColor> productColors = new ArrayList<>();

        Integer totalStockQuantity = 0;

        for (Product product : products) {
            if (product.getProductSize() != null && !productSizes.contains(product.getProductSize())) {
                productSizes.add(product.getProductSize());
            }

            if (product.getProductColor() != null && !productColors.contains(product.getProductColor())) {
                productColors.add(product.getProductColor());
            }

            totalStockQuantity += product.getStockQuantity();
        }

        productSizes.sort(Comparator.comparingInt(ProductSize::getId));
        productColors.sort(Comparator.comparingInt(ProductColor::getId));

        Product minPriceProduct = getMinPriceProduct(products);
        Product maxPriceProduct = getMaxPriceProduct(products);

        return ProductDetailResponse.builder()
                .productList(products)
                .sizeList(productSizes)
                .colorList(productColors)
                .minPriceProduct(minPriceProduct)
                .maxPriceProduct(maxPriceProduct)
                .productDetail(productDetail)
                .totalStockQuantity(totalStockQuantity)
                .build();
    }

    public ProductDetailResponse getProductsByOption(OptionProductRequest request) {
        List<Product> products = switch (request.getOptionName()) {
            case "size" -> productRepository.findByProductDetailIdAndProductSizeId(
                    request.getProductDetailId(),
                    request.getOptionId()
            );

            case "color" -> productRepository.findByProductDetailIdAndProductColorId(
                    request.getProductDetailId(),
                    request.getOptionId()
            );

            default -> new ArrayList<>();
        };

        Integer totalStockQuantity = 0;

        for (Product product : products) {
            totalStockQuantity += product.getStockQuantity();
        }

        Product minPriceProduct = getMinPriceProduct(products);
        Product maxPriceProduct = getMaxPriceProduct(products);

        return ProductDetailResponse.builder()
                .productList(products)
                .minPriceProduct(minPriceProduct)
                .maxPriceProduct(maxPriceProduct)
                .totalStockQuantity(totalStockQuantity)
                .build();
    }

    public byte[] findFirstPhotoByProductDetailId(Integer productDetailId) {
        return productRepository.findFirstByProductDetailIdOrderByIdAsc(productDetailId)
                .map(Product::getPhoto)
                .orElse(null);
    }

    // 批量更新狀態
    @Transactional
    public Map<String, Object> updateProductsStatus(List<Integer> productIds, String batchStatus) {
        batchStatus = batchStatus != null ? batchStatus : "";
        boolean setBatchStatus = "1".equals(batchStatus);

        List<Product> productList = productRepository.findAllById(productIds);

        for (Product product : productList) {
            product.setStatus(setBatchStatus);
        }

        List<Product> modifiedProducts = productRepository.saveAll(productList);

        Map<String, Object> response = new HashMap<>();
        response.put("productList", modifiedProducts);

        return response;
    }

    // 新增商品
    @Transactional
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
        ProductCategory productCategory = productCategoryRepository.findByName(categoryName).orElse(null);

        // set ProductDetail
        ProductDetail productDetail;
        Optional<ProductDetail> optional = productDetailRepository.findByName(productDto.getProductDetail().getName());
        if (optional.isEmpty()) {
            productDetail = new ProductDetail();

            productDetail.setName(productDto.getProductDetail().getName());
            productDetail.setDescription(productDto.getProductDetail().getDescription());
            productDetail.setProductCategory(productCategory);

            productDetailRepository.save(productDetail);
        } else {
            productDetail = optional.get();

            productDetail.setDescription(productDto.getProductDetail().getDescription());

            productDetailRepository.save(productDetail);
        }

        // set ProductSize
        ProductSize productSize = null;
        if (productDto.getProductSize().getName() != null) {
            productSize = productSizeRepository.findByName(productDto.getProductSize().getName()).orElse(null);
        }
        if (productSize == null && productDto.getProductSize().getName() != null) {
            productSize = new ProductSize();
            productSize.setName(productDto.getProductSize().getName());

            productSizeRepository.save(productSize);
        }

        // set ProductColor
        ProductColor productColor = null;
        if (productDto.getProductColor().getName() != null) {
            productColor = productColorRepository.findByName(productDto.getProductColor().getName()).orElse(null);
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
        Optional<Product> existingProduct = productRepository.findByProductDetailIdAndProductSizeIdAndProductColorId(
                product.getProductDetail().getId(),
                product.getProductSize() != null ? product.getProductSize().getId() : null,
                product.getProductColor() != null ? product.getProductColor().getId() : null
        );

        Map<String, Object> response = new HashMap<>();

        // 商品已存在
        if (existingProduct.isPresent()) {
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
    @Transactional
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
            ProductCategory productCategory = productCategoryRepository.findByName(categoryName).orElse(null);

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
    @Transactional
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
    @Transactional
    public void updateStockLevelsByCartItems(List<Cart> cartItems) {
        for (Cart cart : cartItems) {
            if (cart.getProduct() == null) continue;

            // 呼叫 lockProduct 方法，自動加鎖
            Product product = productRepository.lockProduct(cart.getProduct().getId())
                    .orElseThrow(() -> new EntityNotFoundException("Product not found by ID"));

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

    private Product getMaxPriceProduct(List<Product> products) {
        return products.stream()
                .max(Comparator.comparing(
                        p -> p.getDiscountPrice() != null
                                ? p.getUnitPrice().min(p.getDiscountPrice())
                                : p.getUnitPrice(),
                        Comparator.naturalOrder()
                ))
                .orElse(null);
    }

    private Product getMinPriceProduct(List<Product> products) {
        return products.stream()
                .min(Comparator.comparing(
                        p -> p.getDiscountPrice() != null
                                ? p.getUnitPrice().min(p.getDiscountPrice())
                                : p.getUnitPrice(),
                        Comparator.naturalOrder()
                ))
                .orElse(null);
    }
}
