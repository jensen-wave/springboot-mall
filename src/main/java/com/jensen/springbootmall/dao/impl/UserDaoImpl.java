package com.jensen.springbootmall.dao.impl;

import com.jensen.springbootmall.dao.UserDao; // 引入 UserDao 接口，該接口定義了用戶資料庫操作的方法
import com.jensen.springbootmall.dao.rowmapper.UserRowMapper; // 引入 UserRowMapper 類，用於將查詢結果映射為 User 對象
import com.jensen.springbootmall.dto.UserRegisterRequest; // 引入 UserRegisterRequest 類，表示註冊用戶的請求資料
import com.jensen.springbootmall.model.User; // 引入 User 類，表示用戶資料模型
import jakarta.validation.constraints.AssertFalse; // 引入 JPA 相關註解 (未使用，可刪除)
import org.springframework.beans.factory.annotation.Autowired; // 引入自動注入註解
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource; // 引入用於設定 SQL 參數的 Map 類型
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate; // 引入支持命名參數的 JdbcTemplate
import org.springframework.jdbc.support.GeneratedKeyHolder; // 引入生成自增主鍵的 KeyHolder 類
import org.springframework.jdbc.support.KeyHolder; // 引入 KeyHolder 類，表示持有 SQL 查詢後生成的主鍵
import org.springframework.stereotype.Component; // 引入 Component 註解，表示該類為 Spring 的組件

import java.util.Date; // 引入 Date 類型，用於處理時間
import java.util.HashMap; // 引入 HashMap 類型，用來存儲 SQL 查詢的參數
import java.util.List; // 引入 List 類型，用於存儲查詢結果
import java.util.Map; // 引入 Map 類型，用來設定 SQL 查詢參數

@Component // 標註該類為 Spring 管理的組件
public class UserDaoImpl implements UserDao { // 實現 UserDao 接口，提供具體的用戶資料庫操作

    @Autowired // 自動注入 NamedParameterJdbcTemplate，這是一個支持命名參數的 JdbcTemplate
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    // 創建用戶方法，將用戶註冊信息插入到資料庫
    @Override
    public Integer createUser(UserRegisterRequest userRegisterRequest) {
        // SQL 查詢語句，插入新用戶資料
        String sql="INSERT INTO `user` (email, password, created_date, last_modified_date) values (:email, :password, :createdDate, :lastModifiedDate)";

        // 創建 Map 來存儲 SQL 查詢參數
        Map<String,Object> map=new HashMap<>();
        map.put("email",userRegisterRequest.getEmail()); // 設置用戶的電子郵件
        map.put("password",userRegisterRequest.getPassword()); // 設置用戶的密碼

        // 設置創建時間與最後修改時間
        Date now = new Date();
        map.put("createdDate",now);
        map.put("lastModifiedDate",now);

        // 創建 KeyHolder 用於接收自增主鍵
        KeyHolder keyHolder = new GeneratedKeyHolder();

        // 使用 namedParameterJdbcTemplate 執行 SQL 查詢，並將主鍵存儲到 keyHolder 中
        namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource(map), keyHolder);

        // 返回生成的用戶 ID
        return keyHolder.getKey().intValue();
    }

    // 根據用戶 ID 查詢用戶資料
    @Override
    public User getUserById(Integer userId) {
        // SQL 查詢語句，根據 userId 查詢用戶資料
        String sql="select user_id,email,password,created_date,last_modified_date from `user` where user_id=:userId";

        // 創建 Map 來存儲 SQL 查詢參數
        Map<String,Object> map=new HashMap<>();
        map.put("userId",userId); // 設置查詢的 userId

        // 使用 namedParameterJdbcTemplate 執行 SQL 查詢，並使用 UserRowMapper 將查詢結果轉換為 User 對象
        List<User> userList = namedParameterJdbcTemplate.query(sql, map, new UserRowMapper());

        // 如果查詢結果非空，返回第一個用戶資料，否則返回 null
        if (userList.size()>0) {
            return userList.get(0);
        }else {
            return null;
        }
    }

    // 根據電子郵件查詢用戶資料
    @Override
    public User getUserByEmail(String email) {
        // SQL 查詢語句，根據 email 查詢用戶資料
        String sql="select user_id,email,password,created_date,last_modified_date from `user` where email=:email";

        // 創建 Map 來存儲 SQL 查詢參數
        Map<String,Object> map=new HashMap<>();
        map.put("email",email); // 設置查詢的 email

        // 使用 namedParameterJdbcTemplate 執行 SQL 查詢，並使用 UserRowMapper 將查詢結果轉換為 User 對象
        List<User> userList = namedParameterJdbcTemplate.query(sql, map, new UserRowMapper());

        // 如果查詢結果非空，返回第一個用戶資料，否則返回 null
        if (userList.size()>0) {
            return userList.get(0);
        }else {
            return null;
        }
    }
}
