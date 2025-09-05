package co.com.bancolombia.service;

import co.com.bancolombia.model.Franchise;
import co.com.bancolombia.model.gateway.FranchiseRepositoryPort;
import co.com.bancolombia.usecase.exceptions.DuplicateFranchiseException;
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
    public Mono<Franchise> updateName(String franchiseId, String name) {
        log.info("Updating Franchise Name {}", franchiseId);
        String fixedName = name.trim().toLowerCase();

        return Mono.defer(() -> this.franchiseRepositoryPort.findByName(fixedName)
                .flatMap(franchiseDB -> {
                    log.warn("Franchise with name {} already exists!", name);
                    return Mono.<Franchise>error(new DuplicateFranchiseException(fixedName));
                })
                .switchIfEmpty(
                        this.franchiseRepositoryPort.updateFranchiseName(franchiseId, name)
                                .doOnSuccess(updatedF -> log.info("Franchise {} created successfully!", updatedF.getName()))
                                .doOnError(error -> log.error("Error while updating Franchise {}", error.getMessage()))
                )
        );
    }
}
