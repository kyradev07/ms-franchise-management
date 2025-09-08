package co.com.bancolombia.service;

import co.com.bancolombia.model.Franchise;
import co.com.bancolombia.model.gateway.FranchiseRepositoryPort;
import co.com.bancolombia.usecase.exceptions.DuplicateFranchiseException;
import co.com.bancolombia.usecase.exceptions.FranchiseNotFoundException;
import co.com.bancolombia.usecase.in.franchise.UpdateFranchiseNameUseCase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class UpdateFranchiseNameService implements UpdateFranchiseNameUseCase {

    private final FranchiseRepositoryPort franchiseRepositoryPort;

    public UpdateFranchiseNameService(FranchiseRepositoryPort franchiseRepositoryPort) {
        this.franchiseRepositoryPort = franchiseRepositoryPort;
    }

    @Override
    public Mono<Franchise> updateName(String franchiseId, Franchise franchise) {
        log.info("Updating Franchise Name {}", franchiseId);

        return this.franchiseRepositoryPort.findById(franchiseId)
                .switchIfEmpty(Mono.error(new FranchiseNotFoundException(franchiseId)))
                .map(franchiseDB -> {
                    franchiseDB.setName(franchise.getName());
                    return franchiseDB;
                })
                .flatMap(this.franchiseRepositoryPort::save)
                .doOnSuccess(updatedF -> log.info("Franchise {} created successfully!", updatedF.getName()))
                .doOnError(error -> log.error("Error while updating Franchise {}", error.getMessage()))
                .onErrorResume(e -> Mono.error(new DuplicateFranchiseException(franchise.getName())));
    }
}
