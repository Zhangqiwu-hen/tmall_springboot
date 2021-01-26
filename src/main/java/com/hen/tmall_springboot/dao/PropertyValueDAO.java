package com.hen.tmall_springboot.dao;

import com.hen.tmall_springboot.pojo.Product;
import com.hen.tmall_springboot.pojo.Property;
import com.hen.tmall_springboot.pojo.PropertyValue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PropertyValueDAO extends JpaRepository<PropertyValue, Integer> {
    List<PropertyValue> findByProductOrderByIdDesc(Product product);

    PropertyValue getByProductAndProperty(Product product, Property property);
}
