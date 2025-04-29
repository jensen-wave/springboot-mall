// 定義購物車的 RESTful API 控制器，處理前端請求
package com.jensen.springbootmall.controller;

import com.jensen.springbootmall.dto.CreateCartItemRequest;
import com.jensen.springbootmall.model.CartItem;
import com.jensen.springbootmall.service.CartService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// CartController: 處理購物車相關的 HTTP 請求
@RestController
public class CartController {

    // 注入購物車服務層
    @Autowired
    private CartService cartService;

    // POST 請求：新增或更新用戶的購物車項目
    @PostMapping("/users/{userId}/cart")
    public ResponseEntity<CartItem> addCartItem(
            @PathVariable Integer userId, // 從路徑獲取用戶 ID
            @RequestBody @Valid CreateCartItemRequest createCartItemRequest // 從請求體獲取購物車資料
    ) {
        // 調用服務層處理新增或更新
        CartItem cartItem = cartService.addCartItem(userId, createCartItemRequest);
        // 回傳 HTTP 201 Created 狀態和 CartItem 物件
        return ResponseEntity.status(HttpStatus.CREATED).body(cartItem);
    }
}