package co.com.bancolombia.service;

import co.com.bancolombia.model.Branch;
import co.com.bancolombia.model.gateway.FranchiseRepositoryPort;
import co.com.bancolombia.usecase.exceptions.DuplicateBranchException;
import co.com.bancolombia.usecase.in.branch.AddBranchToFranchiseUseCase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@Service
public class AddBranchToFranchiseService implements AddBranchToFranchiseUseCase {

    private final FranchiseRepositoryPort franchiseRepositoryPort;

    public AddBranchToFranchiseService(FranchiseRepositoryPort franchiseRepositoryPort) {
        this.franchiseRepositoryPort = franchiseRepositoryPort;
    }

    @Override
    public Mono<Branch> addBranchToFranchise(String franchiseId, Branch branch) {
        log.info("Adding Branch to Franchise {} with name {}", franchiseId, branch.getName());

        return Mono.defer(() -> this.franchiseRepositoryPort.findById(franchiseId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Franchise not found")))
                .flatMap(franchise -> {
                    boolean existsBranch = franchise.getBranches()
                            .stream()
                            .anyMatch(fBranch -> fBranch.getName().equals(branch.getName()));
                    if (existsBranch) {
                        log.warn("Branch with name {} already exists in Franchise", branch.getName());
                        return Mono.error(new DuplicateBranchException(branch.getName(), franchise.getName()));
                    }
                    branch.setId(UUID.randomUUID().toString());
                    franchise.getBranches().add(branch);
                    return this.franchiseRepositoryPort.addBranch(franchise)
                            .map(fr -> franchise.getBranches()
                                    .stream()
                                    .filter(br -> br.getId().equals(branch.getId()))
                                    .findFirst().orElse(branch)
                            )
                            .doOnSuccess(f -> log.info("Branch added successfully!"))
                            .doOnError(error -> log.error("Error while adding Branch {}", error.getMessage()));
                })
        );
    }
}
