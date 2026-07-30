package petTopia.service.shop;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;
import petTopia.dto.shop.request.AddProductToCartRequest;
import petTopia.dto.shop.request.ConfirmProductRequest;
import petTopia.model.shop.Cart;
import petTopia.model.shop.Product;
import petTopia.model.user.Member;
import petTopia.repository.shop.CartRepository;
import petTopia.repository.shop.ProductRepository;
import petTopia.repository.user.MemberRepository;

@Transactional(readOnly = true)
@Service
public class CartService {

    @Autowired
    private CartRepository cartRepo;

    @Autowired
    private ProductRepository productRepo;

    @Autowired
    private MemberRepository memberRepo;

    // 根據用戶 ID 獲取購物車商品  //單次查詢
    public List<Cart> getCartItems(Integer memberId) {
        return cartRepo.findByMemberId(memberId);
    }

    // 根據用戶 ID 獲取購物車商品  //單次查詢
    public List<Cart> getProductCartItems(Integer memberId, List<Integer> productIds) {
        return cartRepo.findByMemberIdAndProductIdIn(memberId, productIds);
    }

    // 計算購物車的總金額
    public BigDecimal calculateTotalPrice(Integer memberId, List<Integer> productIds) {
        BigDecimal total = BigDecimal.ZERO; // 初始化為 0

        // 查詢會員購物車中這些商品
        List<Cart> cartItems = cartRepo.findByMemberIdAndProductIdIn(memberId, productIds);

        // 計算總金額
        for (Cart item : cartItems) {
            // 取得商品的價格（折扣價或原價）
            BigDecimal price;

            // 檢查折扣價是否為 null，若為 null 則使用單價
            if (item.getProduct().getDiscountPrice() != null) {
                price = item.getProduct().getDiscountPrice();
            } else {
                price = item.getProduct().getUnitPrice();
            }

            // 根據購買數量計算總價
            BigDecimal totalPriceForItem = price.multiply(new BigDecimal(item.getQuantity()));

            // 累加到總金額
            total = total.add(totalPriceForItem);
        }

        return total; // 返回計算後的總金額
    }

//    // 計算購物車的總金額
//    public BigDecimal calculateTotalPriceForCheckout(List<CartItemForCheckoutDto> cartItems) {
//        BigDecimal total = BigDecimal.ZERO; // 初始化為 0
//
//        for (CartItemForCheckoutDto item : cartItems) {
//            // 取得商品的價格（折扣價或原價）
//            BigDecimal price;
//
//            // 檢查折扣價是否為 null，若為 null 則使用單價
//            if (item.getDiscountPrice() != null) {
//                price = item.getDiscountPrice();
//            } else {
//                price = item.getUnitPrice();
//            }
//
//            // 取得數量並轉換為 BigDecimal
//            BigDecimal quantity = new BigDecimal(item.getQuantity());
//
//            // 計算總金額並加到 total
//            total = total.add(price.multiply(quantity)); // 使用 add() 方法進行加法
//        }
//
//        return total; // 返回計算後的總金額
//    }

    // 清空用戶的購物車
    @Transactional
    public void clearCart(Integer memberId, List<Integer> productIds) {
        cartRepo.deleteByMemberIdAndProductIds(memberId, productIds);  // 根據 memberId 刪除購物車內容
    }

    // 商品加入購物車
    @Transactional
    public Cart addProductToCart(AddProductToCartRequest request) {
        Member member = memberRepo.findById(request.getMemberId())
                .orElseThrow(() -> new EntityNotFoundException("Member not found"));

        Product product = productRepo.findByProductDetailIdAndProductSizeIdAndProductColorId(
                request.getProductDetailId(),
                request.getProductSizeId(),
                request.getProductColorId()
        );

        // 檢查會員Cart有沒有同個商品，有就更新，沒有就新增
        Optional<Cart> cartOptional = cartRepo.findByMemberIdAndProductId(request.getMemberId(), product.getId());

        Integer quantity = request.getQuantity();

        if (cartOptional.isPresent()) {
            Cart cart = cartOptional.get();
            Integer updateQuantity = cart.getQuantity() + quantity;

            if (updateQuantity <= product.getStockQuantity()) {
                cart.setQuantity(updateQuantity);
                return cartRepo.save(cart);
            }

            return cart;
        } else {
            if (quantity <= product.getStockQuantity()) {
                Cart addCart = new Cart();

                addCart.setMember(member);
                addCart.setProduct(product);
                addCart.setQuantity(quantity);

                return cartRepo.save(addCart);
            }

            return null;
        }
    }

    // 更新會員購物車內該商品的數量
    @Transactional
    public Cart updateCartProductQuantity(Integer memberId, Integer productId, Integer quantity) {
        if (!memberRepo.existsById(memberId)) {
            throw new EntityNotFoundException("Member not found");
        }

        Product product = productRepo.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));

        Optional<Cart> cartOptional = cartRepo.findByMemberIdAndProductId(memberId, productId);

        if (cartOptional.isEmpty()) return null;

        Cart cart = cartOptional.get();

        if (quantity <= product.getStockQuantity()) {
            cart.setQuantity(quantity);
            return cartRepo.save(cart);
        }

        return cart;
    }

    public Map<String, Object> getCartByMemberAndProductRelatedData(ConfirmProductRequest request) {
        Product product = productRepo.findByProductDetailIdAndProductSizeIdAndProductColorId(
                request.getProductDetailId(),
                request.getProductSizeId(),
                request.getProductColorId()
        );

        Integer productQuantityInCart = cartRepo.findByMemberIdAndProductId(request.getMemberId(), product.getId())
                .map(Cart::getQuantity)
                .orElse(null);

        Map<String, Object> responseData = new HashMap<>();

        responseData.put("productQuantityInCart", productQuantityInCart);
        responseData.put("product", product);

        return responseData;
    }

    // 獲取會員購物車
    public List<Cart> getCartByMemberId(Integer memberId) {
        return cartRepo.findByMemberId(memberId);
    }

    // 獲取會員勾選要結帳的商品的購物車
    public List<Cart> getCartByMemberIdAndProductIds(Integer memberId, List<Integer> productIds) {
        List<Cart> carts = cartRepo.findByMemberIdAndProductIdIn(memberId, productIds);
        if (carts != null && carts.size() != 0) {
            return carts;
        }

        return null;
    }

    // 根據cartId刪除該商品的購物車
    public Integer deleteCartById(Integer cartId) {
        Optional<Cart> cartOpt = cartRepo.findById(cartId);
        if (cartOpt.isPresent()) {
            Cart deleteCart = cartOpt.get();
            cartRepo.delete(deleteCart);
            return cartId;
        }

        return null;
    }

    // 根據memberId獲取購物車數量
    public Integer getMemeberCartCount(Integer memberId) {
        Integer count = cartRepo.countByMemberId(memberId);
        return count;
    }

}
