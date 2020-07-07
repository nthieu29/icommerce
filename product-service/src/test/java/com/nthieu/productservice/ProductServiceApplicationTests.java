package com.nthieu.productservice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.nthieu.productservice.controller.ProductController;
import com.nthieu.productservice.entity.Product;
import com.nthieu.productservice.entity.ProductPriceHistory;
import com.nthieu.productservice.helper.ProductMother;
import com.nthieu.productservice.repository.ProductPriceHistoryRepository;
import com.nthieu.productservice.repository.ProductRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.util.UriComponentsBuilder;

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

    private static boolean isInitializedDB;
    @Autowired
    private ObjectMapper objectMapper;
    @LocalServerPort
    private int port;

    @BeforeEach
    public void initDataInDB() {
        if (!isInitializedDB) {
            productRepository.deleteAllInBatch();
            Product product6 = ProductMother.samsungProduct().name("galaxy S10").color("Black").price(BigDecimal.valueOf(500)).id(6L).build();
            Product product7 = ProductMother.galaxyS20Phone().id(7L).build();
            Product product5 = ProductMother.samsungProduct().name("galaxy Tab").color("White").price(BigDecimal.valueOf(700)).id(5L).build();
            Product product3 = ProductMother.appleProduct().name("iPod").color("White").price(BigDecimal.valueOf(999)).id(3L).build();
            Product product1 = ProductMother.iphone().id(1L).build();
            Product product2 = ProductMother.appleProduct().name("iPad").color("Black").price(BigDecimal.valueOf(1500)).id(2L).build();
            Product product4 = ProductMother.appleProduct().name("iMac").color("White").price(BigDecimal.valueOf(2000)).id(4L).build();
            Product product8 = ProductMother.kiaProduct().name("Cerato").color("Black").price(BigDecimal.valueOf(400000.00)).id(8L).build();
            Product product9 = ProductMother.kiaProduct().name("Optima").color("White").price(BigDecimal.valueOf(500000.00)).id(9L).build();
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
        isInitializedDB = true;
    }

    private HttpHeaders mockAuthenticatedHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Username", "nthieu");
        return headers;
    }

    @Test
    void getAllProductsWhenUserLoggedInThenReturnCorrectResult() {
        HttpEntity<String> requestEntity = new HttpEntity<>(null, mockAuthenticatedHeaders());
        List<Product> products = restTemplate.exchange(ProductController.PRODUCT_PATH,
                HttpMethod.GET, requestEntity, new ParameterizedTypeReference<List<Product>>() {
                }).getBody();
        Assertions.assertEquals(9, products.size());
    }

    @Test
    void getProductsByAppleBrandWhenUserLoggedInThenReturnCorrectResult() {
        HttpEntity<String> entity = new HttpEntity<>(null, mockAuthenticatedHeaders());
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl("http://localhost:" + port + ProductController.PRODUCT_PATH)
                .queryParam("brand", "apple");

        List<Product> products = restTemplate.exchange(uriBuilder.toUriString(),
                HttpMethod.GET, entity, new ParameterizedTypeReference<List<Product>>() {
                }).getBody();
        Assertions.assertEquals(4, products.size());
        for (Product product : products) {
            Assertions.assertTrue(product.getBrand().equalsIgnoreCase("apple"));
        }
    }

    @Test
    void get1ProductWithId1WhenUserLoggedInThenReturnCorrectResult() {
        HttpEntity<String> requestEntity = new HttpEntity<>(null, mockAuthenticatedHeaders());
        ResponseEntity<Product> responseEntity =
                restTemplate.exchange(ProductController.PRODUCT_PATH + "/7",
                        HttpMethod.GET, requestEntity, new ParameterizedTypeReference<Product>() {
                        });
        Product actualProduct = responseEntity.getBody();
        Product expectedProduct = ProductMother.galaxyS20Phone().id(7L).build();
        Assertions.assertEquals(expectedProduct, actualProduct);
    }

    @Test
    void getAppleProductWithPriceFrom1000To2000ThenReturnCorrectResult() {
        HttpEntity<String> entity = new HttpEntity<>(null, mockAuthenticatedHeaders());
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl("http://localhost:" + port + ProductController.PRODUCT_PATH)
                .queryParam("brand", "apple")
                .queryParam("price", 1000)
                .queryParam("price", 2000);

        List<Product> products = restTemplate.exchange(uriBuilder.toUriString(),
                HttpMethod.GET, entity, new ParameterizedTypeReference<List<Product>>() {
                }).getBody();
        Assertions.assertEquals(2, products.size());
        for (Product product : products) {
            Assertions.assertTrue(product.getBrand().equalsIgnoreCase("apple"));
        }
    }

    @Test
    void updateProductNameThenReturnUpdatedProductAndNoKeepTrackProductHistoryRecord() throws JsonProcessingException {
        Product product = ProductMother.iphone().build();
        product.setName("iPhoneXXX");
        product.setId(1L);

        Product updatedProduct = sendPutRequestToUpdateProduct(product);

        Assertions.assertEquals(product, updatedProduct);
        Assertions.assertTrue(priceHistoryRepository.findAllByProductId(1L).isEmpty());
    }

    private Product sendPutRequestToUpdateProduct(Product updatedProduct) throws JsonProcessingException {
        ObjectWriter writer = objectMapper.writer().withDefaultPrettyPrinter();
        String json = writer.writeValueAsString(updatedProduct);
        HttpHeaders headers = mockAuthenticatedHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> requestEntity = new HttpEntity<>(json, headers);

        ResponseEntity<Product> responseEntity =
                restTemplate.exchange(ProductController.PRODUCT_PATH + "/" + updatedProduct.getId().toString(),
                        HttpMethod.PUT, requestEntity, new ParameterizedTypeReference<Product>() {
                        });
        return responseEntity.getBody();
    }

    @Test
    void updateProductPriceThenReturnUpdatedProductAndKeepTrackProductHistoryRecord() throws JsonProcessingException {
        BigDecimal oldPrice = BigDecimal.valueOf(500);
        Product product = ProductMother.samsungProduct().name("galaxy S10").color("Black").price(BigDecimal.valueOf(501)).id(6L).build();

        Product updatedProduct = sendPutRequestToUpdateProduct(product);

        Assertions.assertEquals(product, updatedProduct);
        List<ProductPriceHistory> productPriceHistories = priceHistoryRepository.findAllByProductId(6L);
        Assertions.assertEquals(1, productPriceHistories.size());
        ProductPriceHistory productPriceHistory = productPriceHistories.get(0);
        Assertions.assertEquals(product.getBrand(), productPriceHistory.getBrand());
        Assertions.assertEquals(product.getName(), productPriceHistory.getName());
        Assertions.assertTrue(product.getPrice().compareTo(productPriceHistory.getNewPrice()) == 0);
        Assertions.assertTrue(oldPrice.compareTo(productPriceHistory.getOldPrice()) == 0);
    }

}
