package com.hen.tmall_springboot.service;

import com.hen.tmall_springboot.dao.OrderItemDAO;
import com.hen.tmall_springboot.pojo.Order;
import com.hen.tmall_springboot.pojo.OrderItem;
import com.hen.tmall_springboot.pojo.Product;
import com.hen.tmall_springboot.pojo.User;
import com.hen.tmall_springboot.util.SpringContextUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@CacheConfig(cacheNames = {"orderItems"})
public class OrderItemService {

    @Autowired
    OrderItemDAO orderItemDAO;
    @Autowired
    ProductImageService productImageService;

    public void fill(List<Order> orders) {
        for (Order order : orders)
            this.fill(order);
    }

    public void fill(Order order) {
        List<OrderItem> orderItems = this.listByOrder(order);
        float total = 0;
        int totalNumber = 0;

        for (OrderItem oi : orderItems) {
            total += oi.getNumber() * oi.getProduct().getPromotePrice();
            totalNumber += oi.getNumber();
            productImageService.setFirstProductImage(oi.getProduct());
        }

        order.setTotal(total);
        order.setOrderItems(orderItems);
        order.setTotalNumber(totalNumber);
    }

    @Cacheable(key = "'orderItems-oid-'+ #p0.id")
    public List<OrderItem> listByOrder(Order order) {
        return this.orderItemDAO.findByOrderOrderByIdDesc(order);
    }

    public int getSaleCount(Product product) {
        OrderItemService orderItemService = SpringContextUtil.getBean(OrderItemService.class);
        List<OrderItem> ois = orderItemService.listByProduct(product);
        int result = 0;
        for (OrderItem oi : ois) {
            if (null != oi.getOrder() && null != oi.getOrder().getPayDate())
                result += oi.getNumber();
        }

        return result;
    }

    @Cacheable(key = "'orderItems-pid-'+ #p0.id")
    public List<OrderItem> listByProduct(Product product) {
        return this.orderItemDAO.findByProduct(product);
    }

    @Cacheable(key = "'orderItems-uid-'+ #p0.id")
    public List<OrderItem> listByUser(User user) {
        return this.orderItemDAO.findByUserAndOrderIsNull(user);
    }

    @CacheEvict(allEntries = true)
    public void update(OrderItem orderItem) {
        this.orderItemDAO.save(orderItem);
    }

    @CacheEvict(allEntries = true)
    public void add(OrderItem orderItem) {
        this.orderItemDAO.save(orderItem);
    }

    @Cacheable(key = "'orderItems-one-'+ #p0")
    public OrderItem get(int id) {
        Optional<OrderItem> optional = this.orderItemDAO.findById(id);
        OrderItem orderItem = optional.get();
        return orderItem;
    }

    @CacheEvict(allEntries = true)
    public void delete(int oiid) {
        this.orderItemDAO.deleteById(oiid);
    }
}