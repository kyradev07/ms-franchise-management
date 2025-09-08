package co.com.bancolombia.usecase.in.product;

import reactor.core.publisher.Mono;

public interface DeleteProductFromBranchUseCase {
    Mono<Void> deleteProductFromBranch(String franchiseId, String branchId, String productId);
}
