package co.com.bancolombia.usecase.exceptions;

public class DuplicateProductException extends RuntimeException {
    public DuplicateProductException(String message, String branchName) {
        super("Product with name: " + message + " already exists in Branch: " + branchName);
    }
}
