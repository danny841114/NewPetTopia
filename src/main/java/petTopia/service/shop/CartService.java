package petTopia.service.shop;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import petTopia.dto.shop.request.AddProductToCartRequest;
import petTopia.dto.shop.request.ConfirmProductRequest;
import petTopia.model.shop.Cart;
import petTopia.model.shop.Product;
import petTopia.model.user.Member;
import petTopia.repository.shop.CartRepository;
import petTopia.repository.shop.ProductRepository;
import petTopia.repository.user.MemberRepository;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class CartService {
    private final CartRepository cartRepo;
    private final ProductRepository productRepo;
    private final MemberRepository memberRepo;

    public BigDecimal calculateTotalPrice(Integer memberId, List<Integer> productIds) {
        BigDecimal total = BigDecimal.ZERO; // 初始化為 0

        List<Cart> cartItems = cartRepo.findByMemberIdAndProductIdIn(memberId, productIds);

        for (Cart item : cartItems) {
            BigDecimal price = item.getProduct().getDiscountPrice() != null
                    ? item.getProduct().getDiscountPrice()
                    : item.getProduct().getUnitPrice();

            BigDecimal totalPriceForItem = price.multiply(new BigDecimal(item.getQuantity()));

            total = total.add(totalPriceForItem);
        }

        return total;
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
        cartRepo.deleteByMemberIdAndProductIds(memberId, productIds);
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

    @Transactional
    public Cart updateCartProductQuantity(Integer memberId, Integer productId, Integer quantity) {
        if (!memberRepo.existsById(memberId)) throw new EntityNotFoundException("Member not found");

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

    public List<Cart> getCartByMemberId(Integer memberId) {
        return cartRepo.findByMemberId(memberId);
    }

    public List<Cart> getCartByMemberIdAndProductIds(Integer memberId, List<Integer> productIds) {
        return cartRepo.findByMemberIdAndProductIdIn(memberId, productIds);
    }

    @Transactional
    public void deleteCartById(Integer cartId) {
        cartRepo.findById(cartId)
                .ifPresent(cartRepo::delete);
    }

    // 根據memberId獲取購物車數量
    public Integer getMemberCartCount(Integer memberId) {
        return cartRepo.countByMemberId(memberId);
    }
}
