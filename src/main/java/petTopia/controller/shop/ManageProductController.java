package petTopia.controller.shop;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import petTopia.dto.shop.ProductDto;
import petTopia.dto.shop.ProductDto2;
import petTopia.dto.shop.request.ShopProductsRequest;
import petTopia.dto.shop.response.ProductDetailResponse;
import petTopia.dto.shop.response.ShopProductsResponse;
import petTopia.model.shop.Product;
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
    public ResponseEntity<ShopProductsResponse> getShopProducts(@ModelAttribute ShopProductsRequest request) {
        ShopProductsResponse response = productService.queryProduct(request);
        return ResponseEntity.ok(response);
    }

    // TODO: check API response
    // 後台商品管理 => 批量更新狀態
    @PutMapping("/api/updateProductsStatus")
    public ResponseEntity<?> updateProductsStatus(@RequestParam List<Integer> productIds,
                                                  @RequestParam String batchStatus) {
        Map<String, Object> responseBody = new HashMap<>();
        List<Product> productList = productService.updateProductsStatus(productIds, batchStatus);
        responseBody.put("productList", productList);
        return ResponseEntity.ok(responseBody);
    }

    // 後台商品管理 => 新增商品
    @PostMapping("/api/insertProduct")
    public ResponseEntity<?> insertProduct(@RequestPart ProductDto product, @RequestPart MultipartFile photo) {
        Map<String, Object> response = productService.insertProduct(product, photo);
        return ResponseEntity.ok(response);
    }

    // 後台商品管理 => 修改商品
    @PostMapping("/api/modifyProduct")
    public ResponseEntity<?> modifyProduct(@RequestPart ProductDto2 product,
                                           @RequestPart(required = false) MultipartFile photo) {
        Map<String, Object> responseBody = productService.modifyProduct(product, photo);
        return ResponseEntity.ok(responseBody);
    }

    // 後台商品管理 => 新增商品 => 如果有同名商品直接獲取Description
    @GetMapping("/api/insertProduct/getDescription")
    public ResponseEntity<?> getProductDetailDescription(@RequestParam String productDetailName) {
        ProductDetailResponse response = productDetailService.findByProductDetailName(productDetailName);
        return ResponseEntity.ok(response);
    }

    // 後台商品管理 => 修改商品 => 獲取商品
    @GetMapping("/api/modifyProduct/getProduct")
    public ResponseEntity<?> getProduct(@RequestParam Integer productId) {
        Product product = productService.findById(productId);
        return ResponseEntity.ok(product);
    }

    // 後台商品管理 => 修改商品 => 獲取商品照片
    @GetMapping("/api/modifyProduct/getProductPhoto")
    public ResponseEntity<byte[]> getProductPhoto(@RequestParam Integer productId) {
        byte[] photo = productService.getPhotoByProductId(productId);

        if (photo == null || photo.length == 0) {
            return ResponseEntity.notFound().build();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);

        return ResponseEntity.ok()
                .headers(headers)
                .body(photo);
    }

    // 後台商品管理 => 刪除商品
    @GetMapping("/api/deleteProduct")
    public ResponseEntity<?> deleteProduct(@RequestParam Integer productId) {
        Map<String, Object> response = productService.deleteProduct(productId);
        return ResponseEntity.ok(response);
    }
}
