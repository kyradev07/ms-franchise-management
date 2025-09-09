package co.com.bancolombia.usecase.exceptions;

public class DuplicateFranchiseException extends RuntimeException {
    public DuplicateFranchiseException(String message) {
        super("Franchise with name <" + message + "> already exists");
    }
}
