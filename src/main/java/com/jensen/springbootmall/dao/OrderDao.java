package com.jensen.springbootmall.dao;

import com.jensen.springbootmall.model.Order;
import com.jensen.springbootmall.model.OrderItem;

import java.util.List;

public interface OrderDao {
    Integer createOrder(Integer userId, Integer totalAmount);
    void createOrderItems(Integer orderId, List<OrderItem> orderItemList);

    Order getOrderById(Integer orderId);
    List<OrderItem> getOrderItemByOrderId(Integer orderId);
}
