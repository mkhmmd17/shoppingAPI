package com.oreilly.shopping.controllers;

import com.oreilly.shopping.entities.Product;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

//import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
class ProductRestControllerTest {

    @Autowired
    private WebTestClient webClient;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private List<Long> getIds() {
        return jdbcTemplate.queryForList("select id from product", Long.class);
    }

    private Product getProduct(Long id) {
        return jdbcTemplate.queryForObject("select * from product where id = ?",
                (rs, row) -> new Product(rs.getLong("id"),
                        rs.getString("name"),
                        rs.getBigDecimal("price")),
                id);
    }


    @Test
    void getAllProducts() {
        List<Long> productIds = getIds();
        assertThat(productIds).doesNotContain(999L);
        System.out.println("There are " + productIds.size() + " products in database.");
        webClient.get()
                .uri("/products")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Product.class).hasSize(3)
                .consumeWith(System.out::println);
    }


    @ParameterizedTest(name = "Product ID: {0}")
    @MethodSource("getIds")
    void getProductsThatExist(Long id) {
        webClient.get()
                .uri("/products/{id}", id)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(id);
    }


    @Test
    void getProductThatDoesNotExist() {
        List<Long> productIds = getIds();
        assertThat(productIds).doesNotContain(999L);
        System.out.println("There are " + productIds.size() + " products in database.");
        webClient.get()
                .uri("/products/999")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void productWithValidMinPrice() {
        webClient.get()
                .uri("/products?min=5.00")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Product.class).hasSize(2)
                .consumeWith(System.out::println);
    }

    @Test
    void productWithInvalidMinPrice() {
        webClient.get()
                .uri("/products?min=-1.00")
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void insertProduct() {
        List<Long> productIds = getIds();
        assertThat(productIds).doesNotContain(999L);
        System.out.println("There are " + productIds.size() + " products in database.");
        Product product = new Product("Chair", BigDecimal.valueOf(49.99));
        webClient.post()
                .uri("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(product), Product.class)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").isNotEmpty()
                .jsonPath("$.name").isEqualTo("Chair")
                .jsonPath("$.price").isEqualTo( 49.99);
    }

    @Test
    void updateProduct() {
        Product product = getProduct(getIds().get(0));
        product.setPrice(product.getPrice()
                        .add(BigDecimal.ONE));

        webClient.put()
                .uri("/products/{id}", product.getId())
                .body(Mono.just(product), Product.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Product.class)
                .consumeWith(System.out::println);
    }

    @Test
    void deleteSingleProduct() {
        List<Long> productIds = getIds();
        System.out.println("There are " + productIds.size() + " products in database.");
        if (productIds.size() == 0) {
            System.out.println("There are no products in database.");
            return;
        }


        webClient.get()
                .uri("/products/{id}", productIds.get(0))
                .exchange()
                .expectStatus().isOk();

        webClient.delete()
                .uri("/products/{id}", productIds.get(0))
                .exchange()
                .expectStatus().isNoContent();


        webClient.get()
                .uri("/products/{id}", productIds.get(0))
                .exchange()
                .expectStatus().isNotFound();
    }


    @Test
    void deleteAllProducts() {
        List<Long> productIds = getIds();
        System.out.println("There are " + productIds.size() + " products in database.");

        webClient.delete()
                .uri("/products")
                .exchange()
                .expectStatus().isNoContent();

        webClient.get()
                .uri("/products")
                .exchange()
                .expectBodyList(Product.class).hasSize(0);
    }
}