package com.jensen.springbootmall.controller; // 定義包路徑

import com.fasterxml.jackson.databind.ObjectMapper; // 導入 JSON 轉換工具類
import com.jensen.springbootmall.constant.ProductCategory; // 導入商品分類枚舉
import com.jensen.springbootmall.dto.ProductRequest; // 導入商品請求數據傳輸對象
import org.junit.jupiter.api.Test; // 導入 JUnit 測試註解
import org.springframework.beans.factory.annotation.Autowired; // 導入 Spring 自動注入註解
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc; // 導入自動配置 MockMvc 註解
import org.springframework.boot.test.context.SpringBootTest; // 導入 Spring Boot 測試註解
import org.springframework.http.MediaType; // 導入 HTTP 媒體類型定義
import org.springframework.test.web.servlet.MockMvc; // 導入模擬 MVC 測試工具
import org.springframework.test.web.servlet.RequestBuilder; // 導入請求構建工具
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders; // 導入 MockMvc 請求構建器
import org.springframework.transaction.annotation.Transactional; // 導入事務管理註解

import static org.hamcrest.Matchers.*; // 導入 Hamcrest 匹配器
import static org.junit.jupiter.api.Assertions.*; // 導入 JUnit 斷言
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath; // 導入 JSON 路徑匹配器
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print; // 導入結果打印處理器
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status; // 導入狀態碼匹配器

@SpringBootTest // 配置為 Spring Boot 測試環境
@AutoConfigureMockMvc // 自動配置 MockMvc 測試環境
public class ProductControllerTest { // 定義 ProductController 的測試類

    @Autowired // 自動注入 MockMvc 實例
    private MockMvc mockMvc; // 聲明 MockMvc 用於模擬 HTTP 請求

    private ObjectMapper objectMapper = new ObjectMapper(); // 創建 ObjectMapper 實例用於 JSON 序列化

    // 查詢商品
    @Test // 標記為測試方法
    public void getProduct_success() throws Exception { // 測試成功查詢商品的場景
        RequestBuilder requestBuilder = MockMvcRequestBuilders // 創建請求構建器
                .get("/products/{productId}", 1); // 構建 GET 請求，查詢 ID 為 1 的商品

        mockMvc.perform(requestBuilder) // 執行模擬請求
                .andDo(print()) // 打印請求和響應詳情
                .andExpect(status().isOk()) // 驗證響應狀態碼為 200
                .andExpect(jsonPath("$.productName", equalTo("蘋果（澳洲）"))) // 驗證商品名稱為“蘋果（澳洲）”
                .andExpect(jsonPath("$.category", equalTo("FOOD"))) // 驗證商品分類為 FOOD
                .andExpect(jsonPath("$.imageUrl", notNullValue())) // 驗證圖片 URL 不為空
                .andExpect(jsonPath("$.price", notNullValue())) // 驗證價格不為空
                .andExpect(jsonPath("$.stock", notNullValue())) // 驗證庫存不為空
                .andExpect(jsonPath("$.description", notNullValue())) // 驗證描述不為空
                .andExpect(jsonPath("$.createdDate", notNullValue())) // 驗證創建日期不為空
                .andExpect(jsonPath("$.lastModifiedDate", notNullValue())); // 驗證最後修改日期不為空
    }

    @Test // 標記為測試方法
    public void getProduct_notFound() throws Exception { // 測試查詢不存在商品的場景
        RequestBuilder requestBuilder = MockMvcRequestBuilders // 創建請求構建器
                .get("/products/{productId}", 20000); // 構建 GET 請求，查詢 ID 為 20000 的商品

        mockMvc.perform(requestBuilder) // 執行模擬請求
                .andExpect(status().is(404)); // 驗證響應狀態碼為 404（未找到）
    }

