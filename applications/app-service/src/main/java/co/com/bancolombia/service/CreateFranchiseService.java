package co.com.bancolombia.service;

import co.com.bancolombia.model.Franchise;
import co.com.bancolombia.model.gateway.FranchiseRepositoryPort;
import co.com.bancolombia.service.base.BaseFranchiseService;
import co.com.bancolombia.usecase.exceptions.DuplicateFranchiseException;
import co.com.bancolombia.usecase.in.franchise.CreateFranchiseUseCase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class CreateFranchiseService extends BaseFranchiseService implements CreateFranchiseUseCase {

    public CreateFranchiseService(FranchiseRepositoryPort franchiseRepositoryPort) {
        super(franchiseRepositoryPort);
    }

    @Override
    public Mono<Franchise> create(Franchise franchise) {
        logOperationStart("Creating Franchise with name %s", franchise.getName());

        return Mono.defer(() -> franchiseRepositoryPort.save(franchise)
                .doOnSuccess(newFranchise -> logSuccess("Franchise creation"))
                .doOnError(error -> logError("creating Franchise", error.getMessage()))
                .onErrorResume(e -> Mono.error(new DuplicateFranchiseException(franchise.getName())))
        );
    }
}
