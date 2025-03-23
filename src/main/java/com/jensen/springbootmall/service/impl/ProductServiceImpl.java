package com.jensen.springbootmall.service.impl;

import com.jensen.springbootmall.constant.ProductCategory;
import com.jensen.springbootmall.dto.ProductQueryParams;
import com.jensen.springbootmall.dto.ProductRequest;
import com.jensen.springbootmall.dao.ProductDao;
import com.jensen.springbootmall.model.Product;
import com.jensen.springbootmall.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

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

    @Override
    public void updateProduct(Integer productId, ProductRequest productRequest) {
        productDao.updateProduct(productId,productRequest);
    }

    @Override
    public List<Product> getProducts(ProductQueryParams productQueryParams) {
        return productDao.getProducts(productQueryParams);
    }

    @Override
    public void deleteProductById(Integer productId) {
        productDao.deleteProductById(productId);
    }

    @Override
    public Integer countProduct(ProductQueryParams productQueryParams) {
        return productDao.countProduct(productQueryParams);
    }
}
