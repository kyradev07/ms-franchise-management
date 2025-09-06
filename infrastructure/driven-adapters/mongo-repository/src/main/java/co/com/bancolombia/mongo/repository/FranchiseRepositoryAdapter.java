package co.com.bancolombia.mongo.repository;

import co.com.bancolombia.model.Franchise;
import co.com.bancolombia.model.gateway.FranchiseRepositoryPort;
import co.com.bancolombia.mongo.FranchiseMongoRepository;
import co.com.bancolombia.mongo.mappers.FranchiseMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Repository
public class FranchiseRepositoryAdapter implements FranchiseRepositoryPort {

    private final FranchiseMongoRepository franchiseMongoRepository;
    private final String message = "Franchise not found {}";

    public FranchiseRepositoryAdapter(FranchiseMongoRepository franchiseMongoRepository) {
        this.franchiseMongoRepository = franchiseMongoRepository;
    }

    @Override
    public Mono<Franchise> save(Franchise franchise) {
        return this.franchiseMongoRepository
                .save(FranchiseMapper.toDocument(franchise))
                .map(FranchiseMapper::toDomain)
                .doOnSuccess(f -> log.info("Franchise {} was save success.", f.getName()));
    }

    @Override
    public Mono<Franchise> findById(String id) {
        log.info("Find Franchise by id {}", id);
        return this.franchiseMongoRepository
                .findById(id)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Franchise not found")))
                .map(FranchiseMapper::toDomain)
                .doOnError(f -> log.info(message, id));
    }

    @Override
    public Mono<Franchise> findByName(String name) {
        log.info("Find Franchise by name {}", name);
        return this.franchiseMongoRepository
                .findByName(name)
                .map(FranchiseMapper::toDomain)
                .doOnError(f -> log.info(message, name));
    }

    @Override
    public Flux<Franchise> findAll() {
        return null;
    }

    @Override
    public Mono<Franchise> updateFranchiseName(String id, String name) {
        return this.franchiseMongoRepository.findById(id)
                .map(doc -> {
                    doc.setName(name);
                    return doc;
                })
                .flatMap(this.franchiseMongoRepository::save)
                .map(FranchiseMapper::toDomain)
                .doOnSuccess(f -> log.info("Franchise {} was updated.", f.getName()));
    }
}
