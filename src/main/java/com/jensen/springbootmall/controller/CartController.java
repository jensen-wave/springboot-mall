// 定義購物車的 RESTful API 控制器，處理前端請求
package com.jensen.springbootmall.controller;

import com.jensen.springbootmall.dto.CreateCartItemRequest;
import com.jensen.springbootmall.dto.UpdateCartItemRequest;
import com.jensen.springbootmall.model.CartItem;
import com.jensen.springbootmall.service.CartService;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController // 表示這是一個 RESTful 控制器，會自動將回傳物件轉為 JSON
public class CartController {

    @Autowired
    private CartService cartService;

    // 清空整個購物車（DELETE /users/{userId}/cart）
    @DeleteMapping("/users/{userId}/cart")
    public ResponseEntity<?> clearCart(@PathVariable Integer userId) {
        cartService.clearCart(userId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build(); // 204 No Content
    }

    // 刪除購物車中的單一商品（DELETE /users/{userId}/cart/{cartItemId}）
    @DeleteMapping("/users/{userId}/cart/{cartItemId}")
    public ResponseEntity<?> deleteCartItem(
            @PathVariable Integer userId,
            @PathVariable Integer cartItemId
    ) {
        cartService.deleteCartItem(userId, cartItemId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build(); // 204 No Content
    }

    // 查詢使用者購物車（GET /users/{userId}/cart）
    @GetMapping("/users/{userId}/cart")
    public ResponseEntity<List<CartItem>> getCartItems(@PathVariable Integer userId) {
        List<CartItem> cartItems = cartService.getCartItems(userId);
        return ResponseEntity.status(HttpStatus.OK).body(cartItems); // 200 OK
    }

    // 新增商品到購物車（POST /users/{userId}/cart）
    @PostMapping("/users/{userId}/cart")
    public ResponseEntity<CartItem> addCartItem(
            @PathVariable Integer userId,
            @RequestBody @Valid CreateCartItemRequest createCartItemRequest
    ) {
        CartItem cartItem = cartService.addCartItem(userId, createCartItemRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(cartItem); // 201 Created
    }

    // 修改商品數量（PUT /users/{userId}/cart/{cartItemId}）
    @PutMapping("/users/{userId}/cart/{cartItemId}")
    public ResponseEntity<CartItem> updateCartItemQuantity(
            @PathVariable Integer userId,
            @PathVariable Integer cartItemId,
            @RequestBody @Valid UpdateCartItemRequest updateCartItemRequest
    ) {
        CartItem updatedCartItem = cartService.updateCartItem(userId, cartItemId, updateCartItemRequest.getQuantity());
        return ResponseEntity.status(HttpStatus.OK).body(updatedCartItem); // 200 OK
    }
}
