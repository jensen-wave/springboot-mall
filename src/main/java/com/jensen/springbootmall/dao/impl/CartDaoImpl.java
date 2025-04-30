// 購物車資料存取層的具體實現，使用 JDBC 與資料庫交互
package com.jensen.springbootmall.dao.impl;

import com.jensen.springbootmall.dao.CartDao;
import com.jensen.springbootmall.dao.rowmapper.CartItemRowMapper; // 負責將查詢結果轉換為 CartItem 物件
import com.jensen.springbootmall.dto.CreateCartItemRequest; // 封裝新增購物車項目的資料
import com.jensen.springbootmall.model.CartItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate; // JDBC 查詢模板，支援具名參數
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// CartDaoImpl: 實作 CartDao 介面，處理與購物車相關的資料庫操作
@Component
public class CartDaoImpl implements CartDao {

    // 注入 JDBC 模板，提供查詢與更新功能
    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    // 查詢指定用戶的所有購物車項目（用於前端顯示購物車清單）
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

        // 查詢並使用 RowMapper 將結果轉換為 List<CartItem>
        return namedParameterJdbcTemplate.query(sql, params, new CartItemRowMapper());
    }

    // 查詢指定用戶與商品是否已有購物車項目（避免重複加入）
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

        return cartItems.isEmpty() ? null : cartItems.get(0); // 回傳結果（若有）
    }

    // 新增購物車項目，並回傳資料庫自動產生的主鍵 ID
    @Override
    public Integer createCartItem(Integer userId, CreateCartItemRequest createCartItemRequest) {
        String sql = "INSERT INTO cart_item (user_id, product_id, quantity, created_date, last_modified_date) " +
                "VALUES (:userId, :productId, :quantity, :createdDate, :lastModifiedDate)";

        MapSqlParameterSource paramSource = new MapSqlParameterSource();
        paramSource.addValue("userId", userId);
        paramSource.addValue("productId", createCartItemRequest.getProductId());
        paramSource.addValue("quantity", createCartItemRequest.getQuantity());

        Date now = new Date();
        paramSource.addValue("createdDate", now);
        paramSource.addValue("lastModifiedDate", now);

        KeyHolder keyHolder = new GeneratedKeyHolder(); // 接收自增主鍵
        namedParameterJdbcTemplate.update(sql, paramSource, keyHolder);

        return keyHolder.getKey().intValue(); // 回傳 cart_item_id
    }

    // 更新購物車項目的數量
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

    // 根據 cart_item_id 查詢一筆購物車項目（含商品名稱、價格、圖片）
    @Override
    public CartItem getCartItemById(Integer cartItemId) {
        String sql = "SELECT cart_item.*, product.product_name, product.image_url, product.price " +
                "FROM cart_item " +
                "JOIN product ON cart_item.product_id = product.product_id " +
                "WHERE cart_item_id = :cartItemId";

        Map<String, Object> map = new HashMap<>();
        map.put("cartItemId", cartItemId);

        List<CartItem> cartItems = namedParameterJdbcTemplate.query(sql, map, new CartItemRowMapper());

        return cartItems.isEmpty() ? null : cartItems.get(0); // 回傳查詢結果
    }
}
