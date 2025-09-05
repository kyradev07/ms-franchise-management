package co.com.bancolombia.usecase.in.franchise;

import co.com.bancolombia.model.Franchise;
import reactor.core.publisher.Mono;

public interface CreateFranchiseUseCase {
    Mono<Franchise> create(Franchise franchise);
}
