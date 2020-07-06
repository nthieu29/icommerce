package com.nthieu.orderservice.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Item {
    private Product product;
    private Integer quantity;
}
