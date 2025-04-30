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

// CartDaoImpl: 實現購物車資料庫操作，使用 NamedParameterJdbcTemplate
@Component
public class CartDaoImpl implements CartDao {

    // 注入 JDBC 模板，用於執行 SQL 查詢
    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public CartItem getCartItemByUserIdAndProductId(Integer userId, Integer productId) {
        // 查詢指定用戶和商品的購物車項目

        String sql = "SELECT ci.cart_item_id, ci.user_id, ci.product_id, ci.quantity, " +
                "ci.created_date, ci.last_modified_date, p.price, " +
                "p.product_name, p.image_url " +
                "FROM cart_item ci " +
                "JOIN product p ON ci.product_id = p.product_id " +
                "WHERE ci.user_id = :userId AND ci.product_id = :productId";

        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        map.put("productId", productId);
        // 執行查詢並使用 RowMapper 映射結果
        List<CartItem> cartItems = namedParameterJdbcTemplate.query(sql, map, new CartItemRowMapper());
        // 若無結果返回 null，否則返回第一筆
        return cartItems.isEmpty() ? null : cartItems.get(0);
    }

    @Override
    public Integer createCartItem(Integer userId, CreateCartItemRequest createCartItemRequest) {
        // 插入新購物車項目的 SQL
        String sql = "INSERT INTO cart_item (user_id, product_id, quantity, created_date, last_modified_date) " +
                "VALUES (:userId, :productId, :quantity, :createdDate, :lastModifiedDate)";

        // 準備參數
        MapSqlParameterSource paramSource = new MapSqlParameterSource();
        paramSource.addValue("userId", userId);
        paramSource.addValue("productId", createCartItemRequest.getProductId());
        paramSource.addValue("quantity", createCartItemRequest.getQuantity());
        Date now = new Date();
        paramSource.addValue("createdDate", now);
        paramSource.addValue("lastModifiedDate", now);

        // 執行插入並獲取生成的主鍵
        KeyHolder keyHolder = new GeneratedKeyHolder();
        namedParameterJdbcTemplate.update(sql, paramSource, keyHolder);
        return keyHolder.getKey().intValue();
    }

    @Override
    public void updateCartItemQuantity(Integer cartItemId, Integer newQuantity) {
        // 更新購物車項目數量的 SQL
        String sql = "UPDATE cart_item SET quantity = :quantity, last_modified_date = :lastModifiedDate WHERE cart_item_id = :cartItemId";
        Map<String, Object> map = new HashMap<>();
        map.put("quantity", newQuantity);
        map.put("lastModifiedDate", new Date());
        map.put("cartItemId", cartItemId);
        // 執行更新
        namedParameterJdbcTemplate.update(sql, map);
    }

    @Override
    public CartItem getCartItemById(Integer cartItemId) {
        // 根據 ID 查詢購物車項目
        String sql = "SELECT cart_item.*,product.product_name,product.image_url, product.price " +
                "FROM cart_item " +
                "join product on cart_item.product_id = product.product_id " +
                "WHERE cart_item_id=:cartItemId";

        Map<String, Object> map = new HashMap<>();
        map.put("cartItemId", cartItemId);
        // 執行查詢並映射結果
        List<CartItem> cartItems = namedParameterJdbcTemplate.query(sql, map, new CartItemRowMapper());
        return cartItems.isEmpty() ? null : cartItems.get(0);
    }
}