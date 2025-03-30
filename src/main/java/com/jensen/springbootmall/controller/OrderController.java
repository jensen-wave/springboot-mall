package com.jensen.springbootmall.controller;

import com.jensen.springbootmall.dto.CreateOrderRequest;
import com.jensen.springbootmall.dto.OrderQueryParams;
import com.jensen.springbootmall.model.Order;
import com.jensen.springbootmall.model.Product;
import com.jensen.springbootmall.service.OrderService;
import com.jensen.springbootmall.util.Page;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class OrderController {

    // 注入 OrderService，負責處理訂單邏輯
    @Autowired
    private OrderService orderService;

    // 查詢訂單列表的 API，根據用戶ID查詢該用戶的訂單
    @Validated
    @GetMapping("/users/{userId}/orders")
    public ResponseEntity<Page<Order>> getOrders(
            // 查詢條件：分頁參數 limit 和 offset
            @RequestParam(defaultValue = "10") @Max(1000) @Min(0) Integer limit,  // 限制每頁返回的訂單數量，最大1000
            @RequestParam(defaultValue = "0") @Min(0) Integer offSet,  // 分頁偏移量（從第幾條數據開始）
            @PathVariable Integer userId  // 路徑變數，表示查詢的用戶ID
    ) {

        // 設置查詢條件（封裝到 OrderQueryParams）
        OrderQueryParams orderQueryParams = new OrderQueryParams();
        orderQueryParams.setUserId(userId);  // 設置用戶ID
        orderQueryParams.setLimit(limit);  // 設置分頁的每頁數量
        orderQueryParams.setOffSet(offSet);  // 設置分頁的偏移量

        // 根據查詢條件取得該用戶的所有訂單列表
        List<Order> orderList = orderService.getOrders(orderQueryParams);

        // 取得訂單總數，用於計算總頁數
        Integer count = orderService.countOrder(orderQueryParams);

        // 封裝分頁資訊
        Page<Order> orderPage = new Page<>();
        orderPage.setLimit(limit);  // 設置每頁顯示的數量
        orderPage.setOffset(offSet);  // 設置當前頁的偏移量
        orderPage.setResults(orderList);  // 設置查詢到的訂單數據
        orderPage.setTotal(count);  // 設置訂單總數，方便前端計算總頁數

        // 返回包含分頁結果的 ResponseEntity
        return ResponseEntity.status(HttpStatus.OK).body(orderPage);
    }

    // 創建訂單的 API，根據用戶ID創建新的訂單
    @PostMapping("/users/{userId}/orders")
    public ResponseEntity<Order> createOrder(
            @PathVariable Integer userId,  // 從路徑中取得用戶ID
            @RequestBody @Valid CreateOrderRequest createOrderRequest  // 請求體中包含訂單創建信息，並且進行校驗
    ) {

        // 根據用戶ID及訂單創建請求創建訂單，並返回新訂單的ID
        Integer orderId = orderService.createOrder(userId, createOrderRequest);

        // 根據訂單ID查詢新創建的訂單詳細資料
        Order order = orderService.getOrderById(orderId);

        // 返回創建成功的訂單資料，HTTP狀態為 201 CREATED
        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }
}
