package co.com.bancolombia.service;

import co.com.bancolombia.model.Branch;
import co.com.bancolombia.model.gateway.FranchiseRepositoryPort;
import co.com.bancolombia.service.base.BaseFranchiseService;
import co.com.bancolombia.usecase.in.branch.AddBranchToFranchiseUseCase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@Service
public class AddBranchToFranchiseService extends BaseFranchiseService implements AddBranchToFranchiseUseCase {

    public AddBranchToFranchiseService(FranchiseRepositoryPort franchiseRepositoryPort) {
        super(franchiseRepositoryPort);
    }

    @Override
    public Mono<Branch> addBranchToFranchise(String franchiseId, Branch branch) {
        logOperationStart("Adding Branch %s to Franchise %s", branch.getName(), franchiseId);

        return franchiseRepositoryPort.findById(franchiseId)
                .flatMap(franchise -> {
                    validateBranchNameNotDuplicated(franchise, branch.getName());
                    
                    addBranchToFranchise(franchise, branch);
                    
                    return saveFranchiseAndReturn(franchise, branch);
                })
                .doOnSuccess(addedBranch -> logSuccess("Branch addition"))
                .doOnError(error -> logError("adding Branch", error.getMessage()));
    }

    private void addBranchToFranchise(co.com.bancolombia.model.Franchise franchise, Branch branch) {
        branch.setId(UUID.randomUUID().toString());
        franchise.getBranches().add(branch);
    }
}
