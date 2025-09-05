package co.com.bancolombia.usecase.in.product;

import co.com.bancolombia.model.Product;
import reactor.core.publisher.Mono;

public interface AddProductToBranchUseCase {
    Mono<Product> addProductToBranch(String franchiseId, String branchId, Product branchDTO);
}
