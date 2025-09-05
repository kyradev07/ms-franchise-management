package co.com.bancolombia.usecase.in.branch;

import co.com.bancolombia.model.Branch;
import reactor.core.publisher.Mono;

public interface UpdateBranchNameUseCase {
    Mono<Branch> updateName(String franchiseId, Branch branch);
}
