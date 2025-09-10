package co.com.bancolombia.service;

import co.com.bancolombia.model.Branch;
import co.com.bancolombia.model.Product;
import co.com.bancolombia.model.gateway.FranchiseRepositoryPort;
import co.com.bancolombia.service.base.BaseFranchiseService;
import co.com.bancolombia.usecase.in.product.AddProductToBranchUseCase;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;


@Service
public class AddProductToBranchService extends BaseFranchiseService implements AddProductToBranchUseCase {

    public AddProductToBranchService(FranchiseRepositoryPort franchiseRepositoryPort) {
        super(franchiseRepositoryPort);
    }

    @Override
    public Mono<Product> addProductToBranch(String franchiseId, String branchId, Product product) {
        logOperationStart("Adding Product %s to Branch %s", product.getName(), branchId);

        return franchiseRepositoryPort.findById(franchiseId)
                .flatMap(franchise -> {
                    Branch branch = findBranchOrThrow(franchise, branchId);
                    validateProductNameNotDuplicated(branch, product.getName());
                    
                    addProductToBranch(branch, product);
                    
                    return saveFranchiseAndReturn(franchise, product);
                })
                .doOnSuccess(addedProduct -> logSuccess("Product addition"))
                .doOnError(error -> logError("adding Product", error.getMessage()));
    }

    private void addProductToBranch(Branch branch, Product product) {
        product.setId(UUID.randomUUID().toString());
        branch.getProducts().add(product);
    }
}
