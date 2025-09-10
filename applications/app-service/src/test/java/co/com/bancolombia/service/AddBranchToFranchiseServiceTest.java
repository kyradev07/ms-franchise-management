package co.com.bancolombia.service;

import co.com.bancolombia.model.Branch;
import co.com.bancolombia.model.Franchise;
import co.com.bancolombia.model.Product;
import co.com.bancolombia.model.gateway.FranchiseRepositoryPort;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AddBranchToFranchiseServiceTest {

    @Mock
    private FranchiseRepositoryPort franchiseRepositoryPort;

    private AddBranchToFranchiseService service;
    private Franchise franchise;
    private Branch existingBranch;

    @BeforeEach
    void setUp() {
        service = new AddBranchToFranchiseService(franchiseRepositoryPort);
        
        Product product = new Product("product1", "product 1", 10);
        existingBranch = new Branch("branch1", "existing branch", List.of(product));
        
        List<Branch> branches = new ArrayList<>();
        branches.add(existingBranch);
        
        franchise = new Franchise("franchise1", "franchise 1", branches);
    }

    @Test
    @DisplayName("Should add branch to franchise successfully")
    void shouldAddBranchToFranchiseSuccessfully() {
        Product product = new Product("product2", "product 2", 20);
        Branch newBranch = new Branch(null, "new branch", List.of(product));
        
        when(franchiseRepositoryPort.findById("franchise1")).thenReturn(Mono.just(franchise));
        when(franchiseRepositoryPort.save(any(Franchise.class))).thenReturn(Mono.just(franchise));

        Mono<Branch> result = service.addBranchToFranchise("franchise1", newBranch);

        StepVerifier.create(result)
                .expectNextMatches(branch -> 
                        branch.getId() != null &&
                        "new branch".equals(branch.getName()) &&
                        branch.getProducts().size() == 1
                )
                .verifyComplete();
        
        assertEquals(2, franchise.getBranches().size());
    }

    @Test
    @DisplayName("Should add branch with empty products list")
    void shouldAddBranchWithEmptyProductsList() {
        Branch newBranch = new Branch(null, "empty branch", new ArrayList<>());
        
        when(franchiseRepositoryPort.findById("franchise1")).thenReturn(Mono.just(franchise));
        when(franchiseRepositoryPort.save(any(Franchise.class))).thenReturn(Mono.just(franchise));

        Mono<Branch> result = service.addBranchToFranchise("franchise1", newBranch);

        StepVerifier.create(result)
                .expectNextMatches(branch -> 
                        branch.getId() != null &&
                        "empty branch".equals(branch.getName()) &&
                        branch.getProducts().isEmpty()
                )
                .verifyComplete();
        
        assertEquals(2, franchise.getBranches().size());
    }

    @Test
    @DisplayName("Should add branch with null products")
    void shouldAddBranchWithNullProducts() {
        Branch newBranch = new Branch(null, "null products branch", null);
        
        when(franchiseRepositoryPort.findById("franchise1")).thenReturn(Mono.just(franchise));
        when(franchiseRepositoryPort.save(any(Franchise.class))).thenReturn(Mono.just(franchise));

        Mono<Branch> result = service.addBranchToFranchise("franchise1", newBranch);

        StepVerifier.create(result)
                .expectNextMatches(branch -> 
                        branch.getId() != null &&
                        "null products branch".equals(branch.getName()) &&
                        branch.getProducts() == null
                )
                .verifyComplete();
        
        assertEquals(2, franchise.getBranches().size());
    }

    @Test
    @DisplayName("Should add branch to franchise with no existing branches")
    void shouldAddBranchToFranchiseWithNoExistingBranches() {
        Franchise emptyFranchise = new Franchise("franchise2", "empty franchise", new ArrayList<>());
        Branch newBranch = new Branch(null, "first branch", new ArrayList<>());
        
        when(franchiseRepositoryPort.findById("franchise2")).thenReturn(Mono.just(emptyFranchise));
        when(franchiseRepositoryPort.save(any(Franchise.class))).thenReturn(Mono.just(emptyFranchise));

        Mono<Branch> result = service.addBranchToFranchise("franchise2", newBranch);

        StepVerifier.create(result)
                .expectNextMatches(branch -> 
                        branch.getId() != null &&
                        "first branch".equals(branch.getName())
                )
                .verifyComplete();
        
        assertEquals(1, emptyFranchise.getBranches().size());
    }

    @Test
    @DisplayName("Should throw DuplicateBranchException when branch name already exists")
    void shouldThrowDuplicateBranchExceptionWhenBranchNameAlreadyExists() {
        Branch duplicateBranch = new Branch(null, "existing branch", new ArrayList<>());
        
        when(franchiseRepositoryPort.findById("franchise1")).thenReturn(Mono.just(franchise));

        Mono<Branch> result = service.addBranchToFranchise("franchise1", duplicateBranch);

        StepVerifier.create(result)
                .expectError(DuplicateBranchException.class)
                .verify();
    }

    @Test
    @DisplayName("Should handle franchise repository error")
    void shouldHandleFranchiseRepositoryError() {
        Branch newBranch = new Branch(null, "new branch", new ArrayList<>());
        
        when(franchiseRepositoryPort.findById("franchise1"))
                .thenReturn(Mono.error(new RuntimeException("Database error")));

        Mono<Branch> result = service.addBranchToFranchise("franchise1", newBranch);

        StepVerifier.create(result)
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    @DisplayName("Should handle save error")
    void shouldHandleSaveError() {
        Branch newBranch = new Branch(null, "new branch", new ArrayList<>());
        
        when(franchiseRepositoryPort.findById("franchise1")).thenReturn(Mono.just(franchise));
        when(franchiseRepositoryPort.save(any(Franchise.class)))
                .thenReturn(Mono.error(new RuntimeException("Save error")));

        Mono<Branch> result = service.addBranchToFranchise("franchise1", newBranch);

        StepVerifier.create(result)
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    @DisplayName("Should handle franchise not found")
    void shouldHandleFranchiseNotFound() {
        Branch newBranch = new Branch(null, "new branch", new ArrayList<>());
        
        when(franchiseRepositoryPort.findById("nonexistent"))
                .thenReturn(Mono.empty());

        Mono<Branch> result = service.addBranchToFranchise("nonexistent", newBranch);

        StepVerifier.create(result)
                .verifyComplete();
    }

    @Test
    @DisplayName("Should generate unique ID for new branch")
    void shouldGenerateUniqueIdForNewBranch() {
        Branch newBranch1 = new Branch(null, "branch 1", new ArrayList<>());
        Branch newBranch2 = new Branch(null, "branch 2", new ArrayList<>());
        
        when(franchiseRepositoryPort.findById("franchise1")).thenReturn(Mono.just(franchise));
        when(franchiseRepositoryPort.save(any(Franchise.class))).thenReturn(Mono.just(franchise));

        Mono<Branch> result1 = service.addBranchToFranchise("franchise1", newBranch1);
        Mono<Branch> result2 = service.addBranchToFranchise("franchise1", newBranch2);

        StepVerifier.create(result1)
                .expectNextMatches(branch -> branch.getId() != null)
                .verifyComplete();

        StepVerifier.create(result2)
                .expectNextMatches(branch -> 
                        branch.getId() != null && 
                        !branch.getId().equals(newBranch1.getId())
                )
                .verifyComplete();
    }

    @Test
    @DisplayName("Should add branch with special characters in name")
    void shouldAddBranchWithSpecialCharactersInName() {
        Branch newBranch = new Branch(null, "branch-name_with@special#chars", new ArrayList<>());
        
        when(franchiseRepositoryPort.findById("franchise1")).thenReturn(Mono.just(franchise));
        when(franchiseRepositoryPort.save(any(Franchise.class))).thenReturn(Mono.just(franchise));

        Mono<Branch> result = service.addBranchToFranchise("franchise1", newBranch);

        StepVerifier.create(result)
                .expectNextMatches(branch -> 
                        branch.getId() != null &&
                        "branch-name_with@special#chars".equals(branch.getName())
                )
                .verifyComplete();
    }

    @Test
    @DisplayName("Should add multiple branches with different products")
    void shouldAddMultipleBranchesWithDifferentProducts() {
        Product product1 = new Product("p1", "product 1", 10);
        Product product2 = new Product("p2", "product 2", 20);
        
        Branch branch1 = new Branch(null, "branch a", List.of(product1));
        Branch branch2 = new Branch(null, "branch b", List.of(product2));
        
        when(franchiseRepositoryPort.findById("franchise1")).thenReturn(Mono.just(franchise));
        when(franchiseRepositoryPort.save(any(Franchise.class))).thenReturn(Mono.just(franchise));

        Mono<Branch> result1 = service.addBranchToFranchise("franchise1", branch1);
        Mono<Branch> result2 = service.addBranchToFranchise("franchise1", branch2);

        StepVerifier.create(result1)
                .expectNextMatches(branch -> 
                        "branch a".equals(branch.getName()) &&
                        branch.getProducts().size() == 1
                )
                .verifyComplete();

        StepVerifier.create(result2)
                .expectNextMatches(branch -> 
                        "branch b".equals(branch.getName()) &&
                        branch.getProducts().size() == 1
                )
                .verifyComplete();
    }
}