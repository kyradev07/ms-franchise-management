package co.com.bancolombia.api.router;

import co.com.bancolombia.api.dto.BranchDTO;
import co.com.bancolombia.api.mappers.BranchMapperDTO;
import co.com.bancolombia.api.validations.FieldsValidator;
import co.com.bancolombia.api.validations.MissingRequestBodyException;
import co.com.bancolombia.usecase.in.branch.AddBranchToFranchiseUseCase;
import co.com.bancolombia.usecase.in.branch.UpdateBranchNameUseCase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class BranchHandler {

    private final AddBranchToFranchiseUseCase addBranchToFranchiseUseCase;
    private final UpdateBranchNameUseCase updateBranchNameUseCase;
    private final FieldsValidator fieldsValidator;

    public BranchHandler(
            AddBranchToFranchiseUseCase addBranchToFranchiseUseCase,
            UpdateBranchNameUseCase updateBranchNameUseCase,
            FieldsValidator fieldsValidator) {
        this.addBranchToFranchiseUseCase = addBranchToFranchiseUseCase;
        this.updateBranchNameUseCase = updateBranchNameUseCase;
        this.fieldsValidator = fieldsValidator;
    }

    public Mono<ServerResponse> addBranchToFranchise(ServerRequest serverRequest) {
        String franchiseId = serverRequest.pathVariable("franchiseId");
        return serverRequest.bodyToMono(BranchDTO.class)
                .switchIfEmpty(Mono.error(new MissingRequestBodyException("Body cannot be null")))
                .map(fieldsValidator::validate)
                .map(BranchMapperDTO::toDomain)
                .flatMap(branch -> this.addBranchToFranchiseUseCase.addBranchToFranchise(franchiseId, branch))
                .map(BranchMapperDTO::toDTO)
                .flatMap(ServerResponse.status(HttpStatus.CREATED)::bodyValue);
    }

    public Mono<ServerResponse> updateBranchName(ServerRequest serverRequest) {
        String franchiseId = serverRequest.pathVariable("franchiseId");
        String branchId = serverRequest.pathVariable("branchId");

        return serverRequest.bodyToMono(BranchDTO.class)
                .switchIfEmpty(Mono.error(new MissingRequestBodyException("Body cannot be null")))
                .map(fieldsValidator::validate)
                .map(BranchMapperDTO::toDomain)
                .doOnNext(br -> br.setId(branchId))
                .flatMap(branch -> this.updateBranchNameUseCase.updateName(franchiseId, branch))
                .map(BranchMapperDTO::toDTO)
                .flatMap(ServerResponse.status(HttpStatus.CREATED)::bodyValue);

    }
}
