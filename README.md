# Springboot Mall 線上商城後端系統

一個使用 Spring Boot 架構的簡易 RESTful API，提供用戶註冊、商品管理、訂單建立等功能，適合學習與練習電商系統後端開發。

---

## 專案目標

- 練習使用 Spring Boot 建立電商後端服務
- 掌握 RESTful API 設計、資料庫整合與商業邏輯分層
- 建立具有 CRUD、驗證與查詢功能的簡易商城系統

---

##  技術棧（Tech Stack）

| 類別         | 技術 |
|--------------|------|
| Framework    | Spring Boot |
| Language     | Java 17+ |
| Build Tool   | Maven |
| Database     | MySQL（或內建 H2 測試用） |
| Libraries    | Spring Data JPA, Spring MVC, Hibernate Validator |

---

## 安裝與運行指南

### 系統需求

- Java 17+
- Maven 3.8+
- MySQL 資料庫

###  安裝步驟

```bash
git clone https://github.com/your-username/springboot-mall.git
cd springboot-mall
```

###  設定資料庫連線

在 `src/main/resources/application.properties` 中設定連線參數：

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/mall
spring.datasource.username=root
spring.datasource.password=yourpassword
```

###  啟動專案

```bash
mvn spring-boot:run
```

預設啟動網址：`http://localhost:8080`

---

##  API 文件

###  使用者相關

| 方法 | 路徑               | 功能         |
|------|--------------------|--------------|
| POST | `/users/register`  | 註冊使用者   |
| GET  | `/users/{id}`      | 查詢使用者   |

###  商品相關

| 方法 | 路徑                  | 功能         |
|------|-----------------------|--------------|
| GET  | `/products`           | 商品清單     |
| GET  | `/products/{id}`      | 單一商品查詢 |
| POST | `/products`           | 新增商品     |
| PUT  | `/products/{id}`      | 更新商品     |
| DELETE | `/products/{id}`    | 刪除商品     |

### 訂單相關

| 方法 | 路徑               | 功能         |
|------|--------------------|--------------|
| POST | `/orders`          | 建立訂單     |
| GET  | `/orders/{id}`     | 查詢訂單     |

---

##  專案結構與架構說明

```
src/
└── main/
    └── java/
        └── com.jensen.springbootmall/
            ├── controller/   // API 控制層
            ├── service/      // 商業邏輯層
            ├── repository/   // 資料庫操作層
            └── model/        // 資料模型（Entity）
```

- 架構設計：MVC 分層設計，Controller → Service → Repository

---

##  資料庫結構（ER 圖）

下圖為主要資料表的關聯：

![ER Diagram](./springboot-er.png)


## 主要功能特色

✅ 使用者註冊與查詢  
✅ 商品 CRUD 管理  
✅ 建立訂單與訂單查詢  
✅ 基礎資料驗證與錯誤處理  

---

## 測試資訊

- 使用 JUnit + Spring Boot Test 撰寫整合測試
- 使用 H2 設定測試資料庫
