package com.hen.tmall_springboot.controller;

import com.hen.tmall_springboot.comparator.*;
import com.hen.tmall_springboot.pojo.*;
import com.hen.tmall_springboot.service.*;
import com.hen.tmall_springboot.util.Result;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.HtmlUtils;

import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
public class ForeRESTController {

    @Autowired
    CategoryService categoryService;
    @Autowired
    ProductService productService;
    @Autowired
    UserService userService;
    @Autowired
    ProductImageService productImageService;
    @Autowired
    PropertyValueService propertyValueService;
    @Autowired
    ReviewService reviewService;
    @Autowired
    OrderItemService orderItemService;
    @Autowired
    OrderService orderService;

    @GetMapping({"/forehome"})
    public Object home() {
        List<Category> cs = this.categoryService.list();
        this.productService.fill(cs);
        this.productService.fillByRow(cs);
        this.categoryService.removeCategoryFromProduct(cs);
        return cs;
    }

    @PostMapping({"/foreregister"})
    public Object register(@RequestBody User user) {
        String name = user.getName();
        String password = user.getPassword();
        name = HtmlUtils.htmlEscape(name);
        user.setName(name);
        boolean exist = this.userService.isExist(name);
        String salt;
        if (exist) {
            salt = "用户名已经被使用,不能使用";
            return Result.fail(salt);
        } else {
            salt = (new SecureRandomNumberGenerator()).nextBytes().toString();
            int times = 2;
            String algorithmName = "md5";
            String encodedPassword = (new SimpleHash(algorithmName, password, salt, times)).toString();
            user.setSalt(salt);
            user.setPassword(encodedPassword);
            this.userService.add(user);
            return Result.success();
        }
    }

    @PostMapping({"/forelogin"})
    public Object login(@RequestBody User userParam, HttpSession session) {
        String name = userParam.getName();
        name = HtmlUtils.htmlEscape(name);
        Subject subject = SecurityUtils.getSubject();
        UsernamePasswordToken token = new UsernamePasswordToken(name, userParam.getPassword());

        try {
            subject.login(token);
            User user = this.userService.getByName(name);
            session.setAttribute("user", user);
            return Result.success();
        } catch (AuthenticationException var8) {
            String message = "账号密码错误";
            return Result.fail(message);
        }
    }

    @GetMapping({"/foreproduct/{pid}"})
    public Object product(@PathVariable("pid") int pid) {
        Product product = this.productService.get(pid);
        List<ProductImage> productSingleImages = this.productImageService.listSingleProductImages(product);
        List<ProductImage> productDetailImages = this.productImageService.listDetailProductImages(product);
        product.setProductSingleImages(productSingleImages);
        product.setProductDetailImages(productDetailImages);
        List<PropertyValue> pvs = this.propertyValueService.list(product);
        List<Review> reviews = this.reviewService.list(product);
        this.productService.setSaleAndReviewNumber(product);
        this.productImageService.setFirstProductImage(product);
        Map<String, Object> map = new HashMap();
        map.put("product", product);
        map.put("pvs", pvs);
        map.put("reviews", reviews);
        return Result.success(map);
    }

    @GetMapping({"forecheckLogin"})
    public Object checkLogin() {
        Subject subject = SecurityUtils.getSubject();
        return subject.isAuthenticated() ? Result.success() : Result.fail("未登录");
    }

    @GetMapping({"/forecategory/{cid}"})
    public Object category(@PathVariable("cid") int cid, String sort) {
        Category c = this.categoryService.get(cid);
        this.productService.fill(c);
        this.productService.setSaleAndReviewNumber(c.getProducts());
        this.categoryService.removeCategoryFromProduct(c);
        if (null != sort) {
            switch (sort) {
                case "review":
                    Collections.sort(c.getProducts(), new ProductReviewComparator());
                    break;
                case "date":
                    Collections.sort(c.getProducts(), new ProductDateComparator());
                    break;

                case "saleCount":
                    Collections.sort(c.getProducts(), new ProductSaleCountComparator());
                    break;

                case "price":
                    Collections.sort(c.getProducts(), new ProductPriceComparator());
                    break;

                case "all":
                    Collections.sort(c.getProducts(), new ProductAllComparator());
                    break;
            }
        }
        return c;
    }

