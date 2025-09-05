package co.com.bancolombia.service;

import co.com.bancolombia.model.Branch;
import co.com.bancolombia.model.gateway.FranchiseRepositoryPort;
import co.com.bancolombia.usecase.exceptions.BranchNotFoundException;
import co.com.bancolombia.usecase.exceptions.DuplicateBranchException;
import co.com.bancolombia.usecase.in.branch.UpdateBranchNameUseCase;
import co.com.bancolombia.utils.Filters;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class UpdateBranchNameService implements UpdateBranchNameUseCase {

    private final FranchiseRepositoryPort franchiseRepositoryPort;

    public UpdateBranchNameService(FranchiseRepositoryPort franchiseRepositoryPort) {
        this.franchiseRepositoryPort = franchiseRepositoryPort;
    }

    @Override
    public Mono<Branch> updateName(String franchiseId, Branch branch) {
        log.info("Updating Branch Name {}", branch.getName());

        return Mono.defer(() -> this.franchiseRepositoryPort.findById(franchiseId)
                .flatMap(franchise -> {

                    Branch branchDb = franchise.getBranches()
                            .stream()
                            .filter(br -> br.getId().equals(branch.getId()))
                            .findFirst().orElse(null);

                    if (branchDb == null) {
                        log.warn("Branch with id {} does not exists in Franchise {}", branch.getId(), franchise.getName());
                        return Mono.error(new BranchNotFoundException(branch.getId()));
                    }

                    boolean existName = Filters.filterByName(franchise, branch.getName());

                    if (existName) {
                        log.warn("Branch with name {} already exists in Franchise", branch.getName());
                        return Mono.error(new DuplicateBranchException(branch.getName(), franchise.getName()));
                    }

                    branchDb.setName(branch.getName());

                    return this.franchiseRepositoryPort.save(franchise)
                            .map(fr -> Filters.findBranch(franchise, branch))
                            .doOnSuccess(updatedF -> log.info("Branch name {} was updated successfully!", updatedF.getName()))
                            .doOnError(error -> log.error("Error while updating Branch name {}", error.getMessage()));
                })

        );
    }
}
