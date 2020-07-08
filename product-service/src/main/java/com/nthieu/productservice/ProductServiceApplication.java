package com.nthieu.productservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

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

    @Bean
    public Executor taskExecutor() {
        // We need execute many short-lived asynchronous tasks (send customer activities to CloudAMQP)
        // so Cached Thread Pool already meet our needs
        // for more advanced, we could use ThreadPoolExecutor
        return Executors.newCachedThreadPool();
    }

}
