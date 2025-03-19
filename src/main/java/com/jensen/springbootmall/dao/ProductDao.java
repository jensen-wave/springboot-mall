package com.jensen.springbootmall.dao;

import com.jensen.springbootmall.model.Product;

public interface ProductDao {
    Product getProductById(Integer productId);
}
