package petTopia.repository.shop;

import java.util.List;

import org.json.JSONObject;
import petTopia.model.shop.ProductDetail;

public interface ProductDetailRepositoryCustom {
    // 根據條件搜尋商品的總數
    long count(JSONObject obj);

    // 根據條件搜尋商品
    List<ProductDetail> find(JSONObject obj);

    // 模糊搜尋 (多個關鍵字) (舊版)
    List<ProductDetail> searchProducts(String keywords);
}
