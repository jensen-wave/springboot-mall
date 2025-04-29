// 定義購物車資料存取層的介面，規範與資料庫的交互操作
package com.jensen.springbootmall.dao;

import com.jensen.springbootmall.dto.CreateCartItemRequest;
import com.jensen.springbootmall.model.CartItem;

// CartDao: 提供購物車相關的資料庫操作方法
public interface CartDao {
    // 根據用戶 ID 和商品 ID 查詢購物車項目
    CartItem getCartItemByUserIdAndProductId(Integer userId, Integer productId);

    // 新增購物車項目，返回生成的主鍵 ID
    Integer createCartItem(Integer userId, CreateCartItemRequest createCartItemRequest);

    // 更新指定購物車項目的數量
    void updateCartItemQuantity(Integer cartItemId, Integer newQuantity);

    // 根據購物車項目 ID 查詢單筆項目
    CartItem getCartItemById(Integer cartItemId);
}