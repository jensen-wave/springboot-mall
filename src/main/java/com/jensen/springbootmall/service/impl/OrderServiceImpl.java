package com.jensen.springbootmall.service.impl;

import com.jensen.springbootmall.dao.OrderDao;
import com.jensen.springbootmall.dao.ProductDao;
import com.jensen.springbootmall.dao.UserDao;
import com.jensen.springbootmall.dto.BuyItem;
import com.jensen.springbootmall.dto.CreateOrderRequest;
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

@Component
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderDao orderDao; // 注入 OrderDao，負責操作訂單相關資料庫操作
    @Autowired
    private ProductDao productDao; // 注入 ProductDao，負責操作商品相關資料庫操作
    @Autowired
    private UserDao userDao;
    private static final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class); // 新增日誌

    @Transactional // 使用 @Transactional 保證訂單創建過程中的多個資料庫操作要么全成功，要么全失敗
    @Override
    public Integer createOrder(Integer userId, CreateOrderRequest createOrderRequest) {
        User user = userDao.getUserById(userId);
        if (user == null) {
            log.info("userId{}不存在", userId);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "用戶不存在");
        }

        int totalAmount = 0; // 訂單的總金額
        List<OrderItem> orderItemList = new ArrayList<>(); // 用來儲存訂單的商品項目

        List<BuyItem> buyItemList = createOrderRequest.getBuyItemList(); // 獲取前端傳來的商品購買清單

        // 遍歷商品清單，計算每個商品的花費並累加
        for (BuyItem buyItem : buyItemList) {
            Product product = productDao.getProductById(buyItem.getProductId()); // 根據商品ID查詢商品資訊
            // 檢查商品是否存在
            if (product == null) {
                log.info("商品{}不存在",buyItem.getProductId());
                throw  new ResponseStatusException(HttpStatus.BAD_REQUEST,"商品不存在");
            }
            // 檢查庫存是否足夠
            int remainingStock=product.getStock();
            if (remainingStock<buyItem.getQuantity()) {
                log.info("商品 {} 庫存不足，無法購買，剩餘庫存：{}，欲購買數量：{}", product.getProductName(),remainingStock,buyItem.getQuantity());
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "庫存不足");
            }

            // 計算單個商品的花費：數量 * 價格
            int amount = buyItem.getQuantity() * product.getPrice();
            totalAmount += amount; // 累加總金額

            //更新庫存
            productDao.updateStock(buyItem.getProductId(),remainingStock-buyItem.getQuantity());

            // 轉換 BuyItem 物件為 OrderItem 物件，用來儲存訂單中的商品資訊
            OrderItem orderItem = new OrderItem();
            orderItem.setProductId(buyItem.getProductId()); // 設定商品ID
            orderItem.setQuantity(buyItem.getQuantity()); // 設定商品數量
            orderItem.setAmount(amount); // 設定商品花費
            orderItemList.add(orderItem); // 把這個訂單項目加入到訂單項目清單中
        }

        // 在資料庫中創建訂單，並取得新創建的訂單ID
        Integer orderId = orderDao.createOrder(userId, totalAmount);
        // 創建訂單項目（將訂單ID和商品項目保存到資料庫中）
        orderDao.createOrderItems(orderId, orderItemList);

        return orderId; // 返回創建的訂單ID
    }

    @Override
    public Order getOrderById(Integer orderId) {
        Order order = orderDao.getOrderById(orderId);
        List<OrderItem> orderItemList = orderDao.getOrderItemByOrderId(orderId);
        order.setOrderItemList(orderItemList);
        return order;
    }
}
