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

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateFranchiseNameServiceTest {

    @Mock
    private FranchiseRepositoryPort franchiseRepositoryPort;

    private UpdateFranchiseNameService service;
    private Franchise existingFranchise;

    @BeforeEach
    void setUp() {
        service = new UpdateFranchiseNameService(franchiseRepositoryPort);
        
        Product product = new Product("product1", "product 1", 10);
        Branch branch = new Branch("branch1", "branch 1", List.of(product));
        existingFranchise = new Franchise("franchise1", "original name", List.of(branch));
    }

    @Test
    @DisplayName("Should update franchise name successfully")
    void shouldUpdateFranchiseNameSuccessfully() {
        Franchise updateData = new Franchise(null, "updated name", null);
        Franchise updatedFranchise = new Franchise("franchise1", "updated name", existingFranchise.getBranches());
        
        when(franchiseRepositoryPort.findById("franchise1")).thenReturn(Mono.just(existingFranchise));
        when(franchiseRepositoryPort.save(any(Franchise.class))).thenReturn(Mono.just(updatedFranchise));

        Mono<Franchise> result = service.updateName("franchise1", updateData);

        StepVerifier.create(result)
                .expectNextMatches(franchise -> 
                        "franchise1".equals(franchise.getId()) &&
                        "updated name".equals(franchise.getName()) &&
                        franchise.getBranches().size() == 1
                )
                .verifyComplete();
    }

    @Test
    @DisplayName("Should handle DuplicateFranchiseException when name already exists")
    void shouldHandleDuplicateFranchiseExceptionWhenNameAlreadyExists() {
        Franchise updateData = new Franchise(null, "duplicate name", null);
        
        when(franchiseRepositoryPort.findById("franchise1")).thenReturn(Mono.just(existingFranchise));
        when(franchiseRepositoryPort.save(any(Franchise.class)))
                .thenReturn(Mono.error(new RuntimeException("Duplicate key constraint")));

        Mono<Franchise> result = service.updateName("franchise1", updateData);

        StepVerifier.create(result)
                .expectError(DuplicateFranchiseException.class)
                .verify();
    }

    @Test
    @DisplayName("Should update franchise name with special characters")
    void shouldUpdateFranchiseNameWithSpecialCharacters() {
        Franchise updateData = new Franchise(null, "franchise-name_with@special#chars", null);
        Franchise updatedFranchise = new Franchise("franchise1", "franchise-name_with@special#chars", existingFranchise.getBranches());
        
        when(franchiseRepositoryPort.findById("franchise1")).thenReturn(Mono.just(existingFranchise));
        when(franchiseRepositoryPort.save(any(Franchise.class))).thenReturn(Mono.just(updatedFranchise));

        Mono<Franchise> result = service.updateName("franchise1", updateData);

        StepVerifier.create(result)
                .expectNextMatches(franchise -> 
                        "franchise1".equals(franchise.getId()) &&
                        "franchise-name_with@special#chars".equals(franchise.getName())
                )
                .verifyComplete();
    }

    @Test
    @DisplayName("Should update franchise name to empty string")
    void shouldUpdateFranchiseNameToEmptyString() {
        Franchise updateData = new Franchise(null, "", null);
        Franchise updatedFranchise = new Franchise("franchise1", "", existingFranchise.getBranches());
        
        when(franchiseRepositoryPort.findById("franchise1")).thenReturn(Mono.just(existingFranchise));
        when(franchiseRepositoryPort.save(any(Franchise.class))).thenReturn(Mono.just(updatedFranchise));

        Mono<Franchise> result = service.updateName("franchise1", updateData);

        StepVerifier.create(result)
                .expectNextMatches(franchise -> 
                        "franchise1".equals(franchise.getId()) &&
                        "".equals(franchise.getName())
                )
                .verifyComplete();
    }

    @Test
    @DisplayName("Should update franchise name to null")
    void shouldUpdateFranchiseNameToNull() {
        Franchise updateData = new Franchise(null, null, null);
        Franchise updatedFranchise = new Franchise("franchise1", null, existingFranchise.getBranches());
        
        when(franchiseRepositoryPort.findById("franchise1")).thenReturn(Mono.just(existingFranchise));
        when(franchiseRepositoryPort.save(any(Franchise.class))).thenReturn(Mono.just(updatedFranchise));

        Mono<Franchise> result = service.updateName("franchise1", updateData);

        StepVerifier.create(result)
                .expectNextMatches(franchise -> 
                        "franchise1".equals(franchise.getId()) &&
                        franchise.getName() == null
                )
                .verifyComplete();
    }

    @Test
    @DisplayName("Should handle franchise repository error")
    void shouldHandleFranchiseRepositoryError() {
        Franchise updateData = new Franchise(null, "updated name", null);
        
        when(franchiseRepositoryPort.findById("franchise1"))
                .thenReturn(Mono.error(new RuntimeException("Database error")));

        Mono<Franchise> result = service.updateName("franchise1", updateData);

        StepVerifier.create(result)
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    @DisplayName("Should handle save error")
    void shouldHandleSaveError() {
        Franchise updateData = new Franchise(null, "updated name", null);
        
        when(franchiseRepositoryPort.findById("franchise1")).thenReturn(Mono.just(existingFranchise));
        when(franchiseRepositoryPort.save(any(Franchise.class)))
                .thenReturn(Mono.error(new RuntimeException("Save error")));

        Mono<Franchise> result = service.updateName("franchise1", updateData);

        StepVerifier.create(result)
                .expectError(DuplicateFranchiseException.class)
                .verify();
    }

    @Test
    @DisplayName("Should handle franchise not found")
    void shouldHandleFranchiseNotFound() {
        Franchise updateData = new Franchise(null, "updated name", null);
        
        when(franchiseRepositoryPort.findById("nonexistent"))
                .thenReturn(Mono.empty());

        Mono<Franchise> result = service.updateName("nonexistent", updateData);

        StepVerifier.create(result)
                .verifyComplete();
    }

    @Test
    @DisplayName("Should preserve franchise branches and products after name update")
    void shouldPreserveFranchiseBranchesAndProductsAfterNameUpdate() {
        Franchise updateData = new Franchise(null, "new name", null);
        
        when(franchiseRepositoryPort.findById("franchise1")).thenReturn(Mono.just(existingFranchise));
        when(franchiseRepositoryPort.save(any(Franchise.class))).thenAnswer(invocation -> {
            Franchise savedFranchise = invocation.getArgument(0);
            return Mono.just(savedFranchise);
        });

        Mono<Franchise> result = service.updateName("franchise1", updateData);

        StepVerifier.create(result)
                .expectNextMatches(franchise -> 
                        "franchise1".equals(franchise.getId()) &&
                        "new name".equals(franchise.getName()) &&
                        franchise.getBranches().size() == 1 &&
                        "branch1".equals(franchise.getBranches().get(0).getId()) &&
                        franchise.getBranches().get(0).getProducts().size() == 1
                )
                .verifyComplete();
    }

    @Test
    @DisplayName("Should handle concurrent modification error")
    void shouldHandleConcurrentModificationError() {
        Franchise updateData = new Franchise(null, "updated name", null);
        
        when(franchiseRepositoryPort.findById("franchise1")).thenReturn(Mono.just(existingFranchise));
        when(franchiseRepositoryPort.save(any(Franchise.class)))
                .thenReturn(Mono.error(new RuntimeException("Optimistic locking failure")));

        Mono<Franchise> result = service.updateName("franchise1", updateData);

        StepVerifier.create(result)
                .expectError(DuplicateFranchiseException.class)
                .verify();
    }

    @Test
    @DisplayName("Should update franchise with very long name")
    void shouldUpdateFranchiseWithVeryLongName() {
        String longName = "a".repeat(1000);
        Franchise updateData = new Franchise(null, longName, null);
        Franchise updatedFranchise = new Franchise("franchise1", longName, existingFranchise.getBranches());
        
        when(franchiseRepositoryPort.findById("franchise1")).thenReturn(Mono.just(existingFranchise));
        when(franchiseRepositoryPort.save(any(Franchise.class))).thenReturn(Mono.just(updatedFranchise));

        Mono<Franchise> result = service.updateName("franchise1", updateData);

        StepVerifier.create(result)
                .expectNextMatches(franchise -> 
                        "franchise1".equals(franchise.getId()) &&
                        longName.equals(franchise.getName())
                )
                .verifyComplete();
    }

    @Test
    @DisplayName("Should handle validation error from repository")
    void shouldHandleValidationErrorFromRepository() {
        Franchise updateData = new Franchise(null, "invalid name", null);
        
        when(franchiseRepositoryPort.findById("franchise1")).thenReturn(Mono.just(existingFranchise));
        when(franchiseRepositoryPort.save(any(Franchise.class)))
                .thenReturn(Mono.error(new IllegalArgumentException("Invalid franchise name")));

        Mono<Franchise> result = service.updateName("franchise1", updateData);

        StepVerifier.create(result)
                .expectError(DuplicateFranchiseException.class)
                .verify();
    }
}