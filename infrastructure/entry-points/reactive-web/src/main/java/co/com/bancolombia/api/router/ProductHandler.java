package co.com.bancolombia.api.router;

import co.com.bancolombia.api.dto.ProductDTO;
import co.com.bancolombia.api.mappers.MaxStockMapper;
import co.com.bancolombia.api.mappers.ProductMapperDTO;
import co.com.bancolombia.api.validations.FieldsValidator;
import co.com.bancolombia.api.validations.MissingRequestBodyException;
import co.com.bancolombia.usecase.in.product.AddProductToBranchUseCase;
import co.com.bancolombia.usecase.in.product.DeleteProductFromBranchUseCase;
import co.com.bancolombia.usecase.in.product.GetMaxStockByBranchInFranchiseUseCase;
import co.com.bancolombia.usecase.in.product.UpdateProductUseCase;
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
    private final GetMaxStockByBranchInFranchiseUseCase getMaxStockByBranchInFranchiseUseCase;
    private final DeleteProductFromBranchUseCase deleteProductFromBranchUseCase;
    private final UpdateProductUseCase updateProductUseCase;
    private final FieldsValidator fieldsValidator;

    public ProductHandler(
            AddProductToBranchUseCase addProductToBranchUseCase,
            GetMaxStockByBranchInFranchiseUseCase getMaxStockByBranchInFranchiseUseCase,
            DeleteProductFromBranchUseCase deleteProductFromBranchUseCase,
            UpdateProductUseCase updateProductUseCase,
            FieldsValidator fieldsValidator) {
        this.addProductToBranchUseCase = addProductToBranchUseCase;
        this.getMaxStockByBranchInFranchiseUseCase = getMaxStockByBranchInFranchiseUseCase;
        this.deleteProductFromBranchUseCase = deleteProductFromBranchUseCase;
        this.updateProductUseCase = updateProductUseCase;
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

    public Mono<ServerResponse> getMaxStock(ServerRequest serverRequest) {
        String franchiseId = serverRequest.pathVariable("franchiseId");
        return this.getMaxStockByBranchInFranchiseUseCase.getMaxStockByBranchInFranchise(franchiseId)
                .map(MaxStockMapper::toMaxStockDTO)
                .flatMap(ServerResponse.ok()::bodyValue);
    }

    public Mono<ServerResponse> deleteProductFromBranch(ServerRequest serverRequest) {
        String franchiseId = serverRequest.pathVariable("franchiseId");
        String branchId = serverRequest.pathVariable("branchId");
        String productId = serverRequest.pathVariable("productId");

        return this.deleteProductFromBranchUseCase.deleteProductFromBranch(franchiseId, branchId, productId)
                .then(ServerResponse.status(HttpStatus.NO_CONTENT).build());
    }

    public Mono<ServerResponse> updateProduct(ServerRequest serverRequest) {
        String franchiseId = serverRequest.pathVariable("franchiseId");
        String branchId = serverRequest.pathVariable("branchId");
        String productId = serverRequest.pathVariable("productId");

        return serverRequest.bodyToMono(ProductDTO.class)
                .switchIfEmpty(Mono.error(new MissingRequestBodyException("Body cannot be null")))
                .map(ProductMapperDTO::toDomain)
                .doOnNext(pr -> pr.setId(productId))
                .flatMap(product -> this.updateProductUseCase.updateProduct(franchiseId, branchId, product))
                .map(ProductMapperDTO::toDTO)
                .flatMap(ServerResponse.status(HttpStatus.CREATED)::bodyValue);
    }


}
