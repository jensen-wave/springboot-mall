package com.jensen.springbootmall.service;

import com.jensen.springbootmall.dto.CreateOrderRequest;

public interface OrderService {
    Integer createOrder(Integer UserId, CreateOrderRequest createOrderRequest);
}
