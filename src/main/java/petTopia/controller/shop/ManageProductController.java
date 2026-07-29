package petTopia.controller.shop;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import petTopia.dto.shop.ProductDto;
import petTopia.dto.shop.ProductDto2;
import petTopia.model.shop.Product;
import petTopia.model.shop.ProductDetail;
import petTopia.service.shop.ProductDetailService;
import petTopia.service.shop.ProductService;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/manage/shop/products")
public class ManageProductController {
    private final ProductService productService;
    private final ProductDetailService productDetailService;

    // 後台商品管理 => 查詢所有商品
    @GetMapping
    public ResponseEntity<?> getShopProducts(@RequestParam Integer start,
                                             @RequestParam Integer rows,
                                             @RequestParam Optional<String> keyword,
                                             @RequestParam Optional<String> category,
                                             @RequestParam Optional<String> status,
                                             @RequestParam Optional<String> isProductDiscount,
                                             @RequestParam Optional<String> stockQuantityLessThan,
                                             @RequestParam Optional<String> startDate,
                                             @RequestParam Optional<String> endDate) {
        Map<String, Object> responseBody = new HashMap<>();
        Map<String, Object> filterData = new HashMap<>();

        filterData.put("keyword", keyword.isPresent() ? keyword.get() : "");
        filterData.put("category", category.isPresent() ? category.get() : "");
        filterData.put("status", status.isPresent() ? status.get() : "");
        filterData.put("isProductDiscount", isProductDiscount.isPresent() ? isProductDiscount.get() : "");
        filterData.put("stockQuantityLessThan", stockQuantityLessThan.isPresent() ? stockQuantityLessThan.get() : "");
        filterData.put("startDate", startDate.isPresent() ? startDate.get() : "");
        filterData.put("endDate", endDate.isPresent() ? endDate.get() : "");
        filterData.put("start", start);
        filterData.put("rows", rows);

        // 獲取商品總數
        long count = productService.getProductsCount(filterData);
        responseBody.put("count", count);

        // 獲取商品
        List<Product> productList = productService.getProducts(filterData);
        responseBody.put("productList", productList);

        return ResponseEntity.ok(responseBody);
    }


    // TODO: fix logic
    // 後台商品管理 => 批量更新狀態
    @PutMapping("/api/updateProductsStatus")
    public ResponseEntity<?> updateProductsStatus(@RequestParam List<Integer> productIds,
                                                  @RequestParam Optional<String> batchStatus) {
        Map<String, Object> responseBody = new HashMap<>();
        String batchStatusStr = batchStatus.isPresent() ? batchStatus.get() : "";
        List<Product> productList = productService.updateProductsStatus(productIds, batchStatusStr);
        return ResponseEntity.ok(responseBody);
    }

    // 後台商品管理 => 新增商品
    @PostMapping("/api/insertProduct")
    public ResponseEntity<?> insertProduct(@RequestPart ProductDto product, @RequestPart MultipartFile photo) {
        Map<String, Object> responseBody = new HashMap<>();

        try {
            product.setPhoto(photo.getBytes());
        } catch (IOException e) {
            log.error("Get photo byte array failed", e);
        }

        if (productService.insertProduct(product)) responseBody.put("messages", "新增商品成功");
        else responseBody.put("messages", "同樣商品已存在");

        return ResponseEntity.ok(responseBody);
    }

    // 後台商品管理 => 修改商品
    @PostMapping("/api/modifyProduct")
    public ResponseEntity<?> modifyProduct(@RequestPart ProductDto2 product,
                                           @RequestPart(required = false) MultipartFile photo) {
        Map<String, Object> responseBody = new HashMap<>();

        if (photo != null && !photo.isEmpty()) {
            try {
                product.setPhoto(photo.getBytes());
            } catch (IOException e) {
                log.error("Get photo byte array failed", e);
            }
        } else {
            Product p = productService.findById(product.getId());
            product.setPhoto(p.getPhoto());
        }

        Product modifyProduct = productService.modifyProduct(product);

        if (modifyProduct != null) {
            responseBody.put("modifyProduct", modifyProduct);
            responseBody.put("messages", "修改商品成功");
        } else {
            responseBody.put("modifyProduct", null);
            responseBody.put("messages", "修改商品失敗");
        }

        return ResponseEntity.ok(responseBody);
    }

    // 後台商品管理 => 新增商品 => 如果有同名商品直接獲取Description
    @GetMapping("/api/insertProduct/getDescription")
    public ResponseEntity<?> getProductDetailDescription(@RequestParam String productDetailName) {
        Map<String, Object> responseBody = new HashMap<>();

        ProductDetail productDetail = productDetailService.findByProductDetailName(productDetailName);
        if (productDetail != null) responseBody.put("description", productDetail.getDescription());
        else responseBody.put("description", "");

        return ResponseEntity.ok(responseBody);
    }

    // 後台商品管理 => 修改商品 => 獲取商品
    @GetMapping("/api/modifyProduct/getProduct")
    public ResponseEntity<?> getProduct(@RequestParam Integer productId) {
        Product product = productService.findById(productId);
        return ResponseEntity.ok(product);
    }

    // 後台商品管理 => 修改商品 => 獲取商品照片
    @GetMapping("/api/modifyProduct/getProductPhoto")
    public ResponseEntity<?> getProductPhoto(@RequestParam Integer productId) {
        Product product = productService.findById(productId);

        byte[] photo = product.getPhoto();

        if (photo != null && photo.length != 0) {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(product);
        }

        return ResponseEntity.notFound().build();

    }

    // 後台商品管理 => 刪除商品
    @GetMapping("/api/deleteProduct")
    public ResponseEntity<?> deleteProduct(@RequestParam Integer productId) {
        Map<String, Object> responseBody = new HashMap<>();

        if (productService.deleteProduct(productId)) responseBody.put("messages", "刪除商品成功");
        else responseBody.put("messages", "刪除商品失敗");

        return ResponseEntity.ok(responseBody);
    }
}
