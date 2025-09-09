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

    public FranchiseRepositoryAdapter(FranchiseMongoRepository franchiseMongoRepository) {
        this.franchiseMongoRepository = franchiseMongoRepository;
    }

    @Override
    public Mono<Franchise> save(Franchise franchise) {
        return this.franchiseMongoRepository
                .save(FranchiseMapper.toDocument(franchise))
                .map(FranchiseMapper::toDomain);
    }

    @Override
    public Mono<Franchise> findById(String id) {
        log.info("Find Franchise by id {}", id);
        return this.franchiseMongoRepository
                .findById(id)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Franchise with id <" + id + "> not found!")))
                .map(FranchiseMapper::toDomain);
    }

    @Override
    public Mono<Franchise> findByName(String name) {
        log.info("Find Franchise by name {}", name);
        return this.franchiseMongoRepository
                .findByName(name)
                .map(FranchiseMapper::toDomain);
    }

    @Override
    public Flux<Franchise> findAll() {
        return null;
    }

}
