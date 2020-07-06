package com.nthieu.shoppingcartservice.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonalShoppingCart {
    List<Item> cart;

    public PersonalShoppingCart(ShoppingCart shoppingCart) {
        this.cart = shoppingCart.getPersonalShoppingCart().getCart();
    }
}
