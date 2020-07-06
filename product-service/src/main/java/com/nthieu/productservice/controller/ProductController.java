package com.nthieu.productservice.controller;

import com.nthieu.productservice.entity.Product;
import com.nthieu.productservice.model.ProductRequest;
import com.nthieu.productservice.service.ProductService;
import com.querydsl.core.types.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;


@RestController
@RequestMapping(ProductController.PRODUCT_PATH)
public class ProductController {
    public static final String PRODUCT_PATH = "/products";
    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public List<Product> getAllProducts(@QuerydslPredicate(root = Product.class) Predicate predicate,
                                        Pageable pageable, @RequestParam MultiValueMap<String, List<String>> allRequestParams,
                                        @RequestHeader("Username") String customer) {
        return productService.findAll(predicate, pageable);
    }

    @GetMapping("/{id}")
    public Product getProductDetail(@PathVariable String id, @RequestHeader("Username") String customer) {
        return productService.findProductById(id);
    }

    @PutMapping("/{id}")
    public Product updateProduct(@Valid @RequestBody ProductRequest productRequest, @PathVariable String id) {
        Product updatedProduct = productRequest.toEntity();
        updatedProduct.setId(Long.parseLong(id));
        return productService.update(updatedProduct);
    }

    @PostMapping
    public Product addProduct(@Valid @RequestBody ProductRequest productRequest) {
        Product newProduct = productRequest.toEntity();
        return productService.add(newProduct);
    }

}
