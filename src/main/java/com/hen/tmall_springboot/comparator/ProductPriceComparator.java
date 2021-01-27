package com.hen.tmall_springboot.comparator;

import com.hen.tmall_springboot.pojo.Product;

import java.util.Comparator;

public class ProductPriceComparator implements Comparator<Product> {

    public int compare(Product p1, Product p2) {
        if (p1.getPromotePrice() < p2.getPromotePrice()) {
            return -1;
        } else {
            return p1.getPromotePrice() == p2.getPromotePrice() ? 0 : 1;
        }
    }
}