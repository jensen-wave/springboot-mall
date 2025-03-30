package com.jensen.springbootmall.service;

import com.jensen.springbootmall.dto.CreateOrderRequest;
import com.jensen.springbootmall.dto.OrderQueryParams;
import com.jensen.springbootmall.model.Order;

import java.util.List;

public interface OrderService {
    Integer createOrder(Integer UserId, CreateOrderRequest createOrderRequest);

    Order getOrderById(Integer orderId);

    Integer countOrder(OrderQueryParams orderQueryParams);

    List<Order> getOrders(OrderQueryParams orderQueryParams);

}
