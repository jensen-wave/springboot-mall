// 購物車業務邏輯的具體實現，處理商品驗證和購物車操作
package com.jensen.springbootmall.service.impl;

import com.jensen.springbootmall.dao.CartDao; // DAO：操作購物車資料表
import com.jensen.springbootmall.dao.ProductDao; // DAO：操作商品資料表
import com.jensen.springbootmall.dto.CreateCartItemRequest; // 前端傳來的新增購物車請求資料封裝
import com.jensen.springbootmall.model.CartItem; // 購物車項目模型
import com.jensen.springbootmall.model.Product; // 商品模型
import com.jensen.springbootmall.service.CartService; // 購物車業務邏輯接口
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional; // 保證資料一致性的事務註解
import org.springframework.web.server.ResponseStatusException; // 拋出 HTTP 狀態錯誤用的例外

// CartServiceImpl: 實作 CartService 業務邏輯
@Component
public class CartServiceImpl implements CartService {

    // 注入購物車 DAO，處理與 cart_item 表的互動
    @Autowired
    private CartDao cartDao;

    // 注入商品 DAO，先驗證商品是否存在再處理
    @Autowired
    private ProductDao productDao;

    // 使用 @Transactional 保證整個流程要麼全部成功，要麼全部失敗（例如新增＋查詢）
    @Transactional
    @Override
    public CartItem addCartItem(Integer userId, CreateCartItemRequest createCartItemRequest) {
        // 先根據商品 ID 查詢，確認商品存在
        Product product = productDao.getProductById(createCartItemRequest.getProductId());
        if (product == null) {
            // 如果商品不存在，回傳 400 Bad Request
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "商品不存在");
        }

        // 查詢該用戶是否已經將此商品加入購物車
        CartItem existingCartItem = cartDao.getCartItemByUserIdAndProductId(userId, createCartItemRequest.getProductId());

        if (existingCartItem != null) {
            // 如果已存在，則更新數量（累加）
            Integer newQuantity = existingCartItem.getQuantity() + createCartItemRequest.getQuantity();
            cartDao.updateCartItemQuantity(existingCartItem.getCartItemId(), newQuantity);
            // 回傳更新後的項目內容
            return cartDao.getCartItemById(existingCartItem.getCartItemId());
        } else {
            // 若不存在此商品於購物車中，則新增一筆記錄
            Integer cartItemId = cartDao.createCartItem(userId, createCartItemRequest);
            // 回傳新增後的完整 CartItem（包含商品名稱、價格等）
            return cartDao.getCartItemById(cartItemId);
        }
    }

    @Override
    public CartItem updateCartItem(Integer userId, Integer cartItemId, Integer newQuantity) {

        CartItem cartItem =cartDao.getCartItemById(cartItemId);
        if (cartItem == null || !cartItem.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "購物車項目不存在或不屬於該用戶");
        }
        cartDao.updateCartItemQuantity(cartItemId, newQuantity);

        CartItem updateCartItem = cartDao.getCartItemById(cartItemId);

        return updateCartItem;
    }
}
