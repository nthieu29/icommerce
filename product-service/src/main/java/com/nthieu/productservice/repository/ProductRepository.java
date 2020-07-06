package com.nthieu.productservice.repository;

import com.nthieu.productservice.entity.Product;
import com.nthieu.productservice.entity.QProduct;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.core.types.dsl.StringPath;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.querydsl.binding.SingleValueBinding;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long>,
        QuerydslPredicateExecutor<Product>,
        QuerydslBinderCustomizer<QProduct> {
    @Override
    default void customize(QuerydslBindings bindings, QProduct product) {
        bindings.bind(String.class).first((SingleValueBinding<StringPath, String>) StringExpression::containsIgnoreCase);
        bindings.bind(product.price).all((path, value) -> {
            Iterator<? extends BigDecimal> it = value.iterator();
            BigDecimal from = it.next();
            if (value.size() >= 2) {
                BigDecimal to = it.next();
                return Optional.of(path.between(from, to));
            } else {
                return Optional.of(path.eq(from));
            }
        });
        bindings.excluding(product.id);
    }
}
