package com.hen.tmall_springboot.service;

import com.hen.tmall_springboot.dao.ReviewDAO;
import com.hen.tmall_springboot.pojo.Product;
import com.hen.tmall_springboot.pojo.Review;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@CacheConfig(cacheNames = {"reviews"})
public class ReviewService {

    @Autowired
    ReviewDAO reviewDAO;

    @CacheEvict(allEntries = true)
    public void add(Review review) {
        this.reviewDAO.save(review);
    }

    @Cacheable(key = "'reviews-count-pid-'+ #p0.id")
    public int getCount(Product product) {
        return this.reviewDAO.countByProduct(product);
    }

    @Cacheable(key = "'reviews-pid-'+ #p0.id")
    public List<Review> list(Product product) {
        return this.reviewDAO.findByProductOrderByIdDesc(product);
    }
}