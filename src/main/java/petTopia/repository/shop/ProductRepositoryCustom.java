package petTopia.repository.shop;

import java.util.List;

import org.json.JSONObject;
import petTopia.model.shop.Product;

public interface ProductRepositoryCustom {
    // 根據條件搜尋商品的總數
    Long count(JSONObject obj);

    // 根據條件搜尋商品
    List<Product> find(JSONObject obj);
}
