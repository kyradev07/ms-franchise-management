package co.com.bancolombia.api.validations;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class FieldsValidator {

    private final Validator validator;

    public FieldsValidator(Validator validator) {
        this.validator = validator;
    }

    public <T> T validate(T dto) {
        Set<ConstraintViolation<T>> violations = validator.validate(dto);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
        return dto;
    }
}