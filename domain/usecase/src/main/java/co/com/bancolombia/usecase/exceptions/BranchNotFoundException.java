package co.com.bancolombia.usecase.exceptions;

public class BranchNotFoundException extends RuntimeException {
    public BranchNotFoundException(String message) {
        super("Branch with id: " + message + " not found");
    }
}
