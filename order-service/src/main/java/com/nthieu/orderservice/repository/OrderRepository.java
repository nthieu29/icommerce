package com.nthieu.orderservice.repository;

import com.nthieu.orderservice.domain.Order;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "orders", path = "orders")
public interface OrderRepository extends MongoRepository<Order, String> {
}
