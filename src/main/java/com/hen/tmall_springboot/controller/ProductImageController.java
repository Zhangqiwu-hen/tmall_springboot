package com.hen.tmall_springboot.controller;

import com.hen.tmall_springboot.pojo.Product;
import com.hen.tmall_springboot.pojo.ProductImage;
import com.hen.tmall_springboot.service.ProductImageService;
import com.hen.tmall_springboot.service.ProductService;
import com.hen.tmall_springboot.util.ImageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ClassUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

@RestController
public class ProductImageController {

    @Autowired
    ProductImageService productImageService;
    @Autowired
    ProductService productService;

    @GetMapping("/products/{pid}/productImages")
    public List<ProductImage> list(@RequestParam("type") String type, @PathVariable("pid") int pid) throws Exception {
        Product product = productService.get(pid);

        if (ProductImageService.type_single.equals(type)) {
            List<ProductImage> singles = productImageService.listSingleProductImages(product);
            return singles;
        } else if (ProductImageService.type_detail.equals(type)) {
            List<ProductImage> details = productImageService.listDetailProductImages(product);
            return details;
        } else {
            return new ArrayList<>();
        }
    }

    @PostMapping("/productImages")
    public Object add(@RequestParam("type") String type, @RequestParam("pid") int pid, MultipartFile image, HttpServletRequest request) throws Exception {
        ProductImage productImage = new ProductImage();
        productImage.setType(type);
        productImage.setProduct(productService.get(pid));
        productImageService.add(productImage);
        saveImageFile(productImage, image, request);
        return productImage;
    }

    public void saveImageFile(ProductImage productImage, MultipartFile image, HttpServletRequest request) throws Exception {
        String folder = "";
        if (ProductImageService.type_single.equals(productImage.getType())) {
            folder = "productSingle";
        } else if (ProductImageService.type_detail.equals(productImage.getType())) {
            folder = "productDetail";
        }
        File imageFolder = new File(ClassUtils.getDefaultClassLoader().getResource("").getPath() + "static/img/" + folder);
        File imageFile = new File(imageFolder, productImage.getId() + ".jpg");
        String fileName = imageFile.getName();
        if (!imageFile.getParentFile().exists())
            imageFile.getParentFile().mkdirs();
        image.transferTo(imageFile);
        BufferedImage img = ImageUtil.change2jpg(imageFile);
        ImageIO.write(img, "jpg", imageFile);

        if (ProductImageService.type_single.equals(productImage.getType())) {
            String imageFolder_small = ClassUtils.getDefaultClassLoader().getResource("").getPath() + "static/img/productSingle_small";
            String imageFolder_middle = ClassUtils.getDefaultClassLoader().getResource("").getPath() + "static/img/productSingle_middle";
            File f_small = new File(imageFolder_small, fileName);
            File f_middle = new File(imageFolder_middle, fileName);
            f_small.getParentFile().mkdirs();
            f_middle.getParentFile().mkdirs();
            ImageUtil.resizeImage(imageFile, 56, 56, f_small);
            ImageUtil.resizeImage(imageFile, 217, 190, f_middle);
        }
    }

    @DeleteMapping("productImages/{id}")
    public String delete(@PathVariable("id") int id) throws Exception {
        String type = productImageService.get(id).getType();
        productImageService.delete(id);
        String folder = "";
        if (ProductImageService.type_single.equals(type)) {
            folder = "productSingle";
        } else if (ProductImageService.type_detail.equals(type)) {
            folder = "productDetail";
        }
        File imageFolder = new File(ClassUtils.getDefaultClassLoader().getResource("").getPath() + "static/img/" + folder);
        File imageFile = new File(imageFolder, id + ".jpg");
        imageFile.delete();
        return null;
    }
}
