package com.jensen.springbootmall.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

// CartItem: 表示購物車中的一筆項目，包含用戶、商品、數量等資訊
public class CartItem {
    private Integer cartItemId; // 購物車項目 ID，主鍵
    private Integer userId; // 用戶 ID，關聯 user 表
    private Integer productId; // 商品 ID，關聯 product 表
    private Integer quantity; // 購買數量
    private Date createdDate; // 創建時間
    private Date lastModifiedDate; // 最後修改時間

    private String productName;
    private String imageUrl;

    private Integer unitPrice;


    // 自動計算總價（不存 DB）
    @JsonProperty("totalPrice")
    public Integer getTotalPrice() {
        return quantity * unitPrice;
    }


    // Getter and Setter

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
