package com.hen.tmall_springboot.config;

import com.hen.tmall_springboot.interceptor.LoginInterceptor;
import com.hen.tmall_springboot.interceptor.OtherInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MyWebMvcConfigurer implements WebMvcConfigurer {

    @Bean
    public LoginInterceptor getLoginInterceptor() {
        return new LoginInterceptor();
    }

    @Bean
    public OtherInterceptor getOtherInterceptor() {
        return new OtherInterceptor();
    }

    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(this.getOtherInterceptor()).addPathPatterns("/**");
        registry.addInterceptor(this.getLoginInterceptor()).addPathPatterns("/**");
    }
}