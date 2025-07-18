package com.jensen.springbootmall.service.impl;

import com.jensen.springbootmall.dao.OrderDao;
import com.jensen.springbootmall.dao.ProductDao;
import com.jensen.springbootmall.dao.UserDao;
import com.jensen.springbootmall.dto.BuyItem;
import com.jensen.springbootmall.dto.CreateOrderRequest;
import com.jensen.springbootmall.dto.OrderQueryParams;
import com.jensen.springbootmall.model.Order;
import com.jensen.springbootmall.model.OrderItem;
import com.jensen.springbootmall.model.Product;
import com.jensen.springbootmall.model.User;
import com.jensen.springbootmall.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

// 標記為 Spring 的組件，使其成為 Bean，由 Spring 容器管理
@Component
public class OrderServiceImpl implements OrderService {

    // 注入 OrderDao，負責操作訂單相關資料庫操作
    @Autowired
    private OrderDao orderDao;

    // 注入 ProductDao，負責操作商品相關資料庫操作
    @Autowired
    private ProductDao productDao;

    // 注入 UserDao，負責操作用戶相關資料庫操作
    @Autowired
    private UserDao userDao;

    // 初始化 SLF4J 日誌記錄器，用於記錄服務層的運行資訊
    private static final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);

    // 創建訂單的方法，使用 @Transactional 確保事務一致性
    @Transactional // 保證訂單創建過程中的多個資料庫操作要麼全成功，要麼全失敗
    @Override
    public Integer createOrder(Integer userId, CreateOrderRequest createOrderRequest) {
        // 檢查用戶是否存在
        User user = userDao.getUserById(userId);
        if (user == null) {
            log.info("userId {} 不存在", userId); // 記錄用戶不存在的日誌
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "用戶不存在"); // 拋出 400 錯誤
        }

        // 初始化訂單總金額
        int totalAmount = 0;

        // 初始化訂單項目清單，用於儲存訂單中的商品資訊
        List<OrderItem> orderItemList = new ArrayList<>();

        // 獲取前端傳來的購買商品清單
        List<BuyItem> buyItemList = createOrderRequest.getBuyItemList();

        // 遍歷購買清單，計算總金額並驗證商品與庫存
        for (BuyItem buyItem : buyItemList) {
            // 根據商品 ID 查詢商品資訊
            Product product = productDao.getProductByIdForUpdate(buyItem.getProductId());

            // 檢查商品是否存在
            if (product == null) {
                log.info("商品 {} 不存在", buyItem.getProductId()); // 記錄商品不存在的日誌
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "商品不存在"); // 拋出 400 錯誤
            }

            // 檢查庫存是否足夠
            int remainingStock = product.getStock();
            if (remainingStock < buyItem.getQuantity()) {
                log.info("商品 {} 庫存不足，無法購買，剩餘庫存：{}，欲購買數量：{}",
                        product.getProductName(), remainingStock, buyItem.getQuantity()); // 記錄庫存不足的日誌
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "庫存不足"); // 拋出 400 錯誤
            }

            // 計算單個商品金額：數量 * 價格
            int amount = buyItem.getQuantity() * product.getPrice();
            totalAmount += amount; // 累加至訂單總金額

            // 更新商品庫存，減少購買數量
            productDao.updateStock(buyItem.getProductId(), remainingStock - buyItem.getQuantity());

            // 創建訂單項目物件並設置屬性
            OrderItem orderItem = new OrderItem();
            orderItem.setProductId(buyItem.getProductId()); // 設置商品 ID
            orderItem.setQuantity(buyItem.getQuantity());   // 設置購買數量
            orderItem.setAmount(amount);                    // 設置單項金額
            orderItemList.add(orderItem);                   // 加入訂單項目清單
        }

        // 在資料庫中創建訂單主記錄，返回生成的訂單 ID
        Integer orderId = orderDao.createOrder(userId, totalAmount);

        // 批量創建訂單項目，將商品資訊保存至資料庫
        orderDao.createOrderItems(orderId, orderItemList);

        // 返回創建的訂單 ID
        return orderId;
    }

    // 根據訂單 ID 查詢訂單詳情，包括訂單項目
    @Override
    public Order getOrderById(Integer orderId) {
        // 查詢訂單主記錄
        Order order = orderDao.getOrderById(orderId);

        // 查詢該訂單的訂單項目列表
        List<OrderItem> orderItemList = orderDao.getOrderItemByOrderId(orderId);

        // 將訂單項目列表設置到訂單物件中
        order.setOrderItemList(orderItemList);

        // 返回完整的訂單資訊
        return order;
    }

    // 計算符合查詢條件的訂單總數
    @Override
    public Integer countOrder(OrderQueryParams orderQueryParams) {
        // 直接調用 DAO 層方法，計算訂單總數
        return orderDao.countOrder(orderQueryParams);
    }

    // 查詢符合條件的訂單列表，包含訂單項目
    @Override
    public List<Order> getOrders(OrderQueryParams orderQueryParams) {
        // 查詢訂單列表
        List<Order> orders = orderDao.getOrders(orderQueryParams);

        // 遍歷訂單列表，為每個訂單附加訂單項目
        for (Order order : orders) {
            List<OrderItem> orderItemList = orderDao.getOrderItemByOrderId(order.getOrderId());
            order.setOrderItemList(orderItemList); // 設置訂單項目
        }

        // 返回完整的訂單列表
        return orders;
    }
}
