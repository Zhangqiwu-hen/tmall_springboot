package com.hen.tmall_springboot.dao;

import com.hen.tmall_springboot.pojo.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserDAO extends JpaRepository<User, Integer> {

    User getByName(String name);

    User getByNameAndPassword(String name, String password);
}
