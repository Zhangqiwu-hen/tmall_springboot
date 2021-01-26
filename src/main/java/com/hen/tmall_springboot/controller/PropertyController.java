package com.hen.tmall_springboot.controller;

import com.hen.tmall_springboot.pojo.Property;
import com.hen.tmall_springboot.service.PropertyService;
import com.hen.tmall_springboot.util.Page4Navigator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class PropertyController {

    @Autowired
    PropertyService propertyService;

    @PostMapping(value = "/properties")
    public Object add(@RequestBody Property bean) throws Exception {
        propertyService.add(bean);
        return bean;
    }

    @DeleteMapping(value = "/properties/{id}")
    public String delete(@PathVariable("id") int id) throws Exception {
        propertyService.delete(id);
        return null;
    }

    @GetMapping(value = "/properties/{id}")
    public Property get(@PathVariable("id") int id) throws Exception {
        return propertyService.get(id);
    }

    @PutMapping(value = "/properties")
    public Object update(@RequestBody Property bean) throws Exception {
        propertyService.update(bean);
        return bean;
    }

    @GetMapping(value = "/categories/{cid}/properties")
    public Page4Navigator<Property> list(@PathVariable("cid") int cid,
                                         @RequestParam(value = "start", defaultValue = "0") int start,
                                         @RequestParam(value = "size", defaultValue = "5") int size) throws Exception {
        start = start < 0 ? 0 : start;
        return propertyService.list(cid, start, size, 5);
    }
}
