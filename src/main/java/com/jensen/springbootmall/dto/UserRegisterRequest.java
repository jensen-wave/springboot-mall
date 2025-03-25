package com.jensen.springbootmall.dto; // 引入 DTO (資料傳輸對象) 包

import jakarta.validation.constraints.Email; // 引入電子郵件驗證註解
import jakarta.validation.constraints.NotBlank; // 引入非空白驗證註解

// 用戶註冊請求 DTO 類
public class UserRegisterRequest {

    // 註解: email 必須是有效的電子郵件格式，且不能為空
    @NotBlank  // 驗證 email 不能為空或只包含空格
    @Email    // 驗證 email 必須是合法的電子郵件格式
    private String email;

    // 註解: password 不能為空
    @NotBlank // 驗證 password 不能為空或只包含空格
    private String password;

    // 獲取 email 屬性值的方法
    public @NotBlank String getEmail() {
        return email;
    }

    // 設置 email 屬性值的方法
    public void setEmail(@NotBlank String email) {
        this.email = email;
    }

    // 獲取 password 屬性值的方法
    public @NotBlank String getPassword() {
        return password;
    }

    // 設置 password 屬性值的方法
    public void setPassword(@NotBlank String password) {
        this.password = password;
    }
}
