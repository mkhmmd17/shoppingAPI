package com.oreilly.shopping.dao;

import com.oreilly.shopping.entities.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@Transactional
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;


    @Test
    void countProducts() {
        assertEquals(3, productRepository.count());
    }


    @Test
    void findById() {
        assertTrue(productRepository.findById(1L).isPresent());
    }

    @Test
    void findAll() {
        productRepository.findAll().forEach(System.out::println);
        assertEquals(3, productRepository.findAll().size());
    }

    @Test
    void insertProduct() {
        Product bat = new Product("cricket bat", BigDecimal.valueOf(35.00));
        productRepository.save(bat);
        assertAll(
                () -> assertNotNull(bat.getId()),
                () -> assertEquals(4, productRepository.count())
        );
    }

    @Test
    void deleteProduct() {
        productRepository.deleteById(1L);
        assertEquals(2, productRepository.count());
    }

    @Test
    void deleteAllInBatch() {
        productRepository.deleteAllInBatch();
        assertEquals(0, productRepository.count());
    }
}