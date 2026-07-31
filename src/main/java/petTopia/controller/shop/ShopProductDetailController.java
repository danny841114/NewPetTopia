package petTopia.controller.shop;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import petTopia.dto.shop.request.AddProductToCartRequest;
import petTopia.dto.shop.request.ConfirmProductRequest;
import petTopia.dto.shop.request.OptionProductRequest;
import petTopia.dto.shop.response.ConfirmedProduct;
import petTopia.dto.shop.response.ProductDetailResponse;
import petTopia.model.shop.Cart;
import petTopia.service.shop.CartService;
import petTopia.service.shop.ProductService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/shop/productDetail")
public class ShopProductDetailController {
    private final ProductService productService;
    private final CartService cartService;

    // 商品詳情頁面 vue
    @GetMapping
    public ResponseEntity<ProductDetailResponse> showShopProductDetail(@RequestParam Integer productDetailId) {
        ProductDetailResponse response = productService.getAvailableProducts(productDetailId);
        return ResponseEntity.ok(response);
    }

    // TODO: Similar API exists
    // 商品詳情頁面 => 獲取商品資訊的圖片
    @GetMapping("/api/getPhoto")
    public ResponseEntity<byte[]> getProductPhoto(@RequestParam Integer productId) {
        byte[] photo = productService.getPhotoByProductId(productId);

        if (photo == null || photo.length != 0) return ResponseEntity.notFound().build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);

        return ResponseEntity.ok()
                .headers(headers)
                .body(photo);
    }

    // TODO: change to GetMapping
    // 商品詳情頁面 => 獲取確認商品規格的Product & 獲取會員購物車該商品的數量
    @PostMapping("/api/getConfirmProductByDetailIdSizeIdColorId")
    public ResponseEntity<ConfirmedProduct> getConfirmProduct(@ModelAttribute ConfirmProductRequest request) {
        ConfirmedProduct response = cartService.getCartByMemberAndProductRelatedData(request);
        return ResponseEntity.ok(response);
    }

    // TODO: change to GetMapping
    // 商品詳情頁面 => 選擇一個規格後篩選Product vue
    @PostMapping("/api/getProductByOption")
    public ResponseEntity<ProductDetailResponse> getProductsByOption(@ModelAttribute OptionProductRequest request) {
        ProductDetailResponse response = productService.getProductsByOption(request);
        return ResponseEntity.ok(response);
    }

    // 商品詳情頁面 => 取消所有規格選項後重新獲得同商品詳情的Product
    @GetMapping("/api/getProductByProductDetailId")
    public ResponseEntity<ProductDetailResponse> getProductByProductDetailId(@RequestParam Integer productDetailId) {
        ProductDetailResponse response = productService.getAvailableProductsByDetailId(productDetailId);
        return ResponseEntity.ok(response);
    }

    // TODO: change to request body
    // 商品詳情頁面 => 加入購物車
    @PostMapping("/api/addProductToCart")
    public ResponseEntity<Cart> addProductToCart(@ModelAttribute AddProductToCartRequest request) {
        Cart cart = cartService.addProductToCart(request);
        return ResponseEntity.ok(cart);
    }
}
