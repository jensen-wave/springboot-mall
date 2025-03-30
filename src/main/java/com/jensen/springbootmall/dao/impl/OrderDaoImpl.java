package com.jensen.springbootmall.dao.impl;

import com.jensen.springbootmall.dao.OrderDao;
import com.jensen.springbootmall.dao.rowmapper.OrderItemRowMapper;
import com.jensen.springbootmall.dao.rowmapper.OrderRowMapper;
import com.jensen.springbootmall.dto.OrderQueryParams;
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

    // 根據訂單ID查詢單一訂單的詳細資料
    @Override
    public Order getOrderById(Integer orderId) {
        // 定義 SQL 查詢語句，從 `order` 表中根據訂單ID查詢訂單資料
        String sql = "select order_id,user_id, total_amount, created_date, last_modified_date from `order` where order_id=:orderId";

        // 設定查詢條件
        Map<String, Object> map = new HashMap<>();
        map.put("orderId", orderId);

        // 執行查詢並返回結果
        List<Order> orderList = namedParameterJdbcTemplate.query(sql, map, new OrderRowMapper());

        // 如果查詢結果不為空，則返回第一筆訂單
        if (!orderList.isEmpty()) {
            return orderList.get(0);
        } else {
            return null; // 若查無訂單，返回 null
        }
    }

    // 計算符合條件的訂單數量
    @Override
    public Integer countOrder(OrderQueryParams orderQueryParams) {
        // 定義 SQL 查詢語句，計算符合條件的訂單數量
        String sql = "SELECT count(*) FROM `order` WHERE 1=1";

        // 使用 Map 存放查詢條件
        Map<String, Object> map = new HashMap<>();

        // 根據查詢條件組合 SQL 語句
        sql = addFilteringSql(sql, map, orderQueryParams);

        // 執行查詢並返回結果
        Integer count = namedParameterJdbcTemplate.queryForObject(sql, map, Integer.class);
        return count;
    }

    // 根據查詢條件獲取訂單列表
    @Override
    public List<Order> getOrders(OrderQueryParams orderQueryParams) {
        // 定義 SQL 查詢語句，從 `order` 表中查詢訂單資料
        String sql = "select order_id,user_id, total_amount, created_date, last_modified_date from `order` where 1=1";

        // 使用 Map 存放查詢條件
        Map<String, Object> map = new HashMap<>();
        // 根據查詢條件組合 SQL 語句
        sql = addFilteringSql(sql, map, orderQueryParams);

        // 按照創建日期降序排列
        sql = sql + " order by created_date desc";
        // 設定分頁
        sql = sql + " limit :limit offSet :offSet";

        // 設置分頁參數
        map.put("limit", orderQueryParams.getLimit());
        map.put("offSet", orderQueryParams.getOffSet());

        // 執行查詢並返回結果
        List<Order> orderList = namedParameterJdbcTemplate.query(sql, map, new OrderRowMapper());
        return orderList;
    }

    // 用來處理查詢條件的 SQL 拼接
    private String addFilteringSql(String sql, Map<String, Object> map, OrderQueryParams orderQueryParams) {
        // 如果有提供 userId 查詢條件，則將其加入 SQL 查詢語句中
        if (orderQueryParams.getUserId() != null) {
            sql = sql + " and user_id=:userId";
            map.put("userId", orderQueryParams.getUserId());
        }
        return sql; // 返回處理過的 SQL 語句
    }

    // 根據訂單ID查詢該訂單的所有訂單項目（商品）
    @Override
    public List<OrderItem> getOrderItemByOrderId(Integer orderId) {
        // 定義 SQL 查詢語句，從 `order_item` 表中查詢訂單項目資料
        String sql = "select order_item_id,order_id, oi.product_id, quantity, amount, product_name, image_url from order_item oi\n" +
                "join product p on oi.product_id=p.product_id where order_id=:orderId";

        // 設定查詢條件
        Map<String, Object> map = new HashMap<>();
        map.put("orderId", orderId);

        // 執行查詢並返回結果
        List<OrderItem> orderItemList = namedParameterJdbcTemplate.query(sql, map, new OrderItemRowMapper());
        return orderItemList != null ? orderItemList : null; // 返回查詢結果，若查無訂單項目則返回 null
    }

    // 創建訂單
    @Override
    public Integer createOrder(Integer userId, Integer totalAmount) {
        // 定義 SQL 插入語句，將訂單資料插入 `order` 表
        String sql = "INSERT INTO `order` (user_id, total_amount, created_date, last_modified_date) " +
                "VALUES (:userId, :totalAmount, :createdDate, :lastModifiedDate)";

        // 使用 Map 存放參數
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId); // 設置訂單的用戶 ID
        map.put("totalAmount", totalAmount); // 設置訂單總金額
        Date now = new Date(); // 獲取當前時間
        map.put("createdDate", now); // 設置創建時間
        map.put("lastModifiedDate", now); // 設置最後修改時間

        // 用於儲存自動生成的主鍵（order_id）
        KeyHolder keyHolder = new GeneratedKeyHolder();

        // 執行 SQL 插入操作，並將自動生成的主鍵返回
        namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource(map), keyHolder);

        // 返回資料庫生成的 order_id
        return keyHolder.getKey().intValue();
    }

    // 批量創建訂單項目
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
            parameterSources[i].addValue("orderId", orderId); // 設置訂單 ID
            parameterSources[i].addValue("productId", orderItem.getProductId()); // 設置商品 ID
            parameterSources[i].addValue("quantity", orderItem.getQuantity()); // 設置商品數量
            parameterSources[i].addValue("amount", orderItem.getAmount()); // 設置商品金額
        }

        // 使用 batchUpdate 執行批量插入，效率更高
        namedParameterJdbcTemplate.batchUpdate(sql, parameterSources);
    }
}
