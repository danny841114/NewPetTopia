package petTopia.controller.shop;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import petTopia.dto.shop.request.ShopProductsRequest;
import petTopia.dto.shop.response.ShopProductResponse;
import petTopia.service.shop.ProductDetailService;
import petTopia.service.shop.ProductService;
import petTopia.util.HeadersUtil;

@RequiredArgsConstructor
@RestController
@RequestMapping("/shop/products")
public class ShopProductsController {
    private final ProductService productService;
    private final ProductDetailService productDetailService;

    // 商品瀏覽頁面 vue
    @GetMapping
    public ResponseEntity<ShopProductResponse> getShopProducts(@ModelAttribute ShopProductsRequest request) {
        ShopProductResponse response = productDetailService.getFilteredProducts(request);
        return ResponseEntity.ok(response);
    }

    // 商品瀏覽頁面 => 獲取商品資訊的第一個商品的圖片
    @GetMapping("/api/getPhoto")
    public ResponseEntity<byte[]> getProductPhoto(@RequestParam Integer productDetailId) {
        byte[] photo = productService.findFirstPhotoByProductDetailId(productDetailId);

        if (photo == null || photo.length == 0) return ResponseEntity.notFound().build();

        return ResponseEntity.ok()
                .headers(HeadersUtil.createHeadersWithMediaTypeJpg())
                .body(photo);
    }
}
