package com.nthieu.productservice;

import com.nthieu.productservice.entity.Product;
import com.nthieu.productservice.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.scheduling.annotation.EnableAsync;

import java.math.BigDecimal;

@SpringBootApplication
@EnableSpringDataWebSupport
@EnableBinding(Source.class)
@EnableAspectJAutoProxy
@EnableAsync
@EnableEurekaClient
public class ProductServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProductServiceApplication.class, args);
    }

    //@Bean
    public CommandLineRunner demoData(ProductRepository repo) {
        return args -> {
            Product product6 = new Product(6L, "galaxy s10", "samsung", "black", BigDecimal.valueOf(500.00));
            Product product7 = new Product(7L, "galaxy s20", "samsung", "black", BigDecimal.valueOf(600.00));
            Product product5 = new Product(5L, "galaxy tab", "samsung", "white", BigDecimal.valueOf(700.00));
            Product product3 = new Product(3L, "ipod", "apple", "white", BigDecimal.valueOf(999.00));
            Product product1 = new Product(1L, "iphone", "apple", "black", BigDecimal.valueOf(1200.00));
            Product product2 = new Product(2L, "ipad", "apple", "black", BigDecimal.valueOf(1500.00));
            Product product4 = new Product(4L, "imac", "apple", "white", BigDecimal.valueOf(2000.00));
            Product product8 = new Product(8L, "cerato", "kia", "black", BigDecimal.valueOf(400000.00));
            Product product9 = new Product(9L, "optima", "kia", "white", BigDecimal.valueOf(500000.00));
            repo.save(product1);
            repo.save(product2);
            repo.save(product3);
            repo.save(product4);
            repo.save(product5);
            repo.save(product6);
            repo.save(product7);
            repo.save(product8);
            repo.save(product9);
        };
    }
}
