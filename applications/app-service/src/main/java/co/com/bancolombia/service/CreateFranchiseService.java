package co.com.bancolombia.service;

import co.com.bancolombia.model.Franchise;
import co.com.bancolombia.usecase.exceptions.DuplicateFranchiseException;
import co.com.bancolombia.usecase.in.franchise.CreateFranchiseUseCase;
import co.com.bancolombia.model.gateway.FranchiseRepositoryPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class CreateFranchiseService implements CreateFranchiseUseCase {

    private final FranchiseRepositoryPort franchiseRepositoryPort;

    public CreateFranchiseService(FranchiseRepositoryPort franchiseRepositoryPort) {
        this.franchiseRepositoryPort = franchiseRepositoryPort;
    }

    @Override
    public Mono<Franchise> create(Franchise franchise) {
        log.info("Creating Franchise with name {}", franchise.getName());

        return Mono.defer(() -> this.franchiseRepositoryPort.findByName(franchise.getName())
                .flatMap(franchiseDB -> {
                    log.warn("Franchise with name {} already exists!", franchise.getName());
                    return Mono.<Franchise>error(new DuplicateFranchiseException(franchise.getName()));
                })
                .doOnError(error -> log.error("Error searching franchise {}", error.getMessage()))
                .switchIfEmpty(
                        this.franchiseRepositoryPort.save(franchise)
                                .doOnSuccess(newFranchise -> log.info("Franchise {} created successfully!", newFranchise.getName()))
                                .doOnError(error -> log.error("Error while creating Franchise {}", error.getMessage()))
                )
        );
    }
}
