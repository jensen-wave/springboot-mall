package com.jensen.springbootmall.dao.impl;

import com.jensen.springbootmall.dao.OrderDao;
import com.jensen.springbootmall.dao.rowmapper.OrderItemRowMapper;
import com.jensen.springbootmall.dao.rowmapper.OrderRowMapper;
import com.jensen.springbootmall.model.Order;
import com.jensen.springbootmall.model.OrderItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// 標記為 Spring 的組件，使其成為 Bean，由 Spring 容器管理
@Component
public class OrderDaoImpl implements OrderDao {

    // 注入 Spring JDBC 的 NamedParameterJdbcTemplate，用於執行帶命名參數的 SQL 語句
    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public Order getOrderById(Integer orderId) {
        String sql = "select order_id,user_id, total_amount, created_date, last_modified_date from `order` where order_id=:orderId";
        Map<String, Object> map = new HashMap<>();
        map.put("orderId", orderId);
        List<Order> orderList = namedParameterJdbcTemplate.query(sql, map, new OrderRowMapper());
        if (!orderList.isEmpty()) {
            return orderList.get(0);
        } else {
            return null;
        }

    }

    @Override
    public List<OrderItem> getOrderItemByOrderId(Integer orderId) {
        String sql = "select order_item_id,order_id, oi.product_id, quantity, amount,product_name,image_url from order_item oi\n" +
                "join product p on oi.product_id=p.product_id where order_id=:orderId";
        Map<String, Object> map = new HashMap<>();
        map.put("orderId", orderId);
        List<OrderItem> orderItemList = namedParameterJdbcTemplate.query(sql, map, new OrderItemRowMapper());
        if (orderItemList != null) {
            return orderItemList;
        } else {
            return null;
        }
    }

    // 實現 OrderDao 接口的 createOrder 方法，用於創建訂單總資訊
    @Override
    public Integer createOrder(Integer userId, Integer totalAmount) {
        // 定義 SQL 插入語句，將訂單資料插入 `order` 表
        // 使用反引號（``）包裹 `order`，因為它是 MySQL 保留字
        String sql = "INSERT INTO `order` (user_id, total_amount, created_date, last_modified_date) " +
                "VALUES (:userId, :totalAmount, :createdDate, :lastModifiedDate)";

        // 創建參數映射，用於將 Java 值綁定到 SQL 的命名參數
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);         // 設置訂單的用戶 ID
        map.put("totalAmount", totalAmount); // 設置訂單總金額
        Date now = new Date();             // 獲取當前時間
        map.put("createdDate", now);       // 設置創建時間
        map.put("lastModifiedDate", now);  // 設置最後修改時間

        // 用於儲存自動生成的主鍵（order_id）
        KeyHolder keyHolder = new GeneratedKeyHolder();

        // 執行 SQL 插入操作，將參數映射轉為 MapSqlParameterSource，並儲存生成的主鍵
        namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource(map), keyHolder);

        // 返回資料庫生成的 order_id
        return keyHolder.getKey().intValue();
    }

    // 實現 OrderDao 接口的 createOrderItems 方法，用於批量創建訂單明細
    @Override
    public void createOrderItems(Integer orderId, List<OrderItem> orderItemList) {
        // 定義 SQL 插入語句，將訂單明細插入 order_item 表
        String sql = "INSERT INTO order_item (order_id, product_id, quantity, amount) " +
                "VALUES (:orderId, :productId, :quantity, :amount)";

        // 創建參數陣列，大小與 orderItemList 一致，用於批量插入
        MapSqlParameterSource[] parameterSources = new MapSqlParameterSource[orderItemList.size()];

        // 遍歷訂單明細清單，為每筆資料設置參數
        for (int i = 0; i < orderItemList.size(); i++) {
            OrderItem orderItem = orderItemList.get(i); // 獲取當前訂單明細物件
            parameterSources[i] = new MapSqlParameterSource(); // 初始化當前參數來源
            parameterSources[i].addValue("orderId", orderId);   // 設置訂單 ID，關聯至 `order` 表
            parameterSources[i].addValue("productId", orderItem.getProductId()); // 設置商品 ID
            parameterSources[i].addValue("quantity", orderItem.getQuantity());   // 設置購買數量
            parameterSources[i].addValue("amount", orderItem.getAmount());       // 設置單品項金額
        }

        // 使用 batchUpdate 執行批量插入，提高效率
        // 將 SQL 語句與參數陣列一起傳入，執行多筆插入
        namedParameterJdbcTemplate.batchUpdate(sql, parameterSources);
    }
}