package com.nthieu.shoppingcartservice.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.index.Indexed;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product implements Serializable {
    @Indexed
    private Long id;
    private String name;
    private String brand;
    private String color;
    private BigDecimal price;
}
