// 定義購物車的 RESTful API 控制器，處理前端請求
package com.jensen.springbootmall.controller;

import com.jensen.springbootmall.dto.CreateCartItemRequest; // 請求物件 DTO，封裝加入購物車所需資料
import com.jensen.springbootmall.dto.UpdateCartItemRequest;
import com.jensen.springbootmall.model.CartItem; // 購物車項目模型
import com.jensen.springbootmall.service.CartService; // 購物車業務邏輯服務

import jakarta.validation.Valid; // 驗證請求體內容是否合法
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// CartController: 處理購物車相關的 HTTP 請求
@RestController // 表示這是一個 RESTful 控制器，方法回傳的物件會直接轉成 JSON 回應
public class CartController {

    // 注入 CartService，負責處理新增與更新購物車的邏輯
    @Autowired
    private CartService cartService;

    // POST 請求：新增或更新指定用戶的購物車項目
    @PostMapping("/users/{userId}/cart") // 對應的路徑為 /users/{userId}/cart
    public ResponseEntity<CartItem> addCartItem(
            @PathVariable Integer userId, // 取得 URL 中的用戶 ID 作為參數
            @RequestBody @Valid CreateCartItemRequest createCartItemRequest // 解析 JSON 請求體並驗證欄位（如 productId、quantity）
    ) {
        // 呼叫 Service 層的邏輯，新增或更新購物車項目
        CartItem cartItem = cartService.addCartItem(userId, createCartItemRequest);

        // 將新增或更新後的購物車項目作為回應主體，並設定 HTTP 狀態為 201 Created
        return ResponseEntity.status(HttpStatus.CREATED).body(cartItem);
    }


    @PutMapping("/users/{userId}/cart/{cartItemId}")
    public ResponseEntity<CartItem> updateCartItemQuantity(@PathVariable Integer userId,
                                                           @PathVariable Integer cartItemId,
                                                           @RequestBody @Valid UpdateCartItemRequest updateCartItemRequest){
        CartItem updatedCartItem  = cartService.updateCartItem(userId, cartItemId, updateCartItemRequest.getQuantity());
        return ResponseEntity.status(HttpStatus.OK).body(updatedCartItem );
    }
}
