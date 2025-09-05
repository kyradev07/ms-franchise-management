package co.com.bancolombia.usecase.in;

import co.com.bancolombia.model.Franchise;
import reactor.core.publisher.Mono;

public interface UpdateFranchiseNameUseCase {
    Mono<Franchise> updateName(String franchiseId, String name);
}
