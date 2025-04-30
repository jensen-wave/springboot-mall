// 購物車資料存取層的具體實現，使用 JDBC 與資料庫交互
package com.jensen.springbootmall.dao.impl;

import com.jensen.springbootmall.dao.CartDao;
import com.jensen.springbootmall.dao.rowmapper.CartItemRowMapper;
import com.jensen.springbootmall.dto.CreateCartItemRequest;
import com.jensen.springbootmall.model.CartItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class CartDaoImpl implements CartDao {

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    // 查詢指定用戶的所有購物車項目
    @Override
    public List<CartItem> getCartItemsByUserId(Integer userId) {
        String sql = "SELECT ci.cart_item_id, ci.user_id, ci.product_id, ci.quantity, " +
                "ci.created_date, ci.last_modified_date, " +
                "p.product_name, p.image_url, p.price " +
                "FROM cart_item ci " +
                "JOIN product p ON ci.product_id = p.product_id " +
                "WHERE ci.user_id = :userId";

        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);

        return namedParameterJdbcTemplate.query(sql, params, new CartItemRowMapper());
    }

    // 刪除特定用戶的某筆購物車項目（雙重條件避免誤刪其他用戶資料）
    @Override
    public void deleteCartItem(Integer userId, Integer cartItemId) {
        String sql = "DELETE FROM cart_item WHERE user_id = :userId AND cart_item_id = :cartItemId";

        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        map.put("cartItemId", cartItemId);

        namedParameterJdbcTemplate.update(sql, map);
    }

    // 清空用戶購物車
    @Override
    public void deleteCartItemsByUserId(Integer userId) {
        String sql = "DELETE FROM cart_item WHERE user_id = :userId";

        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);

        namedParameterJdbcTemplate.update(sql, map);
    }

    // 統計用戶購物車內商品筆數（可用於角標或結帳驗證）
    @Override
    public int countCartItemsByUserId(Integer userId) {
        String sql = "SELECT COUNT(*) FROM cart_item WHERE user_id = :userId";

        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);

        return namedParameterJdbcTemplate.queryForObject(sql, map, Integer.class);
    }

    // 查詢特定用戶是否已有某商品在購物車中
    @Override
    public CartItem getCartItemByUserIdAndProductId(Integer userId, Integer productId) {
        String sql = "SELECT ci.cart_item_id, ci.user_id, ci.product_id, ci.quantity, " +
                "ci.created_date, ci.last_modified_date, p.price, " +
                "p.product_name, p.image_url " +
                "FROM cart_item ci " +
                "JOIN product p ON ci.product_id = p.product_id " +
                "WHERE ci.user_id = :userId AND ci.product_id = :productId";

        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        map.put("productId", productId);

        List<CartItem> cartItems = namedParameterJdbcTemplate.query(sql, map, new CartItemRowMapper());
        return cartItems.isEmpty() ? null : cartItems.get(0);
    }

    // 新增購物車項目，並回傳主鍵 ID
    @Override
    public Integer createCartItem(Integer userId, CreateCartItemRequest request) {
        String sql = "INSERT INTO cart_item (user_id, product_id, quantity, created_date, last_modified_date) " +
                "VALUES (:userId, :productId, :quantity, :createdDate, :lastModifiedDate)";

        MapSqlParameterSource paramSource = new MapSqlParameterSource();
        paramSource.addValue("userId", userId);
        paramSource.addValue("productId", request.getProductId());
        paramSource.addValue("quantity", request.getQuantity());

        Date now = new Date();
        paramSource.addValue("createdDate", now);
        paramSource.addValue("lastModifiedDate", now);

        KeyHolder keyHolder = new GeneratedKeyHolder();
        namedParameterJdbcTemplate.update(sql, paramSource, keyHolder);

        return keyHolder.getKey().intValue();
    }

    // 更新購物車商品數量
    @Override
    public void updateCartItemQuantity(Integer cartItemId, Integer newQuantity) {
        String sql = "UPDATE cart_item " +
                "SET quantity = :quantity, last_modified_date = :lastModifiedDate " +
                "WHERE cart_item_id = :cartItemId";

        Map<String, Object> map = new HashMap<>();
        map.put("quantity", newQuantity);
        map.put("lastModifiedDate", new Date());
        map.put("cartItemId", cartItemId);

        namedParameterJdbcTemplate.update(sql, map);
    }

    // 根據 ID 查詢購物車項目（包含關聯的商品資料）
    @Override
    public CartItem getCartItemById(Integer cartItemId) {
        String sql = "SELECT cart_item.*, product.product_name, product.image_url, product.price " +
                "FROM cart_item " +
                "JOIN product ON cart_item.product_id = product.product_id " +
                "WHERE cart_item_id = :cartItemId";

        Map<String, Object> map = new HashMap<>();
        map.put("cartItemId", cartItemId);

        List<CartItem> cartItems = namedParameterJdbcTemplate.query(sql, map, new CartItemRowMapper());
        return cartItems.isEmpty() ? null : cartItems.get(0);
    }
}
