package com.jensen.springbootmall.dao;

import com.jensen.springbootmall.dto.OrderQueryParams;
import com.jensen.springbootmall.model.Order;
import com.jensen.springbootmall.model.OrderItem;

import java.util.List;

public interface OrderDao {

    // 創建訂單，返回訂單ID
    // 接收用戶ID和訂單總金額，並創建訂單
    Integer createOrder(Integer userId, Integer totalAmount);

    // 創建訂單項目（訂單中的商品）
    // 接收訂單ID以及該訂單的商品列表，將商品項目插入資料庫
    void createOrderItems(Integer orderId, List<OrderItem> orderItemList);

    // 根據訂單ID查詢單一訂單的詳細資料
    // 這個方法會返回指定訂單ID的詳細訂單資料
    Order getOrderById(Integer orderId);

    // 根據訂單ID查詢該訂單的所有訂單項目（商品）
    // 返回指定訂單的所有商品項目
    List<OrderItem> getOrderItemByOrderId(Integer orderId);

    // 計算符合查詢條件的訂單數量
    // 根據給定的查詢參數（如用戶ID、分頁參數）來查詢符合條件的訂單總數
    Integer countOrder(OrderQueryParams orderQueryParams);

    // 根據查詢條件獲取訂單列表
    // 根據給定的查詢參數（如用戶ID、分頁參數）查詢符合條件的訂單數據
    List<Order> getOrders(OrderQueryParams orderQueryParams);

}
