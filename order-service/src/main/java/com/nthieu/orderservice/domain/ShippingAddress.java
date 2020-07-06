package com.nthieu.orderservice.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShippingAddress {
    private String city;
    private String street;
    private String phone;
}
