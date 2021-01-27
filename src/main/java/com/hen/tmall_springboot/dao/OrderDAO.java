package com.hen.tmall_springboot.dao;

import com.hen.tmall_springboot.pojo.Order;
import com.hen.tmall_springboot.pojo.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderDAO extends JpaRepository<Order, Integer> {
    List<Order> findByUserAndStatusNotOrderByIdDesc(User user, String status);
}
