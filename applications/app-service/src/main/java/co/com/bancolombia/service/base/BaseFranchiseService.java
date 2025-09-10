package co.com.bancolombia.service.base;

import co.com.bancolombia.model.Branch;
import co.com.bancolombia.model.Franchise;
import co.com.bancolombia.model.Product;
import co.com.bancolombia.model.gateway.FranchiseRepositoryPort;
import co.com.bancolombia.usecase.exceptions.BranchNotFoundException;
import co.com.bancolombia.usecase.exceptions.DuplicateBranchException;
import co.com.bancolombia.usecase.exceptions.DuplicateProductException;
import co.com.bancolombia.usecase.exceptions.ProductNotFoundException;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
public abstract class BaseFranchiseService {

    protected final FranchiseRepositoryPort franchiseRepositoryPort;

    protected BaseFranchiseService(FranchiseRepositoryPort franchiseRepositoryPort) {
        this.franchiseRepositoryPort = franchiseRepositoryPort;
    }

    protected Branch findBranchOrThrow(Franchise franchise, String branchId) {
        Branch branch = franchise.findBranchById(branchId);
        if (branch == null) {
            log.warn("Branch with id {} does not exist in Franchise {}", branchId, franchise.getName());
            throw new BranchNotFoundException(branchId);
        }
        return branch;
    }

    protected Product findProductOrThrow(Branch branch, String productId) {
        Product product = branch.findProductById(productId);
        if (product == null) {
            log.warn("Product with id {} does not exist in Branch", productId);
            throw new ProductNotFoundException(productId);
        }
        return product;
    }

    protected void validateBranchNameNotDuplicated(Franchise franchise, String branchName) {
        if (franchise.existsBranchByName(branchName)) {
            log.warn("Branch with name {} already exists in Franchise", branchName);
            throw new DuplicateBranchException(branchName, franchise.getName());
        }
    }

    protected void validateProductNameNotDuplicated(Branch branch, String productName) {
        if (productName != null && !productName.isBlank() && branch.existsProductByName(branch, productName)) {
            log.warn("Product with name {} already exists in Branch", productName);
            throw new DuplicateProductException(productName, branch.getName());
        }
    }

    protected <T> Mono<T> saveFranchiseAndReturn(Franchise franchise, T returnValue) {
        return franchiseRepositoryPort.save(franchise).thenReturn(returnValue);
    }

    protected Mono<Void> saveFranchise(Franchise franchise) {
        return franchiseRepositoryPort.save(franchise).then();
    }

    protected void logSuccess(String operation) {
        log.info("{} completed successfully!", operation);
    }

    protected void logError(String operation, String errorMessage) {
        log.error("Error while {}: {}", operation, errorMessage);
    }

    protected void logOperationStart(String operation, Object... parameters) {
        log.info("Starting {}", String.format(operation, parameters));
    }
}