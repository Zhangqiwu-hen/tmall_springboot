package com.hen.tmall_springboot.controller;

import com.hen.tmall_springboot.pojo.Category;
import com.hen.tmall_springboot.service.CategoryService;
import com.hen.tmall_springboot.util.ImageUtil;
import com.hen.tmall_springboot.util.Page4Navigator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ClassUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.File;

@RestController
public class CategoryController {

    @Autowired
    CategoryService categoryService;

    @GetMapping({"categories"})
    public Page4Navigator<Category> list(@RequestParam("start") int start, @RequestParam(value = "size", defaultValue = "5") int size) throws Exception {
        start = start < 0 ? 0 : start;
        return this.categoryService.list(start, size, 5);
    }

    @PostMapping({"categories"})
    public Object add(@RequestBody Category bean, MultipartFile image, HttpServletRequest request) throws Exception {
        this.categoryService.add(bean);
        this.saveOrUpdateImageFile(bean, image, request);
        return bean;
    }

    public void saveOrUpdateImageFile(Category bean, MultipartFile image, HttpServletRequest request) throws Exception {
        File imageFolder = new File(ClassUtils.getDefaultClassLoader().getResource("").getPath() + "static/img/category");
        File imageFile = new File(imageFolder, bean.getId() + ".jpg");
        if (!imageFile.getParentFile().exists()) {
            imageFile.getParentFile().mkdirs();
        }

        image.transferTo(imageFile);
        BufferedImage img = ImageUtil.change2jpg(imageFile);
        ImageIO.write(img, "jpg", imageFile);
    }

    @DeleteMapping({"/categories/{id}"})
    public String delete(@PathVariable("id") int id) throws Exception {
        this.categoryService.delete(id);
        File imageFolder = new File(ClassUtils.getDefaultClassLoader().getResource("").getPath() + "static/img/category");
        File imageFile = new File(imageFolder, id + ".jpg");
        imageFile.delete();
        return null;
    }

    @GetMapping({"/categories/{id}"})
    public Object get(@PathVariable("id") int id) throws Exception {
        return this.categoryService.get(id);
    }

    @PutMapping({"/categories/{id}"})
    public Object update(Category bean, MultipartFile image, HttpServletRequest request) throws Exception {
        this.categoryService.update(bean);
        if (null != image) {
            this.saveOrUpdateImageFile(bean, image, request);
        }

        return bean;
    }
}

