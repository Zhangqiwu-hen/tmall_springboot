package com.hen.tmall_springboot.dao;

import com.hen.tmall_springboot.pojo.Product;
import com.hen.tmall_springboot.pojo.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductImageDAO extends JpaRepository<ProductImage, Integer> {
    List<ProductImage> findByProductAndTypeOrderByIdDesc(Product product, String type);
}
