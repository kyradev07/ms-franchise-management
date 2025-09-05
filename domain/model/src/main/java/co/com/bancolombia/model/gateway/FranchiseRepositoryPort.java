package co.com.bancolombia.model.gateway;

import co.com.bancolombia.model.Franchise;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FranchiseRepositoryPort {
    Mono<Franchise> save(Franchise franchise);
    Mono<Franchise> findById(String id);
    Mono<Franchise> findByName(String name);
    Flux<Franchise> findAll();
    Mono<Franchise> updateFranchiseName(String id, String name);
    Mono<Franchise> addBranch(Franchise franchise);
}
