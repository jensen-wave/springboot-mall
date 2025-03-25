package com.jensen.springbootmall.service.impl; // 引入 service 層實現包

import com.jensen.springbootmall.dao.UserDao; // 引入 UserDao 接口，用於與資料庫進行交互
import com.jensen.springbootmall.dto.UserRegisterRequest; // 引入用戶註冊請求的資料傳輸對象 (DTO)
import com.jensen.springbootmall.model.User; // 引入 User 類，表示用戶模型
import com.jensen.springbootmall.service.UserService; // 引入 UserService 接口，該接口定義了用戶服務方法
import org.slf4j.Logger; // 引入 SLF4J 日誌框架的 Logger 類
import org.slf4j.LoggerFactory; // 引入 SLF4J 日誌框架的 LoggerFactory 類
import org.springframework.beans.factory.annotation.Autowired; // 引入自動注入註解
import org.springframework.http.HttpStatus; // 引入 HTTP 狀態碼
import org.springframework.stereotype.Component; // 引入 Component 註解，將該類標註為 Spring 的組件
import org.springframework.web.client.HttpClientErrorException; // 引入 HTTP 客戶端錯誤異常類 (未使用)
import org.springframework.web.client.RestClientException; // 引入 REST 客戶端異常類 (未使用)
import org.springframework.web.server.ResponseStatusException; // 引入用於拋出 HTTP 狀態錯誤的異常類

@Component // 標註該類為 Spring 管理的組件，將自動注入到 Spring 容器中
public class UserServiceImpl implements UserService { // 實現 UserService 接口，提供用戶服務功能

    private final static Logger log = LoggerFactory.getLogger(UserServiceImpl.class); // 設置日誌記錄器，用於記錄警告和錯誤信息

    @Autowired // 自動注入 UserDao，用於操作資料庫
    private UserDao userDao;

    // 註冊用戶的方法
    @Override
    public Integer register(UserRegisterRequest userRegisterRequest) {
        // 檢查是否已有註冊用戶擁有相同的 email
        User user = userDao.getUserByEmail(userRegisterRequest.getEmail());
        if (user != null) {
            // 如果 email 已經存在，則記錄警告日誌並拋出 BAD_REQUEST 錯誤
            log.warn("該email {} 已經被註冊", userRegisterRequest.getEmail());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST); // 拋出 400 錯誤
        }

        // 如果 email 不存在，則創建新用戶帳號
        return userDao.createUser(userRegisterRequest);
    }

    // 根據用戶 ID 獲取用戶資料
    @Override
    public User getUserById(Integer userId) {
        // 調用 userDao 層的 getUserById 方法從資料庫中查詢用戶
        return userDao.getUserById(userId);
    }
}
