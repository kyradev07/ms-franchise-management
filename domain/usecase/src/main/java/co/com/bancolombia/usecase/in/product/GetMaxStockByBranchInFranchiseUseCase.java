package co.com.bancolombia.usecase.in.product;

import co.com.bancolombia.model.Franchise;
import reactor.core.publisher.Mono;

public interface GetMaxStockByBranchInFranchiseUseCase {
    Mono<Franchise> getMaxStockByBranchInFranchise(String franchiseId);
}
