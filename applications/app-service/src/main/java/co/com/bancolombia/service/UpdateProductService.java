package co.com.bancolombia.service;

import co.com.bancolombia.model.Branch;
import co.com.bancolombia.model.Product;
import co.com.bancolombia.model.gateway.FranchiseRepositoryPort;
import co.com.bancolombia.usecase.exceptions.BranchNotFoundException;
import co.com.bancolombia.usecase.exceptions.DuplicateProductException;
import co.com.bancolombia.usecase.exceptions.ProductNotFoundException;
import co.com.bancolombia.usecase.in.product.UpdateProductUseCase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class UpdateProductService implements UpdateProductUseCase {

    private final FranchiseRepositoryPort franchiseRepositoryPort;

    public UpdateProductService(FranchiseRepositoryPort franchiseRepositoryPort) {
        this.franchiseRepositoryPort = franchiseRepositoryPort;
    }

    @Override
    public Mono<Product> updateProduct(String franchiseId, String branchId, Product product) {
        log.info("Updating Product {} in Branch {}", product.getName(), branchId);

        return this.franchiseRepositoryPort.findById(franchiseId)
                .flatMap(franchise -> {

                    Branch branchDb = franchise.findBranchById(branchId);

                    if (branchDb == null) {
                        log.warn("Branch with id {} does not exists in Franchise {}", branchId, franchise.getName());
                        return Mono.error(new BranchNotFoundException(branchId));
                    }

                    Product productDb = branchDb.findProductById(product.getId());

                    if (productDb == null) {
                        log.warn("Product with id {} does not exists in Branch", product.getId());
                        return Mono.error(new ProductNotFoundException(product.getId()));
                    }

                    if (branchDb.existsProductByName(branchDb, product.getName())) {
                        log.warn("Product with name {} already exists in Branch", product.getName());
                        return Mono.error(new DuplicateProductException(product.getName(), branchDb.getName()));
                    }

                    String name = product.getName() == null || product.getName().isBlank()
                            ? productDb.getName()
                            : product.getName();

                    Integer stock = product.getStock() == null || product.getStock() < 0
                            ? productDb.getStock()
                            : productDb.getStock() + product.getStock();

                    productDb.setName(name);
                    productDb.setStock(stock);

                    return this.franchiseRepositoryPort.save(franchise)
                            .thenReturn(productDb);

                })
                .doOnSuccess(f -> log.info("Product updated successfully!"))
                .doOnError(error -> log.error("Error while updating Product {}", error.getMessage())
                );
    }
}
