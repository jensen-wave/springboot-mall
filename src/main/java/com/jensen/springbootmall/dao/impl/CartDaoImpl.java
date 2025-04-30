// 購物車資料存取層的具體實現，使用 JDBC 與資料庫交互
package com.jensen.springbootmall.dao.impl;

import com.jensen.springbootmall.dao.CartDao;
import com.jensen.springbootmall.dao.rowmapper.CartItemRowMapper; // 負責將查詢結果轉換為 CartItem 物件的工具
import com.jensen.springbootmall.dto.CreateCartItemRequest; // 用於接收新增購物車項目的請求資料
import com.jensen.springbootmall.model.CartItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate; // 提供具名參數的 JDBC 工具
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// CartDaoImpl: 實作 CartDao 介面，處理與購物車資料表的 CRUD 操作
@Component
public class CartDaoImpl implements CartDao {

    // 注入 Spring 提供的 JDBC 工具，用來執行帶具名參數的 SQL 查詢與更新
    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    // 查詢指定用戶與商品是否已有購物車項目存在
    @Override
    public CartItem getCartItemByUserIdAndProductId(Integer userId, Integer productId) {
        String sql = "SELECT ci.cart_item_id, ci.user_id, ci.product_id, ci.quantity, " +
                "ci.created_date, ci.last_modified_date, p.price, " +
                "p.product_name, p.image_url " +
                "FROM cart_item ci " +
                "JOIN product p ON ci.product_id = p.product_id " +
                "WHERE ci.user_id = :userId AND ci.product_id = :productId";

        // 使用 Map 傳入具名參數
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        map.put("productId", productId);

        // 執行 SQL 並轉換為 CartItem 物件清單
        List<CartItem> cartItems = namedParameterJdbcTemplate.query(sql, map, new CartItemRowMapper());

        // 若無查詢結果則回傳 null，否則回傳第一筆（應該只會有一筆）
        return cartItems.isEmpty() ? null : cartItems.get(0);
    }

    // 新增購物車項目，回傳自動產生的 cart_item_id
    @Override
    public Integer createCartItem(Integer userId, CreateCartItemRequest createCartItemRequest) {
        String sql = "INSERT INTO cart_item (user_id, product_id, quantity, created_date, last_modified_date) " +
                "VALUES (:userId, :productId, :quantity, :createdDate, :lastModifiedDate)";

        // 使用 MapSqlParameterSource 填入具名參數
        MapSqlParameterSource paramSource = new MapSqlParameterSource();
        paramSource.addValue("userId", userId);
        paramSource.addValue("productId", createCartItemRequest.getProductId());
        paramSource.addValue("quantity", createCartItemRequest.getQuantity());

        Date now = new Date(); // 設定創建與更新時間為當前時間
        paramSource.addValue("createdDate", now);
        paramSource.addValue("lastModifiedDate", now);

        // 用來接收資料庫回傳的自動生成主鍵
        KeyHolder keyHolder = new GeneratedKeyHolder();

        // 執行 insert 並返回生成的主鍵值
        namedParameterJdbcTemplate.update(sql, paramSource, keyHolder);

        return keyHolder.getKey().intValue(); // 回傳 cart_item_id
    }

    // 更新購物車中指定項目的數量
    @Override
    public void updateCartItemQuantity(Integer cartItemId, Integer newQuantity) {
        String sql = "UPDATE cart_item SET quantity = :quantity, last_modified_date = :lastModifiedDate WHERE cart_item_id = :cartItemId";

        Map<String, Object> map = new HashMap<>();
        map.put("quantity", newQuantity); // 新的數量
        map.put("lastModifiedDate", new Date()); // 記錄更新時間
        map.put("cartItemId", cartItemId); // 指定要更新的項目

        // 執行更新操作
        namedParameterJdbcTemplate.update(sql, map);
    }

    // 根據 cart_item_id 查詢購物車單筆項目（含商品資料）
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
