// 定義購物車的 RESTful API 控制器，處理前端請求
package com.jensen.springbootmall.controller;

import com.jensen.springbootmall.dto.CreateCartItemRequest; // 封裝新增購物車資料
import com.jensen.springbootmall.dto.UpdateCartItemRequest; // 封裝更新購物車數量資料
import com.jensen.springbootmall.model.CartItem; // 購物車項目模型
import com.jensen.springbootmall.service.CartService; // 業務邏輯服務

import jakarta.validation.Valid; // 用於請求資料驗證
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// CartController: 處理購物車相關的 HTTP 請求
@RestController
public class CartController {

    // 注入 CartService，處理購物車相關邏輯
    @Autowired
    private CartService cartService;

    @DeleteMapping("/users/{userId}/cart/{cartItemId}")
    public ResponseEntity<?> deleteCartItem(@PathVariable Integer userId,
                                            @PathVariable Integer cartItemId){
        cartService.deleteCartItem(userId,cartItemId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    // GET /users/{userId}/cart：取得指定用戶的購物車清單
    @GetMapping("/users/{userId}/cart")
    public ResponseEntity<List<CartItem>> getCartItems(
            @PathVariable Integer userId // 從 URL 路徑中取得 userId
    ) {
        List<CartItem> cartItems = cartService.getCartItems(userId); // 呼叫 service 取得清單
        return ResponseEntity.status(HttpStatus.OK).body(cartItems); // 回傳 200 OK 與清單資料
    }

    // POST /users/{userId}/cart：新增或累加商品至購物車
    @PostMapping("/users/{userId}/cart")
    public ResponseEntity<CartItem> addCartItem(
            @PathVariable Integer userId,
            @RequestBody @Valid CreateCartItemRequest createCartItemRequest // 驗證商品 ID 與數量是否合法
    ) {
        CartItem cartItem = cartService.addCartItem(userId, createCartItemRequest); // 呼叫 service 處理新增或更新邏輯
        return ResponseEntity.status(HttpStatus.CREATED).body(cartItem); // 回傳 201 Created 與 CartItem 資料
    }

    // PUT /users/{userId}/cart/{cartItemId}：更新購物車中某項商品的數量
    @PutMapping("/users/{userId}/cart/{cartItemId}")
    public ResponseEntity<CartItem> updateCartItemQuantity(
            @PathVariable Integer userId, // 用於驗證該筆資料是否屬於該用戶
            @PathVariable Integer cartItemId,
            @RequestBody @Valid UpdateCartItemRequest updateCartItemRequest // 取得要更新的數量
    ) {
        CartItem updatedCartItem = cartService.updateCartItem(userId, cartItemId, updateCartItemRequest.getQuantity()); // 執行更新
        return ResponseEntity.status(HttpStatus.OK).body(updatedCartItem); // 回傳更新後的資料與 200 OK
    }
}
