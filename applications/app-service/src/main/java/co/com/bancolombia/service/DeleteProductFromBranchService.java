package co.com.bancolombia.service;

import co.com.bancolombia.model.Branch;
import co.com.bancolombia.model.Product;
import co.com.bancolombia.model.gateway.FranchiseRepositoryPort;
import co.com.bancolombia.usecase.exceptions.BranchNotFoundException;
import co.com.bancolombia.usecase.exceptions.ProductNotFoundException;
import co.com.bancolombia.usecase.in.product.DeleteProductFromBranchUseCase;
import co.com.bancolombia.utils.Filters;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Service
public class DeleteProductFromBranchService implements DeleteProductFromBranchUseCase {

    private final FranchiseRepositoryPort franchiseRepositoryPort;

    public DeleteProductFromBranchService(FranchiseRepositoryPort franchiseRepositoryPort) {
        this.franchiseRepositoryPort = franchiseRepositoryPort;
    }

    @Override
    public Mono<Void> deleteProductFromBranch(String franchiseId, String branchId, String productId) {
        log.info("Deleting Product from Branch");

        return Mono.defer(() -> this.franchiseRepositoryPort.findById(franchiseId)
                .flatMap(franchise -> {
                    Branch branch = Filters.filterBranchById(franchise, branchId);

                    if (branch == null) {
                        log.warn("Branch with id {} does not exists in Franchise {}", branchId, franchise.getName());
                        return Mono.error(new BranchNotFoundException(branchId));
                    }

                    boolean existsProduct = Filters.filterProductById(branch, productId);

                    if (!existsProduct) {
                        log.warn("Product with id {} does not exists in Branch", productId);
                        return Mono.error(new ProductNotFoundException(productId));
                    }

                    List<Product> products = branch.getProducts()
                            .stream()
                            .filter(product -> !product.getId().equals(productId))
                            .toList();

                    branch.setProducts(products);

                    return this.franchiseRepositoryPort.save(franchise)
                            .doOnSuccess(f -> log.info("Product deleted successfully!"))
                            .doOnError(error -> log.error("Error while deleting Product {}", error.getMessage()));
                })
                .then()
        );
    }
}
