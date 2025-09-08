package co.com.bancolombia.usecase.exceptions;

public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(String message) {
        super("Product with id: " + message + " not found");
    }
}
