package com.jensen.springbootmall.service.impl;

import com.jensen.springbootmall.dto.ProductRequest;
import com.jensen.springbootmall.dao.ProductDao;
import com.jensen.springbootmall.model.Product;
import com.jensen.springbootmall.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductDao productDao;

    @Override
    public Product getProductById(Integer productId) {
        Product productById = productDao.getProductById(productId);
        return productById;
    }

    @Override
    public Integer createProduct(ProductRequest productRequest) {
        return productDao.createProduct(productRequest);
    }
}