    // 創建商品
    @Transactional // 添加事務管理，測試後回滾數據
    @Test // 標記為測試方法
    public void createProduct_success() throws Exception { // 測試成功創建商品的場景
        ProductRequest productRequest = new ProductRequest(); // 創建商品請求對象
        productRequest.setProductName("test food product"); // 設置商品名稱
        productRequest.setCategory(ProductCategory.FOOD); // 設置商品分類為 FOOD
        productRequest.setImageUrl("http://test.com"); // 設置商品圖片 URL
        productRequest.setPrice(100); // 設置商品價格
        productRequest.setStock(2); // 設置商品庫存

        String json = objectMapper.writeValueAsString(productRequest); // 將請求對象轉換為 JSON 字符串

        RequestBuilder requestBuilder = MockMvcRequestBuilders // 創建請求構建器
                .post("/products") // 構建 POST 請求用於創建商品
                .contentType(MediaType.APPLICATION_JSON) // 設置請求內容類型為 JSON
                .content(json); // 設置請求體內容

        mockMvc.perform(requestBuilder) // 執行模擬請求
                .andExpect(status().is(201)) // 驗證響應狀態碼為 201（創建成功）
                .andExpect(jsonPath("$.productName", equalTo("test food product"))) // 驗證返回的商品名稱
                .andExpect(jsonPath("$.category", equalTo("FOOD"))) // 驗證返回的商品分類
                .andExpect(jsonPath("$.imageUrl", equalTo("http://test.com"))) // 驗證返回的圖片 URL
                .andExpect(jsonPath("$.price", equalTo(100))) // 驗證返回的價格
                .andExpect(jsonPath("$.stock", equalTo(2))) // 驗證返回的庫存
                .andExpect(jsonPath("$.description", nullValue())) // 驗證描述為空
                .andExpect(jsonPath("$.createdDate", notNullValue())) // 驗證創建日期不為空
                .andExpect(jsonPath("$.lastModifiedDate", notNullValue())); // 驗證最後修改日期不為空
    }

    @Transactional // 添加事務管理，測試後回滾數據
    @Test // 標記為測試方法
    public void createProduct_illegalArgument() throws Exception { // 測試創建商品時參數不合法的場景
        ProductRequest productRequest = new ProductRequest(); // 創建商品請求對象
        productRequest.setProductName("test food product"); // 設置商品名稱（缺少其他必填字段）

        String json = objectMapper.writeValueAsString(productRequest); // 將請求對象轉換為 JSON 字符串

        RequestBuilder requestBuilder = MockMvcRequestBuilders // 創建請求構建器
                .post("/products") // 構建 POST 請求用於創建商品
                .contentType(MediaType.APPLICATION_JSON) // 設置請求內容類型為 JSON
                .content(json); // 設置請求體內容

        mockMvc.perform(requestBuilder) // 執行模擬請求
                .andExpect(status().is(400)); // 驗證響應狀態碼為 400（壞請求）
    }

    // 更新商品
    @Transactional // 添加事務管理，測試後回滾數據
    @Test // 標記為測試方法
    public void updateProduct_success() throws Exception { // 測試成功更新商品的場景
        ProductRequest productRequest = new ProductRequest(); // 創建商品請求對象
        productRequest.setProductName("test food product"); // 設置商品名稱
        productRequest.setCategory(ProductCategory.FOOD); // 設置商品分類為 FOOD
        productRequest.setImageUrl("http://test.com"); // 設置商品圖片 URL
        productRequest.setPrice(100); // 設置商品價格
        productRequest.setStock(2); // 設置商品庫存

        String json = objectMapper.writeValueAsString(productRequest); // 將請求對象轉換為 JSON 字符串

        RequestBuilder requestBuilder = MockMvcRequestBuilders // 創建請求構建器
                .put("/products/{productId}", 3) // 構建 PUT 請求，更新 ID 為 3 的商品
                .contentType(MediaType.APPLICATION_JSON) // 設置請求內容類型為 JSON
                .content(json); // 設置請求體內容

        mockMvc.perform(requestBuilder) // 執行模擬請求
                .andExpect(status().is(200)) // 驗證響應狀態碼為 200（成功）
                .andExpect(jsonPath("$.productName", equalTo("test food product"))) // 驗證返回的商品名稱
                .andExpect(jsonPath("$.category", equalTo("FOOD"))) // 驗證返回的商品分類
                .andExpect(jsonPath("$.imageUrl", equalTo("http://test.com"))) // 驗證返回的圖片 URL
                .andExpect(jsonPath("$.price", equalTo(100))) // 驗證返回的價格
                .andExpect(jsonPath("$.stock", equalTo(2))) // 驗證返回的庫存
                .andExpect(jsonPath("$.description", nullValue())) // 驗證描述為空
                .andExpect(jsonPath("$.createdDate", notNullValue())) // 驗證創建日期不為空
                .andExpect(jsonPath("$.lastModifiedDate", notNullValue())); // 驗證最後修改日期不為空
    }

