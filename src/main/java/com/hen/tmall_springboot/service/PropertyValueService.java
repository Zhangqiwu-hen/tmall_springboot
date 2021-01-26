package com.hen.tmall_springboot.service;

import com.hen.tmall_springboot.dao.PropertyValueDAO;
import com.hen.tmall_springboot.pojo.Product;
import com.hen.tmall_springboot.pojo.Property;
import com.hen.tmall_springboot.pojo.PropertyValue;
import com.hen.tmall_springboot.util.SpringContextUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@CacheConfig(cacheNames = "propertyValues")
public class PropertyValueService {

    @Autowired
    PropertyValueDAO propertyValueDAO;
    @Autowired
    PropertyService propertyService;

    public void init(Product product) {
        PropertyValueService propertyValueService = SpringContextUtil.getBean(PropertyValueService.class);

        List<Property> properties = propertyService.listByCategory(product.getCategory());
        for (Property property : properties) {
            PropertyValue propertyValue = propertyValueService.getByProductAndProperty(product, property);
            if (null == propertyValue) {
                propertyValue = new PropertyValue();
                propertyValue.setProduct(product);
                propertyValue.setProperty(property);
                propertyValueDAO.save(propertyValue);
            }
        }
    }

    @Cacheable(key = "'propertyValues-one-pid-'+#p0.id+ '-ptid-' + #p1.id")
    public PropertyValue getByProductAndProperty(Product product, Property property) {
        return propertyValueDAO.getByProductAndProperty(product, property);
    }

    @Cacheable(key = "'propertyValues-pid-'+ #p0.id")
    public List<PropertyValue> list(Product product) {
        return propertyValueDAO.findByProductOrderByIdDesc(product);
    }

    @CacheEvict(allEntries = true)
    public void update(PropertyValue bean) {
        propertyValueDAO.save(bean);
    }

    @CacheEvict(allEntries = true)
    public void delete(PropertyValue bean) {
        propertyValueDAO.delete(bean);
    }
}
