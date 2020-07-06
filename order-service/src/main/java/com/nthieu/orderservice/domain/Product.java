package com.nthieu.orderservice.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product implements Serializable {
    private Long id;
    private String name;
    private String brand;
    private String color;
    private BigDecimal price;
}
