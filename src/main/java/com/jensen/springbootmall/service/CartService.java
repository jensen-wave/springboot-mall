// 定義購物車業務邏輯的介面
package com.jensen.springbootmall.service;

import com.jensen.springbootmall.dto.CreateCartItemRequest; // 封裝新增購物車請求的資料
import com.jensen.springbootmall.model.CartItem; // 表示購物車中的一筆商品項目

import java.util.List;

// CartService: 提供購物車的業務邏輯處理（不直接操作資料庫，透過 DAO 處理資料）
public interface CartService {

    // 新增或更新購物車項目（若已存在則累加數量）
    CartItem addCartItem(Integer userId, CreateCartItemRequest createCartItemRequest);

    // 修改指定購物車項目的購買數量
    CartItem updateCartItem(Integer userId, Integer cartItemId, Integer newQuantity);

    // 查詢用戶的所有購物車項目（用於前端購物車畫面）
    List<CartItem> getCartItems(Integer userId);

    // 刪除購物車中的某一筆項目（驗證用戶身分）
    void deleteCartItem(Integer userId, Integer cartItemId);

    // 清空購物車（刪除所有項目）
    void clearCart(Integer userId);
}
