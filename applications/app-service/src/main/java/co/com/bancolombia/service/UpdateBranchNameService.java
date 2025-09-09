package co.com.bancolombia.service;

import co.com.bancolombia.model.Branch;
import co.com.bancolombia.model.gateway.FranchiseRepositoryPort;
import co.com.bancolombia.usecase.exceptions.BranchNotFoundException;
import co.com.bancolombia.usecase.exceptions.DuplicateBranchException;
import co.com.bancolombia.usecase.in.branch.UpdateBranchNameUseCase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class UpdateBranchNameService implements UpdateBranchNameUseCase {

    private final FranchiseRepositoryPort franchiseRepositoryPort;

    public UpdateBranchNameService(FranchiseRepositoryPort franchiseRepositoryPort) {
        this.franchiseRepositoryPort = franchiseRepositoryPort;
    }

    @Override
    public Mono<Branch> updateName(String franchiseId, Branch branch) {
        log.info("Updating Branch Name {}", branch.getName());

        return this.franchiseRepositoryPort.findById(franchiseId)
                .flatMap(franchise -> {

                    Branch branchDb = franchise.findBranchById(branch.getId());

                    if (branchDb == null) {
                        log.warn("Branch with id {} does not exists in Franchise {}", branch.getId(), franchise.getName());
                        return Mono.error(new BranchNotFoundException(branch.getId()));
                    }

                    if (franchise.existsBranchByName(branch.getName())) {
                        log.warn("Branch with name {} already exists in Franchise", branch.getName());
                        return Mono.error(new DuplicateBranchException(branch.getName(), franchise.getName()));
                    }

                    branchDb.setName(branch.getName());

                    return this.franchiseRepositoryPort.save(franchise)
                            .thenReturn(branchDb);
                })
                .doOnSuccess(updatedF -> log.info("Branch name {} was updated successfully!", updatedF.getName()))
                .doOnError(error -> log.error("Error while updating Branch name {}", error.getMessage()));
    }
}
