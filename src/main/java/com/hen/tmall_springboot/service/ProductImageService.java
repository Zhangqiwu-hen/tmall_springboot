package com.hen.tmall_springboot.service;

import com.hen.tmall_springboot.dao.ProductImageDAO;
import com.hen.tmall_springboot.pojo.OrderItem;
import com.hen.tmall_springboot.pojo.Product;
import com.hen.tmall_springboot.pojo.ProductImage;
import com.hen.tmall_springboot.util.SpringContextUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@CacheConfig(cacheNames = {"productImages"})
public class ProductImageService {

    public static String type_single = "single";
    public static String type_detail = "detail";

    @Autowired
    ProductImageDAO productImageDAO;
    @Autowired
    ProductService productService;

    @Cacheable(key = "'productImages-single-pid-'+ #p0.id")
    public List<ProductImage> listSingleProductImages(Product product) {
        return this.productImageDAO.findByProductAndTypeOrderByIdDesc(product, type_single);
    }

    @Cacheable(key = "'productImages-detail-pid-'+ #p0.id")
    public List<ProductImage> listDetailProductImages(Product product) {
        return this.productImageDAO.findByProductAndTypeOrderByIdDesc(product, type_detail);
    }

    @CacheEvict(allEntries = true)
    public void add(ProductImage productImage) {
        this.productImageDAO.save(productImage);
    }

    @CacheEvict(allEntries = true)
    public void delete(int id) {
        this.productImageDAO.deleteById(id);
    }

    @Cacheable(key = "'productImages-one-'+ #p0")
    public ProductImage get(int id) {
        Optional<ProductImage> optional = this.productImageDAO.findById(id);
        ProductImage productImage = optional.get();
        return productImage;
    }

    public void setFirstProductImage(Product product) {
        ProductImageService productImageService = SpringContextUtil.getBean(ProductImageService.class);
        List<ProductImage> singleImages = productImageService.listSingleProductImages(product);
        if (!singleImages.isEmpty()) {
            product.setFirstProductImage(singleImages.get(0));
        } else {
            product.setFirstProductImage(new ProductImage());
        }
    }

    public void setFirstProductImages(List<Product> products) {
        for (Product product : products) {
            this.setFirstProductImage(product);
        }

    }

    public void setFirstProductImagesOnOrderItems(List<OrderItem> ois) {
        for (OrderItem orderItem : ois) {
            this.setFirstProductImage(orderItem.getProduct());
        }

    }
}

