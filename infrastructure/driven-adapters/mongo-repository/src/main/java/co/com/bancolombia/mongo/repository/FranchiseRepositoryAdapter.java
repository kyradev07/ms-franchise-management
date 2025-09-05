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
                .map(FranchiseMapper::toDomain)
                .doOnSuccess(f -> log.info("Franchise {} save success.", f.getName()));
    }

    @Override
    public Mono<Franchise> findById(String id) {
        return null;
    }

    @Override
    public Mono<Franchise> findByName(String name) {
        log.info("Find Franchise by name {}", name);
        return this.franchiseMongoRepository
                .findByName(name)
                .doOnNext(d -> log.info("valor id {}", d.getId()))
                .map(FranchiseMapper::toDomain).doOnError(f -> log.info("Franchise {} not found.", name));
    }

    @Override
    public Flux<Franchise> findAll() {
        return null;
    }
}
