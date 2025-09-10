package co.com.bancolombia.service;

import co.com.bancolombia.model.Branch;
import co.com.bancolombia.model.gateway.FranchiseRepositoryPort;
import co.com.bancolombia.service.base.BaseFranchiseService;
import co.com.bancolombia.usecase.in.branch.UpdateBranchNameUseCase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class UpdateBranchNameService extends BaseFranchiseService implements UpdateBranchNameUseCase {

    public UpdateBranchNameService(FranchiseRepositoryPort franchiseRepositoryPort) {
        super(franchiseRepositoryPort);
    }

    @Override
    public Mono<Branch> updateName(String franchiseId, Branch branch) {
        logOperationStart("Updating Branch Name to %s", branch.getName());

        return franchiseRepositoryPort.findById(franchiseId)
                .flatMap(franchise -> {
                    Branch existingBranch = findBranchOrThrow(franchise, branch.getId());
                    validateBranchNameNotDuplicated(franchise, branch.getName());
                    
                    existingBranch.setName(branch.getName());
                    
                    return saveFranchiseAndReturn(franchise, existingBranch);
                })
                .doOnSuccess(updatedBranch -> logSuccess("Branch name update"))
                .doOnError(error -> logError("updating Branch name", error.getMessage()));
    }
}
