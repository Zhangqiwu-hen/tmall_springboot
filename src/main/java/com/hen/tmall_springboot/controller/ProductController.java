package com.hen.tmall_springboot.controller;

import com.hen.tmall_springboot.pojo.Product;
import com.hen.tmall_springboot.service.ProductImageService;
import com.hen.tmall_springboot.service.ProductService;
import com.hen.tmall_springboot.util.Page4Navigator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class ProductController {

    @Autowired
    ProductService productService;
    @Autowired
    ProductImageService productImageService;

    @PostMapping(value = "/products")
    public Object add(@RequestBody Product bean) throws Exception {
        productService.add(bean);
        return bean;
    }

    @DeleteMapping(value = "/products/{id}")
    public String delete(@PathVariable("id") int id) throws Exception {
        productService.delete(id);
        return null;
    }

    @GetMapping(value = "/products/{id}")
    public Product get(@PathVariable("id") int id) throws Exception {
        return productService.get(id);
    }

    @PutMapping(value = "/products")
    public Object update(@RequestBody Product bean) throws Exception {
        productService.update(bean);
        return bean;
    }

    @GetMapping(value = "/categories/{cid}/products")
    public Page4Navigator<Product> list(@PathVariable("cid") int cid,
                                        @RequestParam(value = "start", defaultValue = "0") int start,
                                        @RequestParam(value = "size", defaultValue = "5") int size) throws Exception {
        start = start < 0 ? 0 : start;
        Page4Navigator<Product> page = productService.list(cid, start, size, 5);
        productImageService.setFirstProductImages(page.getContent());
        return page;
    }
}
