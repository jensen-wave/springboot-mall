package com.jensen.springbootmall.dao;

import com.jensen.springbootmall.dto.ProductRequest;
import com.jensen.springbootmall.model.Product;

public interface ProductDao {
    Product getProductById(Integer productId);
    Integer createProduct(ProductRequest productRequest);
    void updateProduct(Integer productId,ProductRequest productRequest);

}