    @PostMapping({"/foresearch"})
    public Object search(@RequestParam("keyword") String keyword) {
        if (null == keyword) {
            keyword = "";
        }

        List<Product> ps = this.productService.search(keyword, 0, 10);
        this.productImageService.setFirstProductImages(ps);
        this.productService.setSaleAndReviewNumber(ps);
        return ps;
    }

    @GetMapping({"/forebuyone"})
    public Object buyOne(@RequestParam("pid") int pid, @RequestParam("num") int num, HttpSession session) {
        Map<String, Integer> map = this.buyoneAndAddCart(pid, num, session);
        return Result.success(map);
    }

    private Map<String, Integer> buyoneAndAddCart(int pid, int num, HttpSession session) {
        Product product = this.productService.get(pid);
        int oiid = 0;
        int cartTotalItemNumber = 0;
        User user = (User) session.getAttribute("user");
        boolean found = false;
        List<OrderItem> ois = this.orderItemService.listByUser(user);
        for (OrderItem oi : ois) {
            if (oi.getProduct().getId() == product.getId()) {
                oi.setNumber(oi.getNumber() + num);
                this.orderItemService.update(oi);
                found = true;
                oiid = oi.getId();
                break;
            }
        }

        if (!found) {
            OrderItem oi = new OrderItem();
            oi.setUser(user);
            oi.setProduct(product);
            oi.setNumber(num);
            this.orderItemService.add(oi);
            oiid = oi.getId();
        }

        if (null != user) {
            List<OrderItem> orderItems = this.orderItemService.listByUser(user);

            for (OrderItem oi : orderItems) {
                cartTotalItemNumber++;
            }

        }

        Map<String, Integer> map = new HashMap();
        map.put("oiid", oiid);
        map.put("cartTotalItemNumber", cartTotalItemNumber);
        return map;
    }

    @GetMapping({"forebuy"})
    public Object buy(String[] oiid, HttpSession session) {
        List<OrderItem> orderItems = new ArrayList();
        float total = 0;
        for (String strid : oiid) {
            int id = Integer.parseInt(strid);
            OrderItem oi = this.orderItemService.get(id);
            total += oi.getProduct().getPromotePrice() * oi.getNumber();
            orderItems.add(oi);
        }

        this.productImageService.setFirstProductImagesOnOrderItems(orderItems);
        session.setAttribute("ois", orderItems);
        Map<String, Object> map = new HashMap();
        map.put("orderItems", orderItems);
        map.put("total", total);
        return Result.success(map);
    }

    @GetMapping({"foreaddCart"})
    public Object addCart(int pid, int num, HttpSession session) {
        Map<String, Integer> map = this.buyoneAndAddCart(pid, num, session);
        return Result.success(map);
    }

    @GetMapping({"forecart"})
    public Object cart(HttpSession session) {
        User user = (User) session.getAttribute("user");
        List<OrderItem> ois = this.orderItemService.listByUser(user);
        this.productImageService.setFirstProductImagesOnOrderItems(ois);
        return ois;
    }

    @GetMapping({"forechangeOrderItem"})
    public Object changeOrderItem(HttpSession session, int pid, int num) {
        User user = (User) session.getAttribute("user");
        if (null == user) {
            return Result.fail("未登录");
        } else {
            List<OrderItem> ois = this.orderItemService.listByUser(user);
            for (OrderItem oi : ois) {
                if (oi.getProduct().getId() == pid) {
                    oi.setNumber(num);
                    this.orderItemService.update(oi);
                    break;
                }
            }

            return Result.success();
        }
    }

