package com.nthieu.shoppingcartservice.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@Data
@RedisHash("shoppingCart")
@NoArgsConstructor
@AllArgsConstructor
public class ShoppingCart {
    @Id
    @Indexed
    private String customer;
    private PersonalShoppingCart personalShoppingCart;

    public ShoppingCart(String customer) {
        this.customer = customer;
    }
}
