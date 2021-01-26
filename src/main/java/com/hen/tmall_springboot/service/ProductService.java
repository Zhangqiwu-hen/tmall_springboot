package com.hen.tmall_springboot.service;

import com.hen.tmall_springboot.dao.ProductDAO;
import com.hen.tmall_springboot.es.ProductESDAO;
import com.hen.tmall_springboot.pojo.Category;
import com.hen.tmall_springboot.pojo.Product;
import com.hen.tmall_springboot.util.Page4Navigator;
import com.hen.tmall_springboot.util.SpringContextUtil;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@CacheConfig(cacheNames = "products")
public class ProductService {

    @Autowired
    ProductDAO productDAO;
    @Autowired
    CategoryService categoryService;
    @Autowired
    ProductImageService productImageService;
    @Autowired
    OrderItemService orderItemService;
    @Autowired
    ReviewService reviewService;
    @Autowired
    ProductESDAO productESDAO;
    @Autowired
    ElasticsearchRestTemplate elasticsearchRestTemplate;

    @CacheEvict(allEntries = true)
    public void add(Product bean) {
        productDAO.save(bean);
        productESDAO.save(bean);
    }

    @CacheEvict(allEntries = true)
    public void delete(int id) {
        productDAO.deleteById(id);
        productESDAO.deleteById(id);
    }

    @Cacheable(key = "'products-one-'+ #p0")
    public Product get(int id) {
        Optional<Product> optional = productDAO.findById(id);
        Product product = optional.get();
        return product;
    }

    @CacheEvict(allEntries = true)
    public void update(Product bean) {
        productDAO.save(bean);
        productESDAO.save(bean);
    }

    @Cacheable(key = "'products-cid-'+#p0+'-page-'+#p1 + '-' + #p2 ")
    public Page4Navigator<Product> list(int cid, int start, int size, int navigatePages) {
        Category category = categoryService.get(cid);
        Pageable pageable = PageRequest.of(start, size);
        Page<Product> pageFromJpa = productDAO.findByCategory(category, pageable);
        return new Page4Navigator<>(pageFromJpa, navigatePages);
    }

    public void fill(Category category) {
        ProductService productService = SpringContextUtil.getBean(ProductService.class);
        List<Product> products = productService.listByCategory(category);
        productImageService.setFirstProductImages(products);
        category.setProducts(products);
    }

    public void fill(List<Category> categorys) {
        for (Category category : categorys)
            fill(category);
    }

    public void fillByRow(List<Category> categorys) {
        int productNumberEachRow = 8;
        for (Category category : categorys) {
            List<Product> products = category.getProducts();
            List<List<Product>> productsByRow = new ArrayList<>();
            for (int i = 0; i < products.size(); i += productNumberEachRow) {
                int size = i + productNumberEachRow;
                size = size > products.size() ? products.size() : size;
                List<Product> productsOfEachRow = products.subList(i, size);
                productsByRow.add(productsOfEachRow);
            }
            category.setProductsByRow(productsByRow);
        }
    }

    @Cacheable(key = "'products-cid-'+ #p0.id")
    public List<Product> listByCategory(Category category) {
        return productDAO.findByCategoryOrderById(category);
    }

    public void setSaleAndReviewNumber(Product product) {
        int saleCount = orderItemService.getSaleCount(product);
        product.setSaleCount(saleCount);

        int reviewCount = reviewService.getCount(product);
        product.setReviewCount(reviewCount);

    }

    public void setSaleAndReviewNumber(List<Product> products) {
        for (Product product : products)
            setSaleAndReviewNumber(product);
    }

    public List<Product> search(String keyword, int start, int size) {
        initDatabase2ES();
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
                .should(QueryBuilders.wildcardQuery("name", keyword))
                .should(QueryBuilders.wildcardQuery("subTitle", keyword));
        BoolQueryBuilder boolQueryBuilder1 = QueryBuilders.boolQuery().must(boolQueryBuilder);
        queryBuilder.withQuery(boolQueryBuilder1);
        Pageable pageable = PageRequest.of(start, size);
        queryBuilder.withPageable(pageable);
        Page<Product> page = productESDAO.search(queryBuilder.build());
        return page.getContent();
    }

    private void initDatabase2ES() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> page = productESDAO.findAll(pageable);
        if (page.getContent().isEmpty()) {
            List<Product> products = productDAO.findAll();
            for (Product product : products) {
                productESDAO.save(product);
            }
        }
    }
}
