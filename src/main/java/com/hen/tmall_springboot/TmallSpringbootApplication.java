package com.hen.tmall_springboot;

import com.hen.tmall_springboot.util.PortUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableCaching
@SpringBootApplication
@EnableElasticsearchRepositories(basePackages = {"com.hen.tmall_springboot.es"})
@EnableJpaRepositories(basePackages = {"com.hen.tmall_springboot.dao", "com.hen.tmall_springboot.pojo"})
public class TmallSpringbootApplication {

    static {
        PortUtil.checkPort(6379, "Redis 服务端", true);
        PortUtil.checkPort(9200, "ElasticSearch 服务端", true);
        PortUtil.checkPort(5601, "Kibana 工具", true);
    }

    public static void main(String[] args) {
        SpringApplication.run(TmallSpringbootApplication.class, args);
    }
}
