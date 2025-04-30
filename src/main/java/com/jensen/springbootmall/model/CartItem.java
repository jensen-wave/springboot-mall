package com.jensen.springbootmall.model;

import com.fasterxml.jackson.annotation.JsonProperty; // 用來控制 JSON 輸出的屬性命名

import java.util.Date;

// CartItem: 表示購物車中的一筆項目，包含用戶、商品、數量等資訊
public class CartItem {
    private Integer cartItemId; // 購物車項目 ID，主鍵
    private Integer userId; // 用戶 ID，對應 user 表的主鍵
    private Integer productId; // 商品 ID，對應 product 表的主鍵
    private Integer quantity; // 購買該商品的數量
    private Date createdDate; // 該項目加入購物車的時間
    private Date lastModifiedDate; // 該項目最後更新的時間（如修改數量）

    // 以下欄位非資料庫欄位，為了前端展示而補充
    private String productName; // 商品名稱
    private String imageUrl; // 商品圖片 URL
    private Integer unitPrice; // 商品單價（單位價格）

    // 計算總價（quantity * unitPrice），@JsonProperty 指定輸出 JSON 的欄位名稱為 totalPrice
    @JsonProperty("totalPrice")
    public Integer getTotalPrice() {
        return quantity * unitPrice; // 回傳總價格
    }

    // Getter and Setter 方法（提供封裝欄位的存取）

    public Integer getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(Integer unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Integer getCartItemId() {
        return cartItemId;
    }

    public void setCartItemId(Integer cartItemId) {
        this.cartItemId = cartItemId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }
}
