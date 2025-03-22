package com.jensen.springbootmall.dao;

import com.jensen.springbootmall.constant.ProductCategory;
import com.jensen.springbootmall.dto.ProductRequest;
import com.jensen.springbootmall.model.Product;

import java.util.List;

public interface ProductDao {
    List<Product> getProducts(ProductCategory category,String search);
    Product getProductById(Integer productId);
    Integer createProduct(ProductRequest productRequest);
    void updateProduct(Integer productId,ProductRequest productRequest);
    void deleteProductById(Integer productId);
}
