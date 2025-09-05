package co.com.bancolombia.api.validations;

public class MissingRequestBodyException extends RuntimeException {
    public MissingRequestBodyException(String message) {
        super(message);
    }
}
