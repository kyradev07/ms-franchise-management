package co.com.bancolombia.service;

import co.com.bancolombia.model.Branch;
import co.com.bancolombia.model.gateway.FranchiseRepositoryPort;
import co.com.bancolombia.service.base.BaseFranchiseService;
import co.com.bancolombia.usecase.exceptions.ProductNotFoundException;
import co.com.bancolombia.usecase.in.product.DeleteProductFromBranchUseCase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class DeleteProductFromBranchService extends BaseFranchiseService implements DeleteProductFromBranchUseCase {

    public DeleteProductFromBranchService(FranchiseRepositoryPort franchiseRepositoryPort) {
        super(franchiseRepositoryPort);
    }

    @Override
    public Mono<Void> deleteProductFromBranch(String franchiseId, String branchId, String productId) {
        logOperationStart("Deleting Product %s from Branch %s", productId, branchId);

        return franchiseRepositoryPort.findById(franchiseId)
                .flatMap(franchise -> {
                    Branch branch = findBranchOrThrow(franchise, branchId);
                    removeProductFromBranch(branch, productId);
                    return saveFranchise(franchise);
                })
                .doOnSuccess(result -> logSuccess("Product deletion"))
                .doOnError(error -> logError("deleting Product", error.getMessage()));
    }

    private void removeProductFromBranch(Branch branch, String productId) {
        boolean productRemoved = branch.getProducts().removeIf(product -> product.getId().equals(productId));
        if (!productRemoved) {
            log.warn("Product with id {} does not exist in Branch", productId);
            throw new ProductNotFoundException(productId);
        }
    }
}
