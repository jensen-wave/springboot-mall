// 定義購物車業務邏輯的介面
package com.jensen.springbootmall.service;

import com.jensen.springbootmall.dto.CreateCartItemRequest; // 用於封裝前端傳來的新增購物車資料
import com.jensen.springbootmall.model.CartItem; // 購物車項目模型，作為返回值使用

// CartService: 提供購物車相關的業務處理方法（Service 層負責處理流程邏輯，不直接與資料庫互動）
public interface CartService {

    // 新增或更新購物車項目
    // 如果相同用戶已將該商品加入購物車，則更新數量；否則新增一筆購物車項目
    // 傳入參數：
    //   - userId: 用戶 ID
    //   - createCartItemRequest: 前端請求內容，包含 productId 和 quantity
    // 返回值：
    //   - 完整的 CartItem 物件，包含商品資訊與目前數量
    CartItem addCartItem(Integer userId, CreateCartItemRequest createCartItemRequest);

    CartItem updateCartItem(Integer userId, Integer cartItemId, Integer newQuantity);

}
