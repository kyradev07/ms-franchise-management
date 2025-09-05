package co.com.bancolombia.api.router;

import co.com.bancolombia.api.dto.FranchiseDTO;
import co.com.bancolombia.api.mappers.FranchisMapperDTO;
import co.com.bancolombia.api.validations.ErrorMessage;
import co.com.bancolombia.api.validations.MissingRequestBodyException;
import co.com.bancolombia.api.validations.ValidationFilter;
import co.com.bancolombia.usecase.exceptions.DuplicateFranchiseException;
import co.com.bancolombia.usecase.in.franchise.CreateFranchiseUseCase;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Component
public class FranchiseHandler {

    private final CreateFranchiseUseCase createFranchiseUseCase;
    private final ValidationFilter validatorFilter;

    public FranchiseHandler(CreateFranchiseUseCase createFranchiseUseCase, ValidationFilter validatorFilter) {
        this.createFranchiseUseCase = createFranchiseUseCase;
        this.validatorFilter = validatorFilter;
    }

    public Mono<ServerResponse> createFranchise(ServerRequest serverRequest) {
        log.info("Franchise request {}", serverRequest.uri());
        return serverRequest.bodyToMono(FranchiseDTO.class)
                .switchIfEmpty(Mono.error(new MissingRequestBodyException("Body cannot be null")))
                .map(validatorFilter::validate)
                .map(FranchisMapperDTO::toDomain)
                .flatMap(this.createFranchiseUseCase::create)
                .map(FranchisMapperDTO::toDTO)
                .flatMap(ServerResponse.status(HttpStatus.CREATED)::bodyValue)
                .onErrorResume(ConstraintViolationException.class,
                        e -> ServerResponse.badRequest()
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(ValidationErrorResponse.from(e)))
                .onErrorResume(MissingRequestBodyException.class,
                        e -> ServerResponse.badRequest()
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(new ErrorMessage(e.getMessage())))
                .onErrorResume(DuplicateFranchiseException.class,
                        e -> ServerResponse.badRequest()
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(new ErrorMessage(e.getMessage())))
                .onErrorResume(RuntimeException.class,
                        e -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue(new ErrorMessage(e.getMessage())));
    }

    public record ValidationErrorResponse(List<FieldError> errors) {
        public static ValidationErrorResponse from(ConstraintViolationException e) {
            var errs = e.getConstraintViolations().stream()
                    .map(v -> new FieldError(v.getPropertyPath().toString(), v.getMessage()))
                    .toList();
            return new ValidationErrorResponse(errs);
        }

        public record FieldError(String field, String error) {
        }
    }


}
