package com.oreilly.shopping.controllers;

import com.oreilly.shopping.entities.Product;
import com.oreilly.shopping.services.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductRestController {


    private final ProductService productService;

    public ProductRestController(ProductService productService) {
        this.productService = productService;
    }


    @GetMapping
    public List<Product> getProducts() {
        return productService.findAll();
    }

    @GetMapping("{id}")
    public ResponseEntity<Product> getProduct(@PathVariable Long id) {
        return ResponseEntity.of(productService.findProductById(id));
    }
}
