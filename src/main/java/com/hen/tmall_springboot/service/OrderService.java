package com.hen.tmall_springboot.service;

import com.hen.tmall_springboot.dao.OrderDAO;
import com.hen.tmall_springboot.pojo.Order;
import com.hen.tmall_springboot.pojo.OrderItem;
import com.hen.tmall_springboot.pojo.User;
import com.hen.tmall_springboot.util.Page4Navigator;
import com.hen.tmall_springboot.util.SpringContextUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@CacheConfig(cacheNames = {"orders"})
public class OrderService {

    public static final String waitPay = "waitPay";
    public static final String waitDelivery = "waitDelivery";
    public static final String waitConfirm = "waitConfirm";
    public static final String waitReview = "waitReview";
    public static final String finish = "finish";
    public static final String delete = "delete";
    @Autowired
    OrderDAO orderDAO;
    @Autowired
    OrderItemService orderItemService;

    @Cacheable(key = "'orders-page-'+#p0+ '-' + #p1")
    public Page4Navigator<Order> list(int start, int size, int navigatePages) {
        Sort sort = Sort.by(Direction.DESC, new String[]{"id"});
        Pageable pageable = PageRequest.of(start, size, sort);
        Page<Order> page = this.orderDAO.findAll(pageable);
        return new Page4Navigator(page, navigatePages);
    }

    public void removeOrderFromOrderItem(List<Order> orders) {
        for (Order order : orders) {
            removeOrderFromOrderItem(order);
        }
    }

    public void removeOrderFromOrderItem(Order order) {
        List<OrderItem> orderItems = order.getOrderItems();
        for (OrderItem orderItem : orderItems) {
            orderItem.setOrder(null);
        }
    }

    @Cacheable(key = "'orders-one-'+ #p0")
    public Order get(int oid) {
        Optional<Order> optional = this.orderDAO.findById(oid);
        Order order = optional.get();
        return order;
    }

    @CacheEvict(allEntries = true)
    public void update(Order bean) {
        this.orderDAO.save(bean);
    }

    @CacheEvict(allEntries = true)
    @Transactional(propagation = Propagation.REQUIRED, rollbackForClassName = {"Exception"})
    public float add(Order order, List<OrderItem> ois) {
        float total = 0;
        this.add(order);

        for (OrderItem oi : ois) {
            oi.setOrder(order);
            orderItemService.update(oi);
            total += oi.getProduct().getPromotePrice() * oi.getNumber();
        }

        return total;
    }

    @CacheEvict(allEntries = true)
    public void add(Order order) {
        this.orderDAO.save(order);
    }

    public List<Order> listByUserWithoutDelete(User user) {
        OrderService orderService = SpringContextUtil.getBean(OrderService.class);
        List<Order> orders = orderService.listByUserAndNotDeleted(user);
        this.orderItemService.fill(orders);
        return orders;
    }

    @Cacheable(key = "'orders-uid-'+ #p0.id")
    public List<Order> listByUserAndNotDeleted(User user) {
        return this.orderDAO.findByUserAndStatusNotOrderByIdDesc(user, "delete");
    }
}