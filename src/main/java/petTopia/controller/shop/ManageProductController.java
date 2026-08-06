package petTopia.controller.shop;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import petTopia.dto.shop.ProductDto;
import petTopia.dto.shop.ProductDto2;
import petTopia.dto.shop.request.ShopProductsRequest;
import petTopia.dto.shop.response.ProductDetailDescription;
import petTopia.dto.shop.response.ShopProductsResponse;
import petTopia.model.shop.Product;
import petTopia.service.shop.ProductDetailService;
import petTopia.service.shop.ProductService;
import petTopia.util.HeadersUtil;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/manage/shop/products")
public class ManageProductController {
    private final ProductService productService;
    private final ProductDetailService productDetailService;

    // 後台商品管理 => 查詢所有商品
    @GetMapping
    public ResponseEntity<ShopProductsResponse> getShopProducts(@ModelAttribute ShopProductsRequest request) {
        ShopProductsResponse response = productService.queryProduct(request);
        return ResponseEntity.ok(response);
    }

    // 後台商品管理 => 批量更新狀態
    @PutMapping("/api/updateProductsStatus")
    public ResponseEntity<Map<String, Object>> updateProductsStatus(@RequestParam List<Integer> productIds,
                                                                    @RequestParam String batchStatus) {
        List<Product> modifiedProducts = productService.updateProductsStatus(productIds, batchStatus);

        Map<String, Object> response = new HashMap<>();
        response.put("productList", modifiedProducts);

        return ResponseEntity.ok(response);
    }

    // 後台商品管理 => 新增商品
    @PostMapping("/api/insertProduct")
    public ResponseEntity<Map<String, Object>> insertProduct(@RequestPart ProductDto product,
                                                             @RequestPart MultipartFile photo) {
        Product savedProduct = productService.insertProduct(product, photo);

        Map<String, Object> response = new HashMap<>();

        if (savedProduct == null) {
            response.put("messages", "同樣商品已存在");
            return ResponseEntity.badRequest().body(response);
        } else {
            response.put("messages", "新增商品成功");
            return ResponseEntity.ok(response);
        }
    }

    // 後台商品管理 => 修改商品
    @PostMapping("/api/modifyProduct")
    public ResponseEntity<Map<String, Object>> modifyProduct(@RequestPart ProductDto2 product,
                                                             @RequestPart(required = false) MultipartFile photo) {
        Product modifiedProduct = productService.modifyProduct(product, photo);

        Map<String, Object> response = new HashMap<>();

        if (modifiedProduct == null) {
            response.put("modifyProduct", null);
            response.put("messages", "修改商品失敗");
            return ResponseEntity.badRequest().body(response);
        } else {
            response.put("modifyProduct", modifiedProduct);
            response.put("messages", "修改商品成功");
            return ResponseEntity.ok(response);
        }
    }

    // 後台商品管理 => 新增商品 => 如果有同名商品直接獲取Description
    @GetMapping("/api/insertProduct/getDescription")
    public ResponseEntity<ProductDetailDescription> getProductDetailDescription(@RequestParam String productDetailName) {
        ProductDetailDescription response = productDetailService.findByProductDetailName(productDetailName);
        return ResponseEntity.ok(response);
    }

    // 後台商品管理 => 修改商品 => 獲取商品
    @GetMapping("/api/modifyProduct/getProduct")
    public ResponseEntity<Product> getProduct(@RequestParam Integer productId) {
        Product product = productService.findById(productId);
        return ResponseEntity.ok(product);
    }

    // 後台商品管理 => 修改商品 => 獲取商品照片
    @GetMapping("/api/modifyProduct/getProductPhoto")
    public ResponseEntity<byte[]> getProductPhoto(@RequestParam Integer productId) {
        byte[] photo = productService.getPhotoByProductId(productId);

        if (photo == null || photo.length == 0) return ResponseEntity.notFound().build();

        return ResponseEntity.ok()
                .headers(HeadersUtil.createHeadersWithMediaTypeJpg())
                .body(photo);
    }

    // 後台商品管理 => 刪除商品
    @GetMapping("/api/deleteProduct")
    public ResponseEntity<Map<String, Object>> deleteProduct(@RequestParam Integer productId) {
        Boolean isDeleted = productService.deleteProduct(productId);

        Map<String, Object> response = new HashMap<>();

        if (isDeleted) {
            response.put("messages", "刪除商品成功");
            return ResponseEntity.ok(response);
        } else {
            response.put("messages", "刪除商品失敗");
            return ResponseEntity.badRequest().body(response);
        }
    }
}
