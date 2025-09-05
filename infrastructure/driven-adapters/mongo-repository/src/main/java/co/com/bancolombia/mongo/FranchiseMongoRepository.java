package co.com.bancolombia.mongo;

import co.com.bancolombia.mongo.documents.FranchiseDocument;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface FranchiseMongoRepository extends ReactiveMongoRepository<FranchiseDocument, String> {

    Mono<FranchiseDocument> findByName(String name);
}
