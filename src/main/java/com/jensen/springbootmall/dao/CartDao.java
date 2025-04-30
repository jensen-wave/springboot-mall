// 定義購物車資料存取層的介面，規範與資料庫的交互操作
package com.jensen.springbootmall.dao;

import com.jensen.springbootmall.dto.CreateCartItemRequest; // DTO，用於接收新增購物車項目的請求資料
import com.jensen.springbootmall.model.CartItem; // 模型類別，表示購物車中的一筆資料

import java.util.List;

// CartDao: 提供購物車相關的資料庫操作方法（DAO：Data Access Object）
public interface CartDao {

    // 根據用戶 ID 和商品 ID 查詢購物車項目（確認是否已存在相同商品）
    CartItem getCartItemByUserIdAndProductId(Integer userId, Integer productId);

    // 新增一筆購物車項目，並返回資料庫中自動生成的主鍵 ID
    Integer createCartItem(Integer userId, CreateCartItemRequest createCartItemRequest);

    // 更新指定購物車項目的購買數量
    void updateCartItemQuantity(Integer cartItemId, Integer newQuantity);

    // 根據購物車項目 ID 查詢一筆項目（通常用於後續更新或刪除操作）
    CartItem getCartItemById(Integer cartItemId);

    // 根據用戶 ID 取得其所有購物車項目（通常用於前端頁面列出購物車內容）
    List<CartItem> getCartItemsByUserId(Integer userId);

    // 刪除指定用戶的指定購物車項目（需驗證 userId 確保安全）
    void deleteCartItem(Integer userId, Integer cartItemId);

    // 清空指定用戶的整個購物車
    void deleteCartItemsByUserId(Integer userId);

    // 統計指定用戶的購物車內項目數量（常用於顯示購物車角標數量等場景）
    int countCartItemsByUserId(Integer userId);
}
