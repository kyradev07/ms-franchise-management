package co.com.bancolombia.service;

import co.com.bancolombia.model.Franchise;
import co.com.bancolombia.model.gateway.FranchiseRepositoryPort;
import co.com.bancolombia.service.base.BaseFranchiseService;
import co.com.bancolombia.usecase.exceptions.DuplicateFranchiseException;
import co.com.bancolombia.usecase.in.franchise.UpdateFranchiseNameUseCase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class UpdateFranchiseNameService extends BaseFranchiseService implements UpdateFranchiseNameUseCase {

    public UpdateFranchiseNameService(FranchiseRepositoryPort franchiseRepositoryPort) {
        super(franchiseRepositoryPort);
    }

    @Override
    public Mono<Franchise> updateName(String franchiseId, Franchise franchise) {
        logOperationStart("Updating Franchise Name for ID %s", franchiseId);

        return franchiseRepositoryPort.findById(franchiseId)
                .doOnNext(franchiseDb -> franchiseDb.setName(franchise.getName()))
                .flatMap(franchiseDB -> franchiseRepositoryPort.save(franchiseDB)
                        .onErrorResume(e -> Mono.error(new DuplicateFranchiseException(franchise.getName())))
                )
                .doOnSuccess(updatedFranchise -> logSuccess("Franchise name update"))
                .doOnError(error -> logError("updating Franchise", error.getMessage()));
    }
}
