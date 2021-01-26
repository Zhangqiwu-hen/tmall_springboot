package com.hen.tmall_springboot.service;

import com.hen.tmall_springboot.dao.CategoryDAO;
import com.hen.tmall_springboot.pojo.Category;
import com.hen.tmall_springboot.pojo.Product;
import com.hen.tmall_springboot.util.Page4Navigator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@CacheConfig(cacheNames = "categories")
public class CategoryService {

    @Autowired
    CategoryDAO categoryDAO;

    @Cacheable(key = "'categories-page-'+#p0+ '-' + #p1")
    public Page4Navigator<Category> list(int start, int size, int navigatePages) {
        Pageable pageable = PageRequest.of(start, size);
        Page<Category> pageFromJpa = categoryDAO.findAll(pageable);
        return new Page4Navigator<>(pageFromJpa, navigatePages);
    }

    @Cacheable(key = "'categories-all'")
    public List<Category> list() {
        return categoryDAO.findAll();
    }

    @CacheEvict(allEntries = true)
    public void add(Category bean) {
        categoryDAO.save(bean);
    }

    @CacheEvict(allEntries = true)
    public void delete(int id) {
        categoryDAO.deleteById(id);
    }

    @CacheEvict(allEntries = true)
    public void update(Category bean) {
        categoryDAO.save(bean);
    }

    @Cacheable(key = "'categories-one-'+ #p0")
    public Category get(int id) {
        Optional<Category> optional = categoryDAO.findById(id);
        Category category = optional.get();
        return category;
    }

    public void removeCategoryFromProduct(List<Category> cs) {
        for (Category category : cs) {
            removeCategoryFromProduct(category);
        }
    }

    public void removeCategoryFromProduct(Category category) {
        List<Product> products = category.getProducts();
        if (null != products) {
            for (Product product : products) {
                product.setCategory(null);
            }
        }

        List<List<Product>> productsByRow = category.getProductsByRow();
        if (null != productsByRow) {
            for (List<Product> ps : productsByRow) {
                for (Product p : ps) {
                    p.setCategory(null);
                }
            }
        }
    }
}
