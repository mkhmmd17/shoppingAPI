package com.oreilly.shopping.dao;

import com.oreilly.shopping.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> { //takes Generic Entity type and Primarly Key

    
}
