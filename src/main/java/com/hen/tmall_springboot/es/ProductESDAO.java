package com.hen.tmall_springboot.es;

import com.hen.tmall_springboot.pojo.Product;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ProductESDAO extends ElasticsearchRepository<Product, Integer> {
}
