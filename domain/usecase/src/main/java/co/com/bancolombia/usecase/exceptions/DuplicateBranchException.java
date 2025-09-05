package co.com.bancolombia.usecase.exceptions;

public class DuplicateBranchException extends RuntimeException {
    public DuplicateBranchException(String message, String franchiseName) {
        super("Branch with name: " + message + " already exists in Franchise: " + franchiseName);
    }
}
