package com.nthieu.shoppingcartservice.controller;

import com.nthieu.shoppingcartservice.domain.PersonalShoppingCart;
import com.nthieu.shoppingcartservice.domain.ShoppingCart;
import com.nthieu.shoppingcartservice.service.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ShoppingCartController {
    private final ShoppingCartService shoppingCartService;

    @Autowired
    public ShoppingCartController(ShoppingCartService shoppingCartService) {
        this.shoppingCartService = shoppingCartService;
    }

    @GetMapping("carts")
    public List<ShoppingCart> getAll() {
        return shoppingCartService.findAll();
    }

    @GetMapping("cart")
    public PersonalShoppingCart getShoppingCartOfCustomer(@RequestHeader("Username") String customer) {
        return shoppingCartService.findByCustomer(customer);
    }

    @PutMapping("cart")
    public ShoppingCart updateShoppingCartOfCustomer(@RequestHeader("Username") String customer,
                                                     @RequestBody PersonalShoppingCart personalShoppingCart) {
        return shoppingCartService.updateShoppingCart(customer, personalShoppingCart);
    }

    @DeleteMapping("cart")
    public void deleteShoppingCart(@RequestHeader("Username") String customer) {
        shoppingCartService.deleteShoppingCart(customer);
    }
}
