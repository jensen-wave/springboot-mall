package com.jensen.springbootmall.dao.impl;

import com.jensen.springbootmall.constant.ProductCategory;
import com.jensen.springbootmall.dto.ProductQueryParams;
import com.jensen.springbootmall.dto.ProductRequest;
import com.jensen.springbootmall.dao.ProductDao;
import com.jensen.springbootmall.dao.rowmapper.ProductRowMapper;
import com.jensen.springbootmall.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ProductDaoImpl implements ProductDao {

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public void updateStock(Integer productId, Integer stock) {
        String sql = "update product set stock=:stock,last_modified_date=:lastModifiedDate " +
                "where product_id=:productId";
        Map<String, Object> map = new HashMap<>();
        map.put("stock", stock);
        map.put("productId", productId);
        Date now = new Date();
        map.put("lastModifiedDate", now);
        namedParameterJdbcTemplate.update(sql, map);
    }

    @Override
    public Product getProductById(Integer productId) {
        String sql = "select product_id,product_name, category, image_url, price, stock, description, " +
                "created_date, last_modified_date " +
                "from product where product_id=:productId;";

        Map<String, Object> map = new HashMap<>();
        map.put("productId", productId);
        List<Product> productList = namedParameterJdbcTemplate.query(sql, map, new ProductRowMapper());

        if (productList.size() > 0) {
            return productList.get(0);
        } else {
            return null;
        }

    }

    @Override
    public Product getProductByIdForUpdate(Integer productId) {
        String sql = "select product_id,product_name, category, image_url, price, stock, description, " +
                "created_date, last_modified_date " +
                "from product where product_id=:productId for update;";

        Map<String, Object> map = new HashMap<>();
        map.put("productId", productId);
        List<Product> productList = namedParameterJdbcTemplate.query(sql, map, new ProductRowMapper());

        if (productList.size() > 0) {
            return productList.get(0);
        } else {
            return null;
        }
    }

    @Override
    public Integer createProduct(ProductRequest productRequest) {
        String sql = "INSERT INTO product (product_name, category, image_url, price, stock, description, created_date, last_modified_date) " +
                "VALUES (:productName, :category, :imageUrl, :price, :stock, :description, :createdDate, :lastModifiedDate)";
        ;

        Map<String, Object> map = new HashMap<>();
        map.put("productName", productRequest.getProductName());
        map.put("category", productRequest.getCategory().name());
        map.put("imageUrl", productRequest.getImageUrl());
        map.put("price", productRequest.getPrice());
        map.put("stock", productRequest.getStock());
        map.put("description", productRequest.getDescription());
        Date now = new Date();
        map.put("createdDate", now);
        map.put("lastModifiedDate", now);
        KeyHolder keyHolder = new GeneratedKeyHolder();

        int update = namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource(map), keyHolder);

        return keyHolder.getKey().intValue();
    }

    @Override
    public void updateProduct(Integer productId, ProductRequest productRequest) {

        String sql = "update product set product_name=:productName, category=:category, image_url=:imageUrl, price=:price, stock=:stock, description=:description, last_modified_date=:lastModifiedDate where product_id=:productId";

        Map<String, Object> map = new HashMap<>();
        map.put("productId", productId);
        map.put("productName", productRequest.getProductName());
        map.put("category", productRequest.getCategory().toString());
        map.put("imageUrl", productRequest.getImageUrl());
        map.put("price", productRequest.getPrice());
        map.put("stock", productRequest.getStock());
        map.put("description", productRequest.getDescription());
        map.put("lastModifiedDate", new Date());


        namedParameterJdbcTemplate.update(sql, map);
    }

    @Override
    public List<Product> getProducts(ProductQueryParams productQueryParams) {
        String sql = "select product_id,product_name, category, image_url, price, stock, description, " +
                "created_date, last_modified_date " +
                "from product where 1=1";

        Map<String, Object> map = new HashMap<>();

//        //查詢條件
//        if (productQueryParams.getCategory() != null) {
//            sql = sql + " AND category = :category";
//            map.put("category", productQueryParams.getCategory().name());
//        }
//
//        if (productQueryParams.getSearch() != null) {
//            sql = sql + " AND product_name like :search";
//            map.put("search", "%" + productQueryParams.getSearch() + "%");
//        }
         sql = addFilteringSql(sql, map, productQueryParams);


        // 排序
        sql = sql + " ORDER BY " + productQueryParams.getOrderBy() + " " + productQueryParams.getSort();
        // 分頁
        sql = sql + " limit :limit OFFSET :offSet";

        map.put("limit", productQueryParams.getLimit());
        map.put("offSet", productQueryParams.getOffset());

        List<Product> productList = namedParameterJdbcTemplate.query(sql, map, new ProductRowMapper());
        return productList;
    }

    @Override
    public void deleteProductById(Integer productId) {
        String sql = "delete from product where product_id=:productId";
        Map<String, Object> map = new HashMap<>();
        map.put("productId", productId);
        namedParameterJdbcTemplate.update(sql, map);

    }

    @Override
    public Integer countProduct(ProductQueryParams productQueryParams) {

        String sql = "select count(*) from product WHERE 1=1";

        Map<String, Object> map = new HashMap<>();

//        //查詢條件
//        if (productQueryParams.getCategory() != null) {
//            sql = sql + " AND category = :category";
//            map.put("category", productQueryParams.getCategory().name());
//        }
//
//        if (productQueryParams.getSearch() != null) {
//            sql = sql + " AND product_name like :search";
//            map.put("search", "%" + productQueryParams.getSearch() + "%");
//        }

        sql = addFilteringSql(sql, map, productQueryParams);

        Integer total = namedParameterJdbcTemplate.queryForObject(sql, map, Integer.class);

        return total;
    }

    private String addFilteringSql(String sql, Map<String, Object> map, ProductQueryParams productQueryParams) {
        if (productQueryParams.getCategory() != null) {
            sql += " AND category = :category";
            map.put("category", productQueryParams.getCategory().name());
        }
        if (productQueryParams.getSearch() != null) {
            sql += " AND product_name LIKE :search";
            map.put("search", "%" + productQueryParams.getSearch() + "%");
        }
        return sql;
    }


}
