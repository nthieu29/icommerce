package com.nthieu.orderservice.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Order {
    @Id
    private String id;
    private String customer;
    private PersonalShoppingCart personalShoppingCart;
    private ShippingAddress shippingAddress;
}
