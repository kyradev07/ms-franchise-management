package co.com.bancolombia.api.router;

import co.com.bancolombia.api.dto.FranchiseDTO;
import co.com.bancolombia.api.mappers.FranchisMapperDTO;
import co.com.bancolombia.api.validations.MissingRequestBodyException;
import co.com.bancolombia.api.validations.FieldsValidator;
import co.com.bancolombia.usecase.in.franchise.CreateFranchiseUseCase;
import co.com.bancolombia.usecase.in.franchise.UpdateFranchiseNameUseCase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class FranchiseHandler {

    private final CreateFranchiseUseCase createFranchiseUseCase;
    private final UpdateFranchiseNameUseCase updateFranchiseNameUseCase;
    private final FieldsValidator fieldsValidator;

    public FranchiseHandler(
            CreateFranchiseUseCase createFranchiseUseCase,
            UpdateFranchiseNameUseCase updateFranchiseNameUseCase,
            FieldsValidator fieldsValidator) {
        this.createFranchiseUseCase = createFranchiseUseCase;
        this.updateFranchiseNameUseCase = updateFranchiseNameUseCase;
        this.fieldsValidator = fieldsValidator;
    }

    public Mono<ServerResponse> createFranchise(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(FranchiseDTO.class)
                .switchIfEmpty(Mono.error(new MissingRequestBodyException("Body cannot be null")))
                .map(fieldsValidator::validate)
                .map(FranchisMapperDTO::toDomain)
                .flatMap(this.createFranchiseUseCase::create)
                .map(FranchisMapperDTO::toDTO)
                .flatMap(ServerResponse.status(HttpStatus.CREATED)::bodyValue);
    }

    public Mono<ServerResponse> updateFranchiseName(ServerRequest serverRequest) {
        String id = serverRequest.pathVariable("id");
        return serverRequest.bodyToMono(FranchiseDTO.class)
                .switchIfEmpty(Mono.error(new MissingRequestBodyException("Body cannot be null")))
                .map(fieldsValidator::validate)
                .map(FranchisMapperDTO::toDomain)
                .flatMap(franchise -> this.updateFranchiseNameUseCase.updateName(id, franchise.getName()))
                .map(FranchisMapperDTO::toDTO)
                .flatMap(ServerResponse.status(HttpStatus.CREATED)::bodyValue);
    }
}
