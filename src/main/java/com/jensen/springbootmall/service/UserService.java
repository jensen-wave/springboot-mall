package com.jensen.springbootmall.service;

import com.jensen.springbootmall.dto.UserRegisterRequest;
import com.jensen.springbootmall.model.User;

public interface UserService {
    Integer register(UserRegisterRequest userRegisterRequest);
    User getUserById(Integer userId);

}
