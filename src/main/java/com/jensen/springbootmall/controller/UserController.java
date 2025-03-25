package com.jensen.springbootmall.controller;

import com.jensen.springbootmall.dto.UserRegisterRequest; // 引入用戶註冊請求的資料傳輸對象 (DTO)
import com.jensen.springbootmall.model.User; // 引入用戶模型
import com.jensen.springbootmall.service.UserService; // 引入用戶服務層，負責處理業務邏輯
import jakarta.validation.Valid; // 引入驗證註解，用來驗證請求中的資料
import org.springframework.beans.factory.annotation.Autowired; // 引入自動注入註解
import org.springframework.http.HttpStatus; // 引入 HTTP 狀態碼
import org.springframework.http.ResponseEntity; // 引入 HTTP 響應實體
import org.springframework.web.bind.annotation.PostMapping; // 引入 POST 請求處理註解
import org.springframework.web.bind.annotation.RequestBody; // 引入請求體的註解
import org.springframework.web.bind.annotation.RestController; // 引入 REST 控制器註解

@RestController // 註明該類是 REST 控制器，處理 HTTP 請求並返回 JSON
public class UserController {

    @Autowired // 自動注入 UserService，這樣可以在控制器中使用該服務
    private UserService userService;

    // 註冊新用戶的 POST 請求處理方法
    @PostMapping("/users/register") // 定義處理 POST 請求的路徑 "/users/register"
    public ResponseEntity<User> register(@RequestBody @Valid UserRegisterRequest userRegisterRequest){
        // 使用 UserService 的 register 方法處理註冊，並返回新用戶的 ID
        Integer userId = userService.register(userRegisterRequest);

        // 使用返回的 userId 從資料庫中取得新註冊的用戶資料
        User user = userService.getUserById(userId);

        // 返回 201 Created 狀態碼，並將新用戶資料作為回應主體
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }
}
