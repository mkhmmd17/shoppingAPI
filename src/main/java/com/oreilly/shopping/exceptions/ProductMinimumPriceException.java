package com.oreilly.shopping.exceptions;

public class ProductMinimumPriceException extends RuntimeException {
    public ProductMinimumPriceException() {
        this("Minimum Price must be greater than zero");
    }

    public ProductMinimumPriceException(String message) {
        super(message);
    }

    public ProductMinimumPriceException(double minPrice) {
        this("Minimum Price must be greater than zero" + minPrice);
    }
}
