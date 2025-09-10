package co.com.bancolombia.service;

import co.com.bancolombia.model.Branch;
import co.com.bancolombia.model.Franchise;
import co.com.bancolombia.model.Product;
import co.com.bancolombia.model.gateway.FranchiseRepositoryPort;
import co.com.bancolombia.usecase.exceptions.BranchNotFoundException;
import co.com.bancolombia.usecase.exceptions.ProductNotFoundException;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeleteProductFromBranchServiceTest {

    @Mock
    private FranchiseRepositoryPort franchiseRepositoryPort;

    private DeleteProductFromBranchService service;
    private Franchise franchise;
    private Branch branch;
    private Product product1;
    private Product product2;

    @BeforeEach
    void setUp() {
        service = new DeleteProductFromBranchService(franchiseRepositoryPort);
        
        product1 = new Product("product1", "product 1", 10);
        product2 = new Product("product2", "product 2", 20);
        
        List<Product> products = new ArrayList<>();
        products.add(product1);
        products.add(product2);
        
        branch = new Branch("branch1", "branch 1", products);
        franchise = new Franchise("franchise1", "franchise 1", List.of(branch));
    }

    @Test
    @DisplayName("Should delete product from branch successfully")
    void shouldDeleteProductFromBranchSuccessfully() {
        when(franchiseRepositoryPort.findById("franchise1")).thenReturn(Mono.just(franchise));
        when(franchiseRepositoryPort.save(any(Franchise.class))).thenReturn(Mono.just(franchise));

        Mono<Void> result = service.deleteProductFromBranch("franchise1", "branch1", "product1");

        StepVerifier.create(result)
                .verifyComplete();
        
        assertEquals(1, branch.getProducts().size());
        assertEquals("product2", branch.getProducts().get(0).getId());
    }

    @Test
    @DisplayName("Should delete last product from branch successfully")
    void shouldDeleteLastProductFromBranchSuccessfully() {
        Product singleProduct = new Product("product1", "product 1", 10);
        Branch singleProductBranch = new Branch("branch1", "branch 1", new ArrayList<>(List.of(singleProduct)));
        Franchise singleProductFranchise = new Franchise("franchise1", "franchise 1", List.of(singleProductBranch));
        
        when(franchiseRepositoryPort.findById("franchise1")).thenReturn(Mono.just(singleProductFranchise));
        when(franchiseRepositoryPort.save(any(Franchise.class))).thenReturn(Mono.just(singleProductFranchise));

        Mono<Void> result = service.deleteProductFromBranch("franchise1", "branch1", "product1");

        StepVerifier.create(result)
                .verifyComplete();
        
        assertEquals(0, singleProductBranch.getProducts().size());
    }

    @Test
    @DisplayName("Should throw BranchNotFoundException when branch does not exist")
    void shouldThrowBranchNotFoundExceptionWhenBranchDoesNotExist() {
        when(franchiseRepositoryPort.findById("franchise1")).thenReturn(Mono.just(franchise));

        Mono<Void> result = service.deleteProductFromBranch("franchise1", "nonexistent", "product1");

        StepVerifier.create(result)
                .expectError(BranchNotFoundException.class)
                .verify();
    }

    @Test
    @DisplayName("Should throw ProductNotFoundException when product does not exist")
    void shouldThrowProductNotFoundExceptionWhenProductDoesNotExist() {
        when(franchiseRepositoryPort.findById("franchise1")).thenReturn(Mono.just(franchise));

        Mono<Void> result = service.deleteProductFromBranch("franchise1", "branch1", "nonexistent");

        StepVerifier.create(result)
                .expectError(ProductNotFoundException.class)
                .verify();
    }

    @Test
    @DisplayName("Should handle franchise repository error")
    void shouldHandleFranchiseRepositoryError() {
        when(franchiseRepositoryPort.findById("franchise1"))
                .thenReturn(Mono.error(new RuntimeException("Database error")));

        Mono<Void> result = service.deleteProductFromBranch("franchise1", "branch1", "product1");

        StepVerifier.create(result)
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    @DisplayName("Should handle save error")
    void shouldHandleSaveError() {
        when(franchiseRepositoryPort.findById("franchise1")).thenReturn(Mono.just(franchise));
        when(franchiseRepositoryPort.save(any(Franchise.class)))
                .thenReturn(Mono.error(new RuntimeException("Save error")));

        Mono<Void> result = service.deleteProductFromBranch("franchise1", "branch1", "product1");

        StepVerifier.create(result)
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    @DisplayName("Should handle franchise not found error")
    void shouldHandleFranchiseNotFoundError() {
        when(franchiseRepositoryPort.findById("nonexistent"))
                .thenReturn(Mono.empty());

        Mono<Void> result = service.deleteProductFromBranch("nonexistent", "branch1", "product1");

        StepVerifier.create(result)
                .verifyComplete();
    }

    @Test
    @DisplayName("Should delete correct product when multiple products exist")
    void shouldDeleteCorrectProductWhenMultipleProductsExist() {
        Product product3 = new Product("product3", "product 3", 30);
        branch.getProducts().add(product3);
        
        when(franchiseRepositoryPort.findById("franchise1")).thenReturn(Mono.just(franchise));
        when(franchiseRepositoryPort.save(any(Franchise.class))).thenReturn(Mono.just(franchise));

        Mono<Void> result = service.deleteProductFromBranch("franchise1", "branch1", "product2");

        StepVerifier.create(result)
                .verifyComplete();
        
        assertEquals(2, branch.getProducts().size());
        assertEquals("product1", branch.getProducts().get(0).getId());
        assertEquals("product3", branch.getProducts().get(1).getId());
    }
}