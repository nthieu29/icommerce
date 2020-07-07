package com.nthieu.productservice.helper;

import com.nthieu.productservice.entity.Product;

import java.math.BigDecimal;

public class ProductMother {
    public static Product.ProductBuilder appleProduct() {
        return Product.builder().brand("Apple");
    }

    public static Product.ProductBuilder samsungProduct() {
        return Product.builder().brand("Samsung");
    }

    public static Product.ProductBuilder galaxyS20Phone() {
        return samsungProduct().name("Galaxy S20").color("White").price(BigDecimal.valueOf(1100));
    }

    public static Product.ProductBuilder kiaProduct() {
        return Product.builder().brand("KIA");
    }

    public static Product.ProductBuilder iphone() {
        return appleProduct().name("iPhone").color("Red").price(BigDecimal.valueOf(999));
    }
}
