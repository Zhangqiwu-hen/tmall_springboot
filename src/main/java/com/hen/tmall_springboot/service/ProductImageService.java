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
@CacheConfig(cacheNames = "productImages")
public class ProductImageService {

    public static String type_single = "single";
    public static String type_detail = "detail";

    @Autowired
    ProductImageDAO productImageDAO;
    @Autowired
    ProductService productService;

    @Cacheable(key = "'productImages-single-pid-'+ #p0.id")
    public List<ProductImage> listSingleProductImages(Product product) {
        return productImageDAO.findByProductAndTypeOrderByIdDesc(product, type_single);
    }

    @Cacheable(key = "'productImages-detail-pid-'+ #p0.id")
    public List<ProductImage> listDetailProductImages(Product product) {
        return productImageDAO.findByProductAndTypeOrderByIdDesc(product, type_detail);
    }

    @CacheEvict(allEntries = true)
    public void add(ProductImage productImage) {
        productImageDAO.save(productImage);
    }

    @CacheEvict(allEntries = true)
    public void delete(int id) {
        productImageDAO.deleteById(id);
    }

    @Cacheable(key = "'productImages-one-'+ #p0")
    public ProductImage get(int id) {
        Optional<ProductImage> optional = productImageDAO.findById(id);
        ProductImage productImage = optional.get();
        return productImage;
    }

    public void setFirstProductImage(Product product) {
        ProductImageService productImageService = SpringContextUtil.getBean(ProductImageService.class);
        List<ProductImage> singleImages = productImageService.listSingleProductImages(product);
        if (!singleImages.isEmpty())
            product.setFirstProductImage(singleImages.get(0));
        else
            product.setFirstProductImage(new ProductImage()); //这样做是考虑到产品还没有来得及设置图片，但是在订单后台管理里查看订单项的对应产品图片。
    }

    public void setFirstProductImages(List<Product> products) {
        for (Product product : products)
            setFirstProductImage(product);
    }

    public void setFirstProdutImagesOnOrderItems(List<OrderItem> ois) {
        for (OrderItem orderItem : ois) {
            setFirstProductImage(orderItem.getProduct());
        }
    }
}
