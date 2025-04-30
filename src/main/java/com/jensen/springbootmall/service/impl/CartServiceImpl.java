// 購物車業務邏輯的具體實現，處理商品驗證和購物車操作
package com.jensen.springbootmall.service.impl;

import com.jensen.springbootmall.dao.CartDao;
import com.jensen.springbootmall.dao.ProductDao;
import com.jensen.springbootmall.dto.CreateCartItemRequest;
import com.jensen.springbootmall.model.CartItem;
import com.jensen.springbootmall.model.Product;
import com.jensen.springbootmall.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

// CartServiceImpl: 實作 CartService，處理購物車所有業務邏輯
@Component
public class CartServiceImpl implements CartService {

    @Autowired
    private CartDao cartDao;

    @Autowired
    private ProductDao productDao;

    // 查詢指定用戶的所有購物車項目
    @Override
    public List<CartItem> getCartItems(Integer userId) {
        return cartDao.getCartItemsByUserId(userId);
    }

    // 刪除購物車中的某一筆項目（需確認該筆項目屬於此用戶，可於 Controller 中驗證或此處加強）
    @Override
    public void deleteCartItem(Integer userId, Integer cartItemId) {
        cartDao.deleteCartItem(userId, cartItemId);
    }

    // 清空整個購物車（若購物車本就為空，回傳 400 Bad Request）
    @Override
    public void clearCart(Integer userId) {
        int counted = cartDao.countCartItemsByUserId(userId);
        if (counted == 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "購物車已為空，無需清空");
        }
        cartDao.deleteCartItemsByUserId(userId);
    }

    // 新增或更新購物車項目
    @Transactional
    @Override
    public CartItem addCartItem(Integer userId, CreateCartItemRequest createCartItemRequest) {
        // 驗證商品是否存在
        Product product = productDao.getProductById(createCartItemRequest.getProductId());
        if (product == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "商品不存在");
        }

        // 查詢是否已有相同商品在購物車中
        CartItem existingCartItem = cartDao.getCartItemByUserIdAndProductId(userId, createCartItemRequest.getProductId());

        if (existingCartItem != null) {
            // 若已存在則累加數量
            Integer newQuantity = existingCartItem.getQuantity() + createCartItemRequest.getQuantity();
            cartDao.updateCartItemQuantity(existingCartItem.getCartItemId(), newQuantity);
            return cartDao.getCartItemById(existingCartItem.getCartItemId());
        } else {
            // 否則新增一筆新項目
            Integer cartItemId = cartDao.createCartItem(userId, createCartItemRequest);
            return cartDao.getCartItemById(cartItemId);
        }
    }

    // 修改購物車中某一筆項目的數量
    @Override
    public CartItem updateCartItem(Integer userId, Integer cartItemId, Integer newQuantity) {
        // 查詢並驗證該筆項目是否存在且屬於該用戶
        CartItem cartItem = cartDao.getCartItemById(cartItemId);
        if (cartItem == null || !cartItem.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "購物車項目不存在或不屬於該用戶");
        }

        // 執行更新
        cartDao.updateCartItemQuantity(cartItemId, newQuantity);
        return cartDao.getCartItemById(cartItemId);
    }
}
