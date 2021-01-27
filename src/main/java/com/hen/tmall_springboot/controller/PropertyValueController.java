package com.hen.tmall_springboot.controller;

import com.hen.tmall_springboot.pojo.Product;
import com.hen.tmall_springboot.pojo.PropertyValue;
import com.hen.tmall_springboot.service.ProductService;
import com.hen.tmall_springboot.service.PropertyValueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class PropertyValueController {

    @Autowired
    PropertyValueService propertyValueService;
    @Autowired
    ProductService productService;

    @GetMapping({"products/{pid}/propertyValues"})
    public List<PropertyValue> list(@PathVariable("pid") int pid) throws Exception {
        Product product = this.productService.get(pid);
        this.propertyValueService.init(product);
        List<PropertyValue> propertyValues = this.propertyValueService.list(product);
        return propertyValues;
    }

    @PutMapping({"/propertyValues"})
    public Object update(@RequestBody PropertyValue bean) throws Exception {
        String content = bean.getValue().trim();
        if (content.equals("")) {
            this.propertyValueService.delete(bean);
            return null;
        } else {
            this.propertyValueService.update(bean);
            return bean;
        }
    }
}
