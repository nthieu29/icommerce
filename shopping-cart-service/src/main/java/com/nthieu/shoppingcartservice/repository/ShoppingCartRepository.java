package com.nthieu.shoppingcartservice.repository;

import com.nthieu.shoppingcartservice.domain.ShoppingCart;
import org.springframework.data.repository.CrudRepository;

public interface ShoppingCartRepository extends CrudRepository<ShoppingCart, String> {
}
