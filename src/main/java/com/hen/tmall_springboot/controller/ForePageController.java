package com.hen.tmall_springboot.controller;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ForePageController {

    @GetMapping({"/"})
    public String index() {
        return "redirect:home";
    }

    @GetMapping({"/home"})
    public String home() {
        return "fore/home";
    }

    @GetMapping({"/register"})
    public String register() {
        return "fore/register";
    }

    @GetMapping({"/alipay"})
    public String alipay() {
        return "fore/alipay";
    }

    @GetMapping({"/bought"})
    public String bought() {
        return "fore/bought";
    }

    @GetMapping({"/buy"})
    public String buy() {
        return "fore/buy";
    }

    @GetMapping({"/cart"})
    public String cart() {
        return "fore/cart";
    }

    @GetMapping({"/category"})
    public String category() {
        return "fore/category";
    }

    @GetMapping({"/confirmPay"})
    public String confirmPay() {
        return "fore/confirmPay";
    }

    @GetMapping({"/login"})
    public String login() {
        return "fore/login";
    }

    @GetMapping({"/orderConfirmed"})
    public String orderConfirmed() {
        return "fore/orderConfirmed";
    }

    @GetMapping({"/payed"})
    public String payed() {
        return "fore/payed";
    }

    @GetMapping({"/product"})
    public String product() {
        return "fore/product";
    }

    @GetMapping({"/registerSuccess"})
    public String registerSuccess() {
        return "fore/registerSuccess";
    }

    @GetMapping({"/review"})
    public String review() {
        return "fore/review";
    }

    @GetMapping({"/search"})
    public String searchResult() {
        return "fore/search";
    }

    @GetMapping({"/forelogout"})
    public String logout() {
        Subject subject = SecurityUtils.getSubject();
        if (subject.isAuthenticated()) {
            subject.logout();
        }

        return "redirect:home";
    }
}
