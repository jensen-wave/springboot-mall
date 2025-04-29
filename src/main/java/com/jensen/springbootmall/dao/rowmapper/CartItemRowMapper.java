// 定義資料庫查詢結果到 CartItem 物件的映射邏輯
package com.jensen.springbootmall.dao.rowmapper;

import com.jensen.springbootmall.model.CartItem;
import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

// CartItemRowMapper: 將資料庫查詢結果轉換為 CartItem 物件
public class CartItemRowMapper implements RowMapper<CartItem> {
    @Override
    public CartItem mapRow(ResultSet rs, int rowNum) throws SQLException {
        CartItem cartItem = new CartItem();
        // 從結果集提取欄位並設置到 CartItem 物件
        cartItem.setCartItemId(rs.getInt("cart_item_id"));
        cartItem.setUserId(rs.getInt("user_id"));
        cartItem.setProductId(rs.getInt("product_id"));
        cartItem.setQuantity(rs.getInt("quantity"));
        cartItem.setCreatedDate(rs.getTimestamp("created_date"));
        cartItem.setLastModifiedDate(rs.getTimestamp("last_modified_date"));
        return cartItem;
    }
}