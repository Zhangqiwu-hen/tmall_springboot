package com.hen.tmall_springboot.service;

import com.hen.tmall_springboot.dao.PropertyDAO;
import com.hen.tmall_springboot.pojo.Category;
import com.hen.tmall_springboot.pojo.Property;
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
@CacheConfig(cacheNames = "properties")
public class PropertyService {

    @Autowired
    PropertyDAO propertyDAO;
    @Autowired
    CategoryService categoryService;

    @CacheEvict(allEntries = true)
    public void add(Property bean) {
        propertyDAO.save(bean);
    }

    @CacheEvict(allEntries = true)
    public void delete(int id) {
        propertyDAO.deleteById(id);
    }

    @Cacheable(key = "'properties-one-'+ #p0")
    public Property get(int id) {
        Optional<Property> optional = propertyDAO.findById(id);
        Property property = optional.get();
        return property;
    }

    @CacheEvict(allEntries = true)
    public void update(Property bean) {
        propertyDAO.save(bean);
    }

    @Cacheable(key = "'properties-cid-'+#p0+'-page-'+#p1 + '-' + #p2 ")
    public Page4Navigator<Property> list(int cid, int start, int size, int navigatePages) {
        Category category = (Category) categoryService.get(cid);
        Pageable pageable = PageRequest.of(start, size);
        Page<Property> pageFromJpa = propertyDAO.findByCategory(category, pageable);
        return new Page4Navigator<>(pageFromJpa, navigatePages);
    }

    @Cacheable(key = "'properties-cid-'+ #p0.id")
    public List<Property> listByCategory(Category category) {
        return propertyDAO.findByCategory(category);
    }

}
