package com.oreilly.shopping.dao;

import com.oreilly.shopping.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> { //takes Generic Entity type and Primarly Key
    List<Product> findAllByPriceGreaterThanEqual(BigDecimal minPrice);
}
