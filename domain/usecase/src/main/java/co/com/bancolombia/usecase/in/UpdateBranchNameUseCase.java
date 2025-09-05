package co.com.bancolombia.usecase.in;

import co.com.bancolombia.model.Branch;
import reactor.core.publisher.Mono;

public interface UpdateBranchNameUseCase {
    Mono<Branch> updateName(String franchiseId, String branchId, String name);
}
