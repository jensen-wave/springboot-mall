package com.jensen.springbootmall.dao;

import com.jensen.springbootmall.dto.UserRegisterRequest;
import com.jensen.springbootmall.model.User;

public interface UserDao {
    Integer createUser(UserRegisterRequest userRegisterRequest);
    User getUserById(Integer userId);
}