    @Transactional // 添加事務管理，測試後回滾數據
    @Test // 標記為測試方法
    public void updateProduct_illegalArgument() throws Exception { // 測試更新商品時參數不合法的場景
        ProductRequest productRequest = new ProductRequest(); // 創建商品請求對象
        productRequest.setProductName("test food product"); // 設置商品名稱（缺少其他必填字段）

        String json = objectMapper.writeValueAsString(productRequest); // 將請求對象轉換為 JSON 字符串

        RequestBuilder requestBuilder = MockMvcRequestBuilders // 創建請求構建器
                .put("/products/{productId}", 3) // 構建 PUT 請求，更新 ID 為 3 的商品
                .contentType(MediaType.APPLICATION_JSON) // 設置請求內容類型為 JSON
                .content(json); // 設置請求體內容

        mockMvc.perform(requestBuilder) // 執行模擬請求
                .andExpect(status().is(400)); // 驗證響應狀態碼為 400（壞請求）
    }

    @Transactional // 添加事務管理，測試後回滾數據
    @Test // 標記為測試方法
    public void updateProduct_productNotFound() throws Exception { // 測試更新不存在商品的場景
        ProductRequest productRequest = new ProductRequest(); // 創建商品請求對象
        productRequest.setProductName("test food product"); // 設置商品名稱
        productRequest.setCategory(ProductCategory.FOOD); // 設置商品分類為 FOOD
        productRequest.setImageUrl("http://test.com"); // 設置商品圖片 URL
        productRequest.setPrice(100); // 設置商品價格
        productRequest.setStock(2); // 設置商品庫存

        String json = objectMapper.writeValueAsString(productRequest); // 將請求對象轉換為 JSON 字符串

        RequestBuilder requestBuilder = MockMvcRequestBuilders // 創建請求構建器
                .put("/products/{productId}", 20000) // 構建 PUT 請求，更新 ID 為 20000 的商品
                .contentType(MediaType.APPLICATION_JSON) // 設置請求內容類型為 JSON
                .content(json); // 設置請求體內容

        mockMvc.perform(requestBuilder) // 執行模擬請求
                .andExpect(status().is(404)); // 驗證響應狀態碼為 404（未找到）
    }

    // 刪除商品
    @Transactional // 添加事務管理，測試後回滾數據
    @Test // 標記為測試方法
    public void deleteProduct_success() throws Exception { // 測試成功刪除商品的場景
        RequestBuilder requestBuilder = MockMvcRequestBuilders // 創建請求構建器
                .delete("/products/{productId}", 5); // 構建 DELETE 請求，刪除 ID 為 5 的商品

        mockMvc.perform(requestBuilder) // 執行模擬請求
                .andExpect(status().is(204)); // 驗證響應狀態碼為 204（無內容）
    }

    @Transactional // 添加事務管理，測試後回滾數據
    @Test // 標記為測試方法
    public void deleteProduct_deleteNonExistingProduct() throws Exception { // 測試刪除不存在商品的場景
        RequestBuilder requestBuilder = MockMvcRequestBuilders // 創建請求構建器
                .delete("/products/{productId}", 20000); // 構建 DELETE 請求，刪除 ID 為 20000 的商品

        mockMvc.perform(requestBuilder) // 執行模擬請求
                .andExpect(status().is(204)); // 驗證響應狀態碼為 204（無內容）
    }

    // 查詢商品列表
    @Test // 標記為測試方法
    public void getProducts() throws Exception { // 測試查詢商品列表的場景
        RequestBuilder requestBuilder = MockMvcRequestBuilders // 創建請求構建器
                .get("/products"); // 構建 GET 請求，查詢商品列表

        mockMvc.perform(requestBuilder) // 執行模擬請求
                .andDo(print()) // 打印請求和響應詳情
                .andExpect(status().isOk()) // 驗證響應狀態碼為 200
                .andExpect(jsonPath("$.limit", notNullValue())) // 驗證返回的分頁限制不為空
                .andExpect(jsonPath("$.offset", notNullValue())) // 驗證返回的偏移量不為空
                .andExpect(jsonPath("$.total", notNullValue())) // 驗證返回的總數不為空
                .andExpect(jsonPath("$.results", hasSize(5))); // 驗證返回的結果列表大小為 5
    }

