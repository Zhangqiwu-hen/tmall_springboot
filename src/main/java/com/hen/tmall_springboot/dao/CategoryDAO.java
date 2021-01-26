package com.hen.tmall_springboot.dao;

import com.hen.tmall_springboot.pojo.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryDAO extends JpaRepository<Category, Integer> {
}
