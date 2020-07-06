package com.nthieu.productservice.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Aspect
@Component
public class CustomerAuditAspect {
    private final Source source;

    @Value("${spring.data.web.sort.sort-parameter:sort}")
    private String sortParameter;

    public CustomerAuditAspect(Source source) {
        this.source = source;
    }

    @Before("execution(* com.nthieu.productservice.controller.ProductController.getProductDetail(..))")
    @Async
    public void logUserViewProduct(JoinPoint joinPoint) {
        Object[] methodArguments = joinPoint.getArgs();
        String productId = (String) methodArguments[0];
        String username = (String) methodArguments[1];
        CustomerActivity customerActivity = new CustomerActivity(username, LocalDateTime.now(), productId);
        Message<CustomerActivity> message = MessageBuilder.withPayload(customerActivity).build();
        source.output().send(message);
    }

    @Before("execution(* com.nthieu.productservice.controller.ProductController.getAllProducts(..))")
    @Async
    public void logUserFilterAndSort(JoinPoint joinPoint) {
        Object[] methodArguments = joinPoint.getArgs();
        Map<String, List<String>> requestParams = (Map<String, List<String>>) methodArguments[2];
        List<String> sortBy = requestParams.get(sortParameter);
        Map<String, List<String>> filterBy = new HashMap<>();
        for (Map.Entry<String, List<String>> entry : requestParams.entrySet()) {
            if (!entry.getKey().equals(sortParameter)) {
                filterBy.put(entry.getKey(), entry.getValue());
            }
        }
        if ((sortBy != null && !sortBy.isEmpty()) || (!filterBy.isEmpty())) {
            String username = (String) methodArguments[3];
            CustomerActivity customerActivity = new CustomerActivity(username,
                    LocalDateTime.now(), null, filterBy, sortBy);
            Message<CustomerActivity> message = MessageBuilder.withPayload(customerActivity).build();
            source.output().send(message);
        }
    }
}
