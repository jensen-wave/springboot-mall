package com.jensen.springbootmall.controller;

import com.jensen.springbootmall.constant.ProductCategory;
import com.jensen.springbootmall.dto.ProductQueryParams;
import com.jensen.springbootmall.dto.ProductRequest;
import com.jensen.springbootmall.model.Product;
import com.jensen.springbootmall.service.ProductService;
import com.jensen.springbootmall.util.Page;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.lang.module.ModuleDescriptor;
import java.util.List;

@Validated
@RestController
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping("/products")
    public ResponseEntity<Page<Product>> getProducts(
            // 查詢條件 Filtering
            @RequestParam(required = false) ProductCategory category,
            @RequestParam(required = false) String search,
            // 排序 Sorting
            @RequestParam (defaultValue = "created_date") String orderBy,
            @RequestParam (defaultValue = "desc") String sort,
            // 分頁 Pagination
            @Max (1000) @Min (0) @RequestParam (defaultValue = "5") Integer limit,
            @Min (0) @RequestParam (defaultValue = "0") Integer offset
    ) {
        ProductQueryParams productQueryParams = new ProductQueryParams();
        productQueryParams.setCategory(category);
        productQueryParams.setSearch(search);

        productQueryParams.setOrderBy(orderBy);
        productQueryParams.setSort(sort);

        productQueryParams.setLimit(limit);
        productQueryParams.setOffset(offset);
// 取得 product list
        List<Product> productList = productService.getProducts(productQueryParams);
// 取得 product 總數
       Integer total= productService.countProduct(productQueryParams);
// 分頁
        Page<Product> page = new Page<>();

        page.setLimit(limit);
        page.setOffset(offset);
        page.setTotal(total);
        page.setResults(productList);

        return ResponseEntity.status(HttpStatus.OK).body(page);
    }

    @GetMapping("/products/{productId}")
    public ResponseEntity<Product> getByProductId(@PathVariable Integer productId) {

        Product product = productService.getProductById(productId);
        if (product != null) {
            return ResponseEntity.status(HttpStatus.OK).body(product);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping("/products")
    public ResponseEntity<Product> createProduct(@RequestBody @Valid ProductRequest productRequest) {
        Integer productId = productService.createProduct(productRequest);
        Product product = productService.getProductById(productId);
        return ResponseEntity.status(HttpStatus.CREATED).body(product);
    }

    @PutMapping("/products/{productId}")
    public ResponseEntity<Product> updateProduct(@PathVariable Integer productId, @RequestBody @Valid ProductRequest productRequest) {

        Product product = productService.getProductById(productId);
        // 檢查商品是否存在
        if (product == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        // 修改商品
        productService.updateProduct(productId, productRequest);
        // 查詢修改後的商品

        Product updatedProduct = productService.getProductById(productId);
        // 回傳 200 OK
        return ResponseEntity.status(HttpStatus.OK).body(updatedProduct);
    }

    @DeleteMapping("/products/{productId}")
    public ResponseEntity<?> deleteProduct(@PathVariable Integer productId) {
        productService.deleteProductById(productId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
