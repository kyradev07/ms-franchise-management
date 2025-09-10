package co.com.bancolombia.service;

import co.com.bancolombia.model.Branch;
import co.com.bancolombia.model.Franchise;
import co.com.bancolombia.model.Product;
import co.com.bancolombia.model.gateway.FranchiseRepositoryPort;
import co.com.bancolombia.usecase.exceptions.BranchNotFoundException;
import co.com.bancolombia.usecase.exceptions.DuplicateBranchException;
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
class UpdateBranchNameServiceTest {

    @Mock
    private FranchiseRepositoryPort franchiseRepositoryPort;

    private UpdateBranchNameService service;
    private Franchise franchise;
    private Branch branch1;
    private Branch branch2;

    @BeforeEach
    void setUp() {
        service = new UpdateBranchNameService(franchiseRepositoryPort);
        
        Product product = new Product("product1", "product 1", 10);
        
        branch1 = new Branch("branch1", "branch 1", List.of(product));
        branch2 = new Branch("branch2", "branch 2", new ArrayList<>());
        
        franchise = new Franchise("franchise1", "franchise 1", List.of(branch1, branch2));
    }

    @Test
    @DisplayName("Should update branch name successfully")
    void shouldUpdateBranchNameSuccessfully() {
        Branch branchUpdate = new Branch("branch1", "updated branch name", null);
        
        when(franchiseRepositoryPort.findById("franchise1")).thenReturn(Mono.just(franchise));
        when(franchiseRepositoryPort.save(any(Franchise.class))).thenReturn(Mono.just(franchise));

        Mono<Branch> result = service.updateName("franchise1", branchUpdate);

        StepVerifier.create(result)
                .expectNextMatches(branch -> 
                        "branch1".equals(branch.getId()) &&
                        "updated branch name".equals(branch.getName())
                )
                .verifyComplete();
    }

    @Test
    @DisplayName("Should throw BranchNotFoundException when branch does not exist")
    void shouldThrowBranchNotFoundExceptionWhenBranchDoesNotExist() {
        Branch branchUpdate = new Branch("nonexistent", "new name", null);
        
        when(franchiseRepositoryPort.findById("franchise1")).thenReturn(Mono.just(franchise));

        Mono<Branch> result = service.updateName("franchise1", branchUpdate);

        StepVerifier.create(result)
                .expectError(BranchNotFoundException.class)
                .verify();
    }

    @Test
    @DisplayName("Should throw DuplicateBranchException when name already exists")
    void shouldThrowDuplicateBranchExceptionWhenNameAlreadyExists() {
        Branch branchUpdate = new Branch("branch1", "branch 2", null);
        
        when(franchiseRepositoryPort.findById("franchise1")).thenReturn(Mono.just(franchise));

        Mono<Branch> result = service.updateName("franchise1", branchUpdate);

        StepVerifier.create(result)
                .expectError(DuplicateBranchException.class)
                .verify();
    }

    @Test
    @DisplayName("Should handle franchise repository error")
    void shouldHandleFranchiseRepositoryError() {
        Branch branchUpdate = new Branch("branch1", "updated name", null);
        
        when(franchiseRepositoryPort.findById("franchise1"))
                .thenReturn(Mono.error(new RuntimeException("Database error")));

        Mono<Branch> result = service.updateName("franchise1", branchUpdate);

        StepVerifier.create(result)
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    @DisplayName("Should handle save error")
    void shouldHandleSaveError() {
        Branch branchUpdate = new Branch("branch1", "updated name", null);
        
        when(franchiseRepositoryPort.findById("franchise1")).thenReturn(Mono.just(franchise));
        when(franchiseRepositoryPort.save(any(Franchise.class)))
                .thenReturn(Mono.error(new RuntimeException("Save error")));

        Mono<Branch> result = service.updateName("franchise1", branchUpdate);

        StepVerifier.create(result)
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    @DisplayName("Should handle franchise not found")
    void shouldHandleFranchiseNotFound() {
        Branch branchUpdate = new Branch("branch1", "updated name", null);
        
        when(franchiseRepositoryPort.findById("nonexistent"))
                .thenReturn(Mono.empty());

        Mono<Branch> result = service.updateName("nonexistent", branchUpdate);

        StepVerifier.create(result)
                .verifyComplete();
    }

    @Test
    @DisplayName("Should update branch name with special characters")
    void shouldUpdateBranchNameWithSpecialCharacters() {
        Branch branchUpdate = new Branch("branch1", "branch-name_with@special#chars", null);
        
        when(franchiseRepositoryPort.findById("franchise1")).thenReturn(Mono.just(franchise));
        when(franchiseRepositoryPort.save(any(Franchise.class))).thenReturn(Mono.just(franchise));

        Mono<Branch> result = service.updateName("franchise1", branchUpdate);

        StepVerifier.create(result)
                .expectNextMatches(branch -> 
                        "branch1".equals(branch.getId()) &&
                        "branch-name_with@special#chars".equals(branch.getName())
                )
                .verifyComplete();
    }

    @Test
    @DisplayName("Should update branch name with numbers")
    void shouldUpdateBranchNameWithNumbers() {
        Branch branchUpdate = new Branch("branch1", "branch 123", null);
        
        when(franchiseRepositoryPort.findById("franchise1")).thenReturn(Mono.just(franchise));
        when(franchiseRepositoryPort.save(any(Franchise.class))).thenReturn(Mono.just(franchise));

        Mono<Branch> result = service.updateName("franchise1", branchUpdate);

        StepVerifier.create(result)
                .expectNextMatches(branch -> 
                        "branch1".equals(branch.getId()) &&
                        "branch 123".equals(branch.getName())
                )
                .verifyComplete();
    }

    @Test
    @DisplayName("Should preserve branch products after name update")
    void shouldPreserveBranchProductsAfterNameUpdate() {
        Branch branchUpdate = new Branch("branch1", "updated branch", null);
        
        when(franchiseRepositoryPort.findById("franchise1")).thenReturn(Mono.just(franchise));
        when(franchiseRepositoryPort.save(any(Franchise.class))).thenReturn(Mono.just(franchise));

        Mono<Branch> result = service.updateName("franchise1", branchUpdate);

        StepVerifier.create(result)
                .expectNextMatches(branch -> 
                        "branch1".equals(branch.getId()) &&
                        "updated branch".equals(branch.getName()) &&
                        branch.getProducts().size() == 1 &&
                        "product1".equals(branch.getProducts().getFirst().getId())
                )
                .verifyComplete();
    }
}