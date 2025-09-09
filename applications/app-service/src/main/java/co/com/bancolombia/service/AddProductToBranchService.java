package co.com.bancolombia.service;

import co.com.bancolombia.model.Branch;
import co.com.bancolombia.model.Product;
import co.com.bancolombia.model.gateway.FranchiseRepositoryPort;
import co.com.bancolombia.usecase.exceptions.BranchNotFoundException;
import co.com.bancolombia.usecase.exceptions.DuplicateProductException;
import co.com.bancolombia.usecase.in.product.AddProductToBranchUseCase;
import co.com.bancolombia.utils.Filters;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@Service
public class AddProductToBranchService implements AddProductToBranchUseCase {

    private final FranchiseRepositoryPort franchiseRepositoryPort;

    public AddProductToBranchService(FranchiseRepositoryPort franchiseRepositoryPort) {
        this.franchiseRepositoryPort = franchiseRepositoryPort;
    }

    @Override
    public Mono<Product> addProductToBranch(String franchiseId, String branchId, Product product) {
        log.info("Adding Product to Branch {} to branch {}", product.getName(), branchId);

        return this.franchiseRepositoryPort.findById(franchiseId)
                .flatMap(franchise -> {

                    Branch branch = Filters.findBranchById(franchise, branchId);

                    if (branch == null) {
                        log.warn("Branch with id {} does not exists in Franchise {}", branchId, franchise.getName());
                        return Mono.error(new BranchNotFoundException(branchId));
                    }

                    if (Filters.existsProductByName(branch, product.getName())) {
                        log.warn("Product with name {} already exists in Branch", product.getName());
                        return Mono.error(new DuplicateProductException(product.getName(), branch.getName()));
                    }

                    product.setId(UUID.randomUUID().toString());
                    branch.getProducts().add(product);

                    return this.franchiseRepositoryPort.save(franchise)
                            .thenReturn(product);

                })
                .doOnSuccess(f -> log.info("Product added successfully!"))
                .doOnError(error -> log.error("Error while adding Product {}", error.getMessage())
        );

    }
}
