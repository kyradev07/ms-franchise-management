package co.com.bancolombia.api.router;

import co.com.bancolombia.api.dto.ProductDTO;
import co.com.bancolombia.api.mappers.ProductMapperDTO;
import co.com.bancolombia.api.validations.FieldsValidator;
import co.com.bancolombia.api.validations.MissingRequestBodyException;
import co.com.bancolombia.usecase.in.product.AddProductToBranchUseCase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class ProductHandler {
    private final AddProductToBranchUseCase addProductToBranchUseCase;
    private final FieldsValidator fieldsValidator;

    public ProductHandler(AddProductToBranchUseCase addProductToBranchUseCase, FieldsValidator fieldsValidator) {
        this.addProductToBranchUseCase = addProductToBranchUseCase;
        this.fieldsValidator = fieldsValidator;
    }

    public Mono<ServerResponse> addProductToBranch(ServerRequest serverRequest) {
        String franchiseId = serverRequest.pathVariable("franchiseId");
        String branchId = serverRequest.pathVariable("branchId");

        return serverRequest.bodyToMono(ProductDTO.class)
                .switchIfEmpty(Mono.error(new MissingRequestBodyException("Body cannot be null")))
                .map(fieldsValidator::validate)
                .map(ProductMapperDTO::toDomain)
                .flatMap(product -> this.addProductToBranchUseCase.addProductToBranch(franchiseId, branchId, product))
                .map(ProductMapperDTO::toDTO)
                .flatMap(ServerResponse.status(HttpStatus.CREATED)::bodyValue);
    }

}
