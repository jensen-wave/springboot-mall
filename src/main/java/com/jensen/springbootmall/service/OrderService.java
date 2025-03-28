package com.jensen.springbootmall.service;

import com.jensen.springbootmall.dto.CreateOrderRequest;
import com.jensen.springbootmall.model.Order;

public interface OrderService {
    Integer createOrder(Integer UserId, CreateOrderRequest createOrderRequest);
    Order getOrderById(Integer orderId);
}
