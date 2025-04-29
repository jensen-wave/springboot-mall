// 定義購物車業務邏輯的介面
package com.jensen.springbootmall.service;

import com.jensen.springbootmall.dto.CreateCartItemRequest;
import com.jensen.springbootmall.model.CartItem;

// CartService: 提供購物車相關的業務處理方法
public interface CartService {
    // 新增或更新購物車項目，返回完整的 CartItem 物件
    CartItem addCartItem(Integer userId, CreateCartItemRequest createCartItemRequest);
}