package com.nthieu.shoppingcartservice.service;

import com.nthieu.shoppingcartservice.domain.PersonalShoppingCart;
import com.nthieu.shoppingcartservice.domain.ShoppingCart;
import com.nthieu.shoppingcartservice.repository.ShoppingCartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ShoppingCartService {
    private final ShoppingCartRepository repository;

    @Autowired
    public ShoppingCartService(ShoppingCartRepository repository) {
        this.repository = repository;
    }

    public List<ShoppingCart> findAll() {
        ArrayList<ShoppingCart> result = new ArrayList<>();
        Iterable<ShoppingCart> shoppingCarts = repository.findAll();
        shoppingCarts.forEach(result::add);
        return result;
    }

    public PersonalShoppingCart findByCustomer(String customer) {
        ShoppingCart shoppingCart = repository.findById(customer).orElseThrow();
        return new PersonalShoppingCart(shoppingCart);
    }

    public ShoppingCart updateShoppingCart(String customer, PersonalShoppingCart personalShoppingCart) {
        Optional<ShoppingCart> shoppingCart = repository.findById(customer);
        ShoppingCart customerShoppingCart = shoppingCart.orElse(new ShoppingCart(customer));
        customerShoppingCart.setPersonalShoppingCart(personalShoppingCart);
        return repository.save(customerShoppingCart);
    }

    public void deleteShoppingCart(String customer) {
        repository.deleteById(customer);
    }
}
