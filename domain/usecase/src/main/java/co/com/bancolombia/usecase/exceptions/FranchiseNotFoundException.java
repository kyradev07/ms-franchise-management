package co.com.bancolombia.usecase.exceptions;

public class FranchiseNotFoundException extends RuntimeException {
    public FranchiseNotFoundException(String message) {
        super("Franchise with id: " + message + " not found");
    }
}