    @Test // 標記為測試方法
    public void getProducts_filtering() throws Exception { // 測試帶篩選條件的商品列表查詢
        RequestBuilder requestBuilder = MockMvcRequestBuilders // 創建請求構建器
                .get("/products") // 構建 GET 請求，查詢商品列表
                .param("search", "B") // 添加搜索參數“B”
                .param("category", "CAR"); // 添加分類參數“CAR”

        mockMvc.perform(requestBuilder) // 執行模擬請求
                .andExpect(status().isOk()) // 驗證響應狀態碼為 200
                .andExpect(jsonPath("$.limit", notNullValue())) // 驗證返回的分頁限制不為空
                .andExpect(jsonPath("$.offset", notNullValue())) // 驗證返回的偏移量不為空
                .andExpect(jsonPath("$.total", notNullValue())) // 驗證返回的總數不為空
                .andExpect(jsonPath("$.results", hasSize(2))); // 驗證返回的結果列表大小為 2
    }

    @Test // 標記為測試方法
    public void getProducts_sorting() throws Exception { // 測試帶排序條件的商品列表查詢
        RequestBuilder requestBuilder = MockMvcRequestBuilders // 創建請求構建器
                .get("/products") // 構建 GET 請求，查詢商品列表
                .param("orderBy", "price") // 添加按價格排序參數
                .param("sort", "desc"); // 添加降序排序參數

        mockMvc.perform(requestBuilder) // 執行模擬請求
                .andDo(print()) // 打印請求和響應詳情
                .andExpect(status().isOk()) // 驗證響應狀態碼為 200
                .andExpect(jsonPath("$.limit", notNullValue())) // 驗證返回的分頁限制不為空
                .andExpect(jsonPath("$.offset", notNullValue())) // 驗證返回的偏移量不為空
                .andExpect(jsonPath("$.total", notNullValue())) // 驗證返回的總數不為空
                .andExpect(jsonPath("$.results", hasSize(5))) // 驗證返回的結果列表大小為 5
                .andExpect(jsonPath("$.results[0].productId", equalTo(6))) // 驗證第一個商品 ID 為 6
                .andExpect(jsonPath("$.results[1].productId", equalTo(5))) // 驗證第二個商品 ID 為 5
                .andExpect(jsonPath("$.results[2].productId", equalTo(7))) // 驗證第三個商品 ID 為 7
                .andExpect(jsonPath("$.results[3].productId", equalTo(4))) // 驗證第四個商品 ID 為 4
                .andExpect(jsonPath("$.results[4].productId", equalTo(2))); // 驗證第五個商品 ID 為 2
    }

    @Test // 標記為測試方法
    public void getProducts_pagination() throws Exception { // 測試帶分頁條件的商品列表查詢
        RequestBuilder requestBuilder = MockMvcRequestBuilders // 創建請求構建器
                .get("/products") // 構建 GET 請求，查詢商品列表
                .param("limit", "2") // 添加每頁限制為 2 的參數
                .param("offset", "2"); // 添加偏移量為 2 的參數

        mockMvc.perform(requestBuilder) // 執行模擬請求
                .andDo(print()) // 打印請求和響應詳情
                .andExpect(status().isOk()) // 驗證響應狀態碼為 200
                .andExpect(jsonPath("$.limit", notNullValue())) // 驗證返回的分頁限制不為空
                .andExpect(jsonPath("$.offset", notNullValue())) // 驗證返回的偏移量不為空
                .andExpect(jsonPath("$.total", notNullValue())) // 驗證返回的總數不為空
                .andExpect(jsonPath("$.results", hasSize(2))) // 驗證返回的結果列表大小為 2
                .andExpect(jsonPath("$.results[0].productId", equalTo(5))) // 驗證第一個商品 ID 為 5
                .andExpect(jsonPath("$.results[1].productId", equalTo(4))); // 驗證第二個商品 ID 為 4
    }
}