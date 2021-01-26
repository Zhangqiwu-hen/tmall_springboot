package com.hen.tmall_springboot.dao;

import com.hen.tmall_springboot.pojo.Category;
import com.hen.tmall_springboot.pojo.Property;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PropertyDAO extends JpaRepository<Property, Integer> {
    Page<Property> findByCategory(Category category, Pageable pageable);

    List<Property> findByCategory(Category category);
}