    @GetMapping({"foredeleteOrderItem"})
    public Object deleteOrderItem(HttpSession session, int oiid) {
        User user = (User) session.getAttribute("user");
        if (null == user) {
            return Result.fail("未登录");
        } else {
            this.orderItemService.delete(oiid);
            int cartTotalItemNumber = 0;
            if (null != user) {
                List<OrderItem> orderItems = this.orderItemService.listByUser(user);

                for (OrderItem oi : orderItems) {
                    cartTotalItemNumber++;
                }
            }

            Map<String, Integer> map = new HashMap();
            map.put("cartTotalItemNumber", cartTotalItemNumber);
            return Result.success(map);
        }
    }

    @PostMapping({"forecreateOrder"})
    public Object createOrder(@RequestBody Order order, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (null == user) {
            return Result.fail("未登录");
        } else {
            String orderCode = (new SimpleDateFormat("yyyyMMddHHmmssSSS")).format(new Date()) + RandomUtils.nextInt(10000);
            order.setOrderCode(orderCode);
            order.setCreateDate(new Date());
            order.setUser(user);
            order.setStatus("waitPay");
            List<OrderItem> ois = (List) session.getAttribute("ois");
            float total = this.orderService.add(order, ois);
            Map<String, Object> map = new HashMap();
            map.put("oid", order.getId());
            map.put("total", total);
            return Result.success(map);
        }
    }

    @GetMapping({"forepayed"})
    @Transactional(propagation = Propagation.REQUIRED, rollbackForClassName = {"Exception"})
    public Object payed(int oid) {
        Order order = this.orderService.get(oid);
        order.setStatus("waitDelivery");
        order.setPayDate(new Date());
        this.orderService.update(order);
        List<OrderItem> orderItems = this.orderItemService.listByOrder(order);
        for (OrderItem orderItem : orderItems) {
            Product product = orderItem.getProduct();
            int stock = product.getStock();
            stock -= orderItem.getNumber();
            product.setStock(stock);
            this.productService.update(product);
        }

        return order;
    }

    @GetMapping({"forebought"})
    public Object bought(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (null == user) {
            return Result.fail("未登录");
        } else {
            List<Order> os = this.orderService.listByUserWithoutDelete(user);
            this.orderService.removeOrderFromOrderItem(os);
            return os;
        }
    }

    @GetMapping({"foreconfirmPay"})
    public Object confirmPay(int oid) {
        Order o = this.orderService.get(oid);
        this.orderItemService.fill(o);
        this.orderService.removeOrderFromOrderItem(o);
        return o;
    }

    @GetMapping({"foreorderConfirmed"})
    public Object orderConfirmed(int oid) {
        Order o = this.orderService.get(oid);
        o.setStatus("waitReview");
        o.setConfirmDate(new Date());
        this.orderService.update(o);
        return Result.success();
    }

    @PutMapping({"foredeleteOrder"})
    public Object deleteOrder(int oid) {
        Order o = this.orderService.get(oid);
        o.setStatus("delete");
        this.orderService.update(o);
        return Result.success();
    }

    @GetMapping({"forereview"})
    public Object review(int oid) {
        Order o = this.orderService.get(oid);
        this.orderItemService.fill(o);
        Product p = o.getOrderItems().get(0).getProduct();
        List<Review> reviews = this.reviewService.list(p);
        this.productService.setSaleAndReviewNumber(p);
        this.orderService.removeOrderFromOrderItem(o);
        Map<String, Object> map = new HashMap();
        map.put("p", p);
        map.put("o", o);
        map.put("reviews", reviews);
        return Result.success(map);
    }

    @PostMapping({"foredoreview"})
    @Transactional(propagation = Propagation.REQUIRED, rollbackForClassName = {"Exception"})
    public Object doreview(HttpSession session, int oid, int pid, String content) {
        Order o = this.orderService.get(oid);
        o.setStatus("finish");
        this.orderService.update(o);
        Product p = this.productService.get(pid);
        content = HtmlUtils.htmlEscape(content);
        User user = (User) session.getAttribute("user");
        Review review = new Review();
        review.setContent(content);
        review.setProduct(p);
        review.setCreateDate(new Date());
        review.setUser(user);
        this.reviewService.add(review);
        return Result.success();
    }
}

