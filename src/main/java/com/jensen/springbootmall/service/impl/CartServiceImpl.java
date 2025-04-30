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

import java.util.List;

// CartServiceImpl: 實作 CartService 業務邏輯
@Component
public class CartServiceImpl implements CartService {

    // 注入購物車 DAO，處理與 cart_item 表的互動
    @Autowired
    private CartDao cartDao;

    // 注入商品 DAO，負責檢查商品是否存在
    @Autowired
    private ProductDao productDao;

    // 查詢指定用戶的所有購物車項目（用於前端載入購物車頁面）
    @Override
    public List<CartItem> getCartItems(Integer userId) {
        return cartDao.getCartItemsByUserId(userId); // 直接調用 DAO 查詢所有項目
    }

    @Override
    public void deleteCartItem(Integer userId, Integer cartItemId) {
        cartDao.deleteCartItem(userId,cartItemId);
    }

    // 新增或更新購物車項目，使用 @Transactional 確保操作原子性
    @Transactional
    @Override
    public CartItem addCartItem(Integer userId, CreateCartItemRequest createCartItemRequest) {
        // 檢查商品是否存在
        Product product = productDao.getProductById(createCartItemRequest.getProductId());
        if (product == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "商品不存在");
        }

        // 查詢是否已有相同商品存在於該用戶購物車中
        CartItem existingCartItem = cartDao.getCartItemByUserIdAndProductId(userId, createCartItemRequest.getProductId());

        if (existingCartItem != null) {
            // 已存在則更新數量（累加）
            Integer newQuantity = existingCartItem.getQuantity() + createCartItemRequest.getQuantity();
            cartDao.updateCartItemQuantity(existingCartItem.getCartItemId(), newQuantity);
            return cartDao.getCartItemById(existingCartItem.getCartItemId()); // 回傳更新後項目
        } else {
            // 若不存在則新增一筆購物車項目
            Integer cartItemId = cartDao.createCartItem(userId, createCartItemRequest);
            return cartDao.getCartItemById(cartItemId); // 回傳新增後項目
        }
    }

    // 修改指定購物車項目的數量（僅限該用戶本人）
    @Override
    public CartItem updateCartItem(Integer userId, Integer cartItemId, Integer newQuantity) {
        // 查詢該購物車項目是否存在，並檢查是否屬於該用戶
        CartItem cartItem = cartDao.getCartItemById(cartItemId);
        if (cartItem == null || !cartItem.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "購物車項目不存在或不屬於該用戶");
        }

        // 更新數量
        cartDao.updateCartItemQuantity(cartItemId, newQuantity);

        // 查詢更新後的完整項目資料並回傳
        return cartDao.getCartItemById(cartItemId);
    }
}
