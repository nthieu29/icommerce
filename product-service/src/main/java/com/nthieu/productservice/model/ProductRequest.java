package com.nthieu.productservice.model;

import com.nthieu.productservice.entity.Product;
import lombok.Data;
import org.modelmapper.ModelMapper;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class ProductRequest {
    @NotBlank(message = "name must be not blank")
    private String name;

    @NotBlank(message = "brand must be not blank")
    private String brand;

    @NotBlank(message = "color must be not blank")
    private String color;

    @NotNull(message = "price must be not null")
    @DecimalMin("0.0")
    @Digits(integer = 9, fraction = 2)
    private BigDecimal price;

    public Product toEntity() {
        ModelMapper mapper = new ModelMapper();
        return mapper.map(this, Product.class);
    }
}
