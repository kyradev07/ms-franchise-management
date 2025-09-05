package co.com.bancolombia.usecase.in.branch;

import co.com.bancolombia.model.Branch;
import reactor.core.publisher.Mono;

public interface AddBranchToFranchiseUseCase {
    Mono<Branch> addBranchToFranchise(String franchiseId, Branch branch);
}
