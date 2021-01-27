package com.hen.tmall_springboot.comparator;

import com.hen.tmall_springboot.pojo.Product;

import java.util.Comparator;

public class ProductDateComparator implements Comparator<Product> {

    public int compare(Product p1, Product p2) {
        return p2.getCreateDate().compareTo(p1.getCreateDate());
    }
}