package com.nthieu.productservice;

import com.nthieu.productservice.controller.ProductController;
import com.nthieu.productservice.entity.Product;
import com.nthieu.productservice.repository.ProductPriceHistoryRepository;
import com.nthieu.productservice.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class ProductServiceApplicationTests {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductPriceHistoryRepository priceHistoryRepository;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private Source source;

    @BeforeEach
    public void initDataInDB() {
        Product product6 = new Product(6L, "galaxy s10", "samsung", "black", BigDecimal.valueOf(500.00));
        Product product7 = new Product(7L, "galaxy s20", "samsung", "black", BigDecimal.valueOf(600.00));
        Product product5 = new Product(5L, "galaxy tab", "samsung", "white", BigDecimal.valueOf(700.00));
        Product product3 = new Product(3L, "ipod", "apple", "white", BigDecimal.valueOf(999.00));
        Product product1 = new Product(1L, "iphone", "apple", "black", BigDecimal.valueOf(1200.00));
        Product product2 = new Product(2L, "ipad", "apple", "black", BigDecimal.valueOf(1500.00));
        Product product4 = new Product(4L, "imac", "apple", "white", BigDecimal.valueOf(2000.00));
        Product product8 = new Product(8L, "cerato", "kia", "black", BigDecimal.valueOf(400000.00));
        Product product9 = new Product(9L, "optima", "kia", "white", BigDecimal.valueOf(500000.00));
        productRepository.save(product1);
        productRepository.save(product2);
        productRepository.save(product3);
        productRepository.save(product4);
        productRepository.save(product5);
        productRepository.save(product6);
        productRepository.save(product7);
        productRepository.save(product8);
        productRepository.save(product9);
    }

    @Test
    void contextLoads() {
        HttpHeaders headers = new HttpHeaders();
        //headers.set("Username", "nthieu");
        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        ResponseEntity<Product> responseEntity =
                restTemplate.exchange(ProductController.PRODUCT_PATH + "/1",
                        HttpMethod.GET, entity, new ParameterizedTypeReference<Product>() {
                        });
        Product products = responseEntity.getBody();

        List<Product> forNow = restTemplate.exchange(ProductController.PRODUCT_PATH,
                HttpMethod.GET, entity, new ParameterizedTypeReference<List<Product>>() {
                }).getBody();
    }

}
