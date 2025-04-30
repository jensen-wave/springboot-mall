// 定義購物車業務邏輯的介面
package com.jensen.springbootmall.service;

import com.jensen.springbootmall.dto.CreateCartItemRequest; // 用於封裝前端傳來的新增購物車資料
import com.jensen.springbootmall.model.CartItem; // 購物車項目模型，作為返回值使用

import java.util.List;

// CartService: 提供購物車相關的業務處理方法（Service 層負責處理流程邏輯，不直接與資料庫互動）
public interface CartService {

    // 新增或更新購物車項目
    // 如果該用戶購物車中已有相同商品，則更新數量（累加）；否則新增一筆新項目
    CartItem addCartItem(Integer userId, CreateCartItemRequest createCartItemRequest);

    // 更新購物車項目的數量（根據 cartItemId 指定修改）
    // 傳入參數：
    //   - userId: 驗證操作者身分
    //   - cartItemId: 要更新的購物車項目 ID
    //   - newQuantity: 新的數量（可為 0，用於前端後續刪除設計）
    CartItem updateCartItem(Integer userId, Integer cartItemId, Integer newQuantity);

    // 查詢指定用戶的所有購物車項目清單（用於購物車頁面顯示）
    List<CartItem> getCartItems(Integer userId);

    void deleteCartItem(Integer userId,Integer cartItemId);

}
