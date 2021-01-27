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
@CacheConfig(cacheNames = {"categories"})
public class CategoryService {

    @Autowired
    CategoryDAO categoryDAO;

    @Cacheable(key = "'categories-page-'+#p0+ '-' + #p1")
    public Page4Navigator<Category> list(int start, int size, int navigatePages) {
        Pageable pageable = PageRequest.of(start, size);
        Page<Category> pageFromJpa = this.categoryDAO.findAll(pageable);
        return new Page4Navigator(pageFromJpa, navigatePages);
    }

    @Cacheable(key = "'categories-all'")
    public List<Category> list() {
        return this.categoryDAO.findAll();
    }

    @CacheEvict(allEntries = true)
    public void add(Category bean) {
        this.categoryDAO.save(bean);
    }

    @CacheEvict(allEntries = true)
    public void delete(int id) {
        this.categoryDAO.deleteById(id);
    }

    @CacheEvict(allEntries = true)
    public void update(Category bean) {
        this.categoryDAO.save(bean);
    }

    @Cacheable(key = "'categories-one-'+ #p0")
    public Category get(int id) {
        Optional<Category> optional = this.categoryDAO.findById(id);
        Category category = optional.get();
        return category;
    }

    public void removeCategoryFromProduct(List<Category> cs) {
        for (Category category : cs) {
            this.removeCategoryFromProduct(category);
        }

    }

    //这个方法的用处是删除Product对象上的分类。 因为在对分类做序列还转换为 json 的时候，会遍历里面的 products, 然后遍历出来的产品上，又会有分类，接着就开始子子孙孙无穷溃矣地遍历
    //而在这里去掉，就没事了。 只要在前端业务上，没有通过产品获取分类的业务，去掉也没有关系
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
