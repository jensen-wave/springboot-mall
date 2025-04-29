package com.jensen.springbootmall.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

// CreateCartItemRequest: 用於封裝前端傳入的購物車項目資料
public class CreateCartItemRequest {
    // 商品 ID，必須非空且大於等於 1
    @NotNull
    @Min(1)
    private Integer productId;

    // 購買數量，必須非空且大於等於 1
    @NotNull
    @Min(1)
    private Integer quantity;


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
}
