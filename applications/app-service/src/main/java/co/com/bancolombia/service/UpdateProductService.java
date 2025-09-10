package co.com.bancolombia.service;

import co.com.bancolombia.model.Branch;
import co.com.bancolombia.model.Product;
import co.com.bancolombia.model.gateway.FranchiseRepositoryPort;
import co.com.bancolombia.service.base.BaseFranchiseService;
import co.com.bancolombia.usecase.in.product.UpdateProductUseCase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class UpdateProductService extends BaseFranchiseService implements UpdateProductUseCase {

    public UpdateProductService(FranchiseRepositoryPort franchiseRepositoryPort) {
        super(franchiseRepositoryPort);
    }

    @Override
    public Mono<Product> updateProduct(String franchiseId, String branchId, Product product) {
        logOperationStart("Updating Product %s in Branch %s", product.getName(), branchId);

        return franchiseRepositoryPort.findById(franchiseId)
                .flatMap(franchise -> {
                    Branch branch = findBranchOrThrow(franchise, branchId);
                    Product existingProduct = findProductOrThrow(branch, product.getId());
                    validateProductNameNotDuplicated(branch, product.getName());
                    
                    updateProductFields(existingProduct, product);
                    
                    return saveFranchiseAndReturn(franchise, existingProduct);
                })
                .doOnSuccess(updatedProduct -> logSuccess("Product update"))
                .doOnError(error -> logError("updating Product", error.getMessage()));
    }

    private void updateProductFields(Product existingProduct, Product updateData) {
        updateProductName(existingProduct, updateData.getName());
        updateProductStock(existingProduct, updateData.getStock());
    }

    private void updateProductName(Product existingProduct, String newName) {
        if (newName != null && !newName.isBlank()) {
            existingProduct.setName(newName);
        }
    }

    private void updateProductStock(Product existingProduct, Integer stockIncrement) {
        if (stockIncrement != null && stockIncrement >= 0) {
            existingProduct.setStock(existingProduct.getStock() + stockIncrement);
        }
    }
}
