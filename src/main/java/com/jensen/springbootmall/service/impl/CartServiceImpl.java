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

// CartServiceImpl: 實現購物車業務邏輯
@Component
public class CartServiceImpl implements CartService {

    // 注入購物車 DAO
    @Autowired
    private CartDao cartDao;

    // 注入商品 DAO，用於驗證商品
    @Autowired
    private ProductDao productDao;

    // 使用事務確保資料一致性
    @Transactional
    @Override
    public CartItem addCartItem(Integer userId, CreateCartItemRequest createCartItemRequest) {
        // 驗證商品是否存在
        Product product = productDao.getProductById(createCartItemRequest.getProductId());
        if (product == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "商品不存在");
        }

        // 檢查是否已存在相同的購物車項目
        CartItem existingCartItem = cartDao.getCartItemByUserIdAndProductId(userId, createCartItemRequest.getProductId());

        if (existingCartItem != null) {
            // 若存在，更新數量（累加）
            Integer newQuantity = existingCartItem.getQuantity() + createCartItemRequest.getQuantity();
            cartDao.updateCartItemQuantity(existingCartItem.getCartItemId(), newQuantity);
            // 返回更新後的項目
            return cartDao.getCartItemById(existingCartItem.getCartItemId());
        } else {
            // 若不存在，新增項目
            Integer cartItemId = cartDao.createCartItem(userId, createCartItemRequest);
            // 返回新增的項目
            return cartDao.getCartItemById(cartItemId);
        }
    }
}