package com.hen.tmall_springboot.service;

import com.hen.tmall_springboot.dao.UserDAO;
import com.hen.tmall_springboot.pojo.User;
import com.hen.tmall_springboot.util.Page4Navigator;
import com.hen.tmall_springboot.util.SpringContextUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@CacheConfig(cacheNames = {"users"})
public class UserService {

    @Autowired
    UserDAO userDAO;

    @Cacheable(key = "'users-page-'+#p0+ '-' + #p1")
    public Page4Navigator<User> list(int start, int size, int navigatePages) {
        Pageable pageable = PageRequest.of(start, size);
        Page<User> page = this.userDAO.findAll(pageable);
        Page4Navigator<User> page4Navigator = new Page4Navigator(page, navigatePages);
        return page4Navigator;
    }

    public boolean isExist(String name) {
        UserService userService = (UserService) SpringContextUtil.getBean(UserService.class);
        User user = userService.getByName(name);
        return null != user;
    }

    @Cacheable(key = "'users-one-name-'+ #p0")
    public User getByName(String name) {
        return this.userDAO.getByName(name);
    }

    @CacheEvict(allEntries = true)
    public void add(User user) {
        this.userDAO.save(user);
    }

    @Cacheable(key = "'users-one-name-'+ #p0 +'-password-'+ #p1")
    public User get(String name, String password) {
        return this.userDAO.getByNameAndPassword(name, password);
    }
}

