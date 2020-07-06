package com.nthieu.productservice.repository;

import com.nthieu.productservice.entity.ProductPriceHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductPriceHistoryRepository extends JpaRepository<ProductPriceHistory, Long> {
}
