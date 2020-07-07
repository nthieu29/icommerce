package com.nthieu.productservice.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class ProductPriceHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "brand", nullable = false)
    private String brand;

    @Column(name = "color", nullable = false)
    private String color;

    @Column(name = "old_price", nullable = false, precision = 9, scale = 2)
    private BigDecimal oldPrice;

    @Column(name = "new_Price", nullable = false, precision = 9, scale = 2)
    private BigDecimal newPrice;

    @Column(name = "updated_on", nullable = false, columnDefinition = "TIMESTAMP")
    @CreatedDate
    private LocalDateTime updatedOn;

    @Column(name = "by_user", nullable = false)
    @CreatedBy
    private String byUser;

    public ProductPriceHistory(Product product, BigDecimal newPrice) {
        this.productId = product.getId();
        this.name = product.getName();
        this.brand = product.getBrand();
        this.color = product.getColor();
        this.oldPrice = product.getPrice();
        this.newPrice = newPrice;
    }

}
