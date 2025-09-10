package co.com.bancolombia.service;

import co.com.bancolombia.model.Branch;
import co.com.bancolombia.model.Franchise;
import co.com.bancolombia.model.Product;
import co.com.bancolombia.model.gateway.FranchiseRepositoryPort;
import co.com.bancolombia.usecase.exceptions.DuplicateFranchiseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateFranchiseServiceTest {

    @Mock
    private FranchiseRepositoryPort franchiseRepositoryPort;

    private CreateFranchiseService service;

    @BeforeEach
    void setUp() {
        service = new CreateFranchiseService(franchiseRepositoryPort);
    }

    @Test
    @DisplayName("Should create franchise successfully")
    void shouldCreateFranchiseSuccessfully() {
        Franchise franchise = new Franchise("franchise1", "test franchise", new ArrayList<>());
        
        when(franchiseRepositoryPort.save(franchise)).thenReturn(Mono.just(franchise));

        Mono<Franchise> result = service.create(franchise);

        StepVerifier.create(result)
                .expectNextMatches(savedFranchise -> 
                        "franchise1".equals(savedFranchise.getId()) &&
                        "test franchise".equals(savedFranchise.getName()) &&
                        savedFranchise.getBranches().isEmpty()
                )
                .verifyComplete();
    }

    @Test
    @DisplayName("Should create franchise with branches and products")
    void shouldCreateFranchiseWithBranchesAndProducts() {
        Product product = new Product("product1", "product 1", 10);
        Branch branch = new Branch("branch1", "branch 1", List.of(product));
        Franchise franchise = new Franchise("franchise1", "test franchise", List.of(branch));
        
        when(franchiseRepositoryPort.save(franchise)).thenReturn(Mono.just(franchise));

        Mono<Franchise> result = service.create(franchise);

        StepVerifier.create(result)
                .expectNextMatches(savedFranchise -> 
                        "franchise1".equals(savedFranchise.getId()) &&
                        "test franchise".equals(savedFranchise.getName()) &&
                        savedFranchise.getBranches().size() == 1 &&
                        savedFranchise.getBranches().get(0).getProducts().size() == 1
                )
                .verifyComplete();
    }

    @Test
    @DisplayName("Should create franchise with null branches")
    void shouldCreateFranchiseWithNullBranches() {
        Franchise franchise = new Franchise("franchise1", "test franchise", null);
        
        when(franchiseRepositoryPort.save(franchise)).thenReturn(Mono.just(franchise));

        Mono<Franchise> result = service.create(franchise);

        StepVerifier.create(result)
                .expectNextMatches(savedFranchise -> 
                        "franchise1".equals(savedFranchise.getId()) &&
                        "test franchise".equals(savedFranchise.getName()) &&
                        savedFranchise.getBranches() == null
                )
                .verifyComplete();
    }

    @Test
    @DisplayName("Should handle DuplicateFranchiseException when franchise name already exists")
    void shouldHandleDuplicateFranchiseExceptionWhenFranchiseNameAlreadyExists() {
        Franchise franchise = new Franchise("franchise1", "existing franchise", new ArrayList<>());
        
        when(franchiseRepositoryPort.save(franchise))
                .thenReturn(Mono.error(new RuntimeException("Duplicate key")));

        Mono<Franchise> result = service.create(franchise);

        StepVerifier.create(result)
                .expectError(DuplicateFranchiseException.class)
                .verify();
    }

    @Test
    @DisplayName("Should handle database connection error")
    void shouldHandleDatabaseConnectionError() {
        Franchise franchise = new Franchise("franchise1", "test franchise", new ArrayList<>());
        
        when(franchiseRepositoryPort.save(franchise))
                .thenReturn(Mono.error(new RuntimeException("Connection timeout")));

        Mono<Franchise> result = service.create(franchise);

        StepVerifier.create(result)
                .expectError(DuplicateFranchiseException.class)
                .verify();
    }

    @Test
    @DisplayName("Should create franchise with special characters in name")
    void shouldCreateFranchiseWithSpecialCharactersInName() {
        Franchise franchise = new Franchise("franchise1", "franchise-name_with@special#chars", new ArrayList<>());
        
        when(franchiseRepositoryPort.save(franchise)).thenReturn(Mono.just(franchise));

        Mono<Franchise> result = service.create(franchise);

        StepVerifier.create(result)
                .expectNextMatches(savedFranchise -> 
                        "franchise1".equals(savedFranchise.getId()) &&
                        "franchise-name_with@special#chars".equals(savedFranchise.getName())
                )
                .verifyComplete();
    }

    @Test
    @DisplayName("Should create franchise with empty string name")
    void shouldCreateFranchiseWithEmptyStringName() {
        Franchise franchise = new Franchise("franchise1", "", new ArrayList<>());
        
        when(franchiseRepositoryPort.save(franchise)).thenReturn(Mono.just(franchise));

        Mono<Franchise> result = service.create(franchise);

        StepVerifier.create(result)
                .expectNextMatches(savedFranchise -> 
                        "franchise1".equals(savedFranchise.getId()) &&
                        "".equals(savedFranchise.getName())
                )
                .verifyComplete();
    }

    @Test
    @DisplayName("Should create franchise with null name")
    void shouldCreateFranchiseWithNullName() {
        Franchise franchise = new Franchise("franchise1", null, new ArrayList<>());
        
        when(franchiseRepositoryPort.save(franchise)).thenReturn(Mono.just(franchise));

        Mono<Franchise> result = service.create(franchise);

        StepVerifier.create(result)
                .expectNextMatches(savedFranchise -> 
                        "franchise1".equals(savedFranchise.getId()) &&
                        savedFranchise.getName() == null
                )
                .verifyComplete();
    }

    @Test
    @DisplayName("Should create franchise with multiple branches")
    void shouldCreateFranchiseWithMultipleBranches() {
        Product product1 = new Product("product1", "product 1", 10);
        Product product2 = new Product("product2", "product 2", 20);
        
        Branch branch1 = new Branch("branch1", "branch 1", List.of(product1));
        Branch branch2 = new Branch("branch2", "branch 2", List.of(product2));
        
        Franchise franchise = new Franchise("franchise1", "multi-branch franchise", List.of(branch1, branch2));
        
        when(franchiseRepositoryPort.save(franchise)).thenReturn(Mono.just(franchise));

        Mono<Franchise> result = service.create(franchise);

        StepVerifier.create(result)
                .expectNextMatches(savedFranchise -> 
                        "franchise1".equals(savedFranchise.getId()) &&
                        "multi-branch franchise".equals(savedFranchise.getName()) &&
                        savedFranchise.getBranches().size() == 2
                )
                .verifyComplete();
    }

    @Test
    @DisplayName("Should handle validation error")
    void shouldHandleValidationError() {
        Franchise franchise = new Franchise("franchise1", "test franchise", new ArrayList<>());
        
        when(franchiseRepositoryPort.save(franchise))
                .thenReturn(Mono.error(new IllegalArgumentException("Invalid franchise data")));

        Mono<Franchise> result = service.create(franchise);

        StepVerifier.create(result)
                .expectError(DuplicateFranchiseException.class)
                .verify();
    }

    @Test
    @DisplayName("Should create franchise using defer for lazy evaluation")
    void shouldCreateFranchiseUsingDeferForLazyEvaluation() {
        Franchise franchise = new Franchise("franchise1", "deferred franchise", new ArrayList<>());
        
        when(franchiseRepositoryPort.save(any(Franchise.class))).thenReturn(Mono.just(franchise));

        Mono<Franchise> result = service.create(franchise);

        // The operation should not execute until subscribed
        when(franchiseRepositoryPort.save(franchise)).thenReturn(Mono.just(franchise));

        StepVerifier.create(result)
                .expectNextMatches(savedFranchise -> 
                        "franchise1".equals(savedFranchise.getId()) &&
                        "deferred franchise".equals(savedFranchise.getName())
                )
                .verifyComplete();
    }
}