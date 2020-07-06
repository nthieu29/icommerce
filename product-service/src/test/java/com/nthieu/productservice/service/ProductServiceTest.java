package com.nthieu.productservice.service;

import com.nthieu.productservice.entity.Product;
import com.nthieu.productservice.repository.ProductPriceHistoryRepository;
import com.nthieu.productservice.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {
    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductPriceHistoryRepository priceRepository;

    @InjectMocks
    private ProductService service;

    private Product mockProduct() {
        return new Product(1L, "iphone", "apple", "red", BigDecimal.valueOf(1000));
    }

    @Test
    public void updateProduct_WhenNoPriceChange_ThenNoPriceHistoryRecorded() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(mockProduct()));
        Product updatedProduct = mockProduct();
        updatedProduct.setName("ipad");
        service.update(updatedProduct);
        verifyNoInteractions(priceRepository);
    }

    @Test
    public void updateProduct_WhenPriceChange_ThenCreateNewPriceHistoryRecord() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(mockProduct()));
        Product updatedProduct = mockProduct();
        updatedProduct.setPrice(BigDecimal.valueOf(999));
        service.update(updatedProduct);
        verify(priceRepository).save(any());
    }
}