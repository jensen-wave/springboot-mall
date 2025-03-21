package com.jensen.springbootmall.service;

import com.jensen.springbootmall.dto.ProductRequest;
import com.jensen.springbootmall.model.Product;

public interface ProductService {
    Product getProductById(Integer productId);
    Integer createProduct(ProductRequest productRequest);
    void updateProduct(Integer productId,ProductRequest productRequest);
    void deleteProductById(Integer productId);
}
