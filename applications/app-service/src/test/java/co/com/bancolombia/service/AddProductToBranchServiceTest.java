package co.com.bancolombia.service;

import co.com.bancolombia.model.Branch;
import co.com.bancolombia.model.Franchise;
import co.com.bancolombia.model.Product;
import co.com.bancolombia.model.gateway.FranchiseRepositoryPort;
import co.com.bancolombia.usecase.exceptions.BranchNotFoundException;
import co.com.bancolombia.usecase.exceptions.DuplicateProductException;
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
class AddProductToBranchServiceTest {

    @Mock
    private FranchiseRepositoryPort franchiseRepositoryPort;

    private AddProductToBranchService service;
    private Franchise franchise;
    private Branch branch;
    private Product existingProduct;

    @BeforeEach
    void setUp() {
        service = new AddProductToBranchService(franchiseRepositoryPort);
        
        existingProduct = new Product("product1", "existing product", 10);
        branch = new Branch("branch1", "branch 1", new ArrayList<>(List.of(existingProduct)));
        franchise = new Franchise("franchise1", "franchise 1", List.of(branch));
    }

    @Test
    @DisplayName("Should add product to branch successfully")
    void shouldAddProductToBranchSuccessfully() {
        Product newProduct = new Product(null, "new product", 15);
        
        when(franchiseRepositoryPort.findById("franchise1")).thenReturn(Mono.just(franchise));
        when(franchiseRepositoryPort.save(any(Franchise.class))).thenReturn(Mono.just(franchise));

        Mono<Product> result = service.addProductToBranch("franchise1", "branch1", newProduct);

        StepVerifier.create(result)
                .expectNextMatches(product -> 
                        product.getId() != null &&
                        "new product".equals(product.getName()) &&
                        product.getStock() == 15
                )
                .verifyComplete();
        
        assertEquals(2, branch.getProducts().size());
    }

    @Test
    @DisplayName("Should add product with zero stock")
    void shouldAddProductWithZeroStock() {
        Product newProduct = new Product(null, "zero stock product", 0);
        
        when(franchiseRepositoryPort.findById("franchise1")).thenReturn(Mono.just(franchise));
        when(franchiseRepositoryPort.save(any(Franchise.class))).thenReturn(Mono.just(franchise));

        Mono<Product> result = service.addProductToBranch("franchise1", "branch1", newProduct);

        StepVerifier.create(result)
                .expectNextMatches(product -> 
                        product.getId() != null &&
                        "zero stock product".equals(product.getName()) &&
                        product.getStock() == 0
                )
                .verifyComplete();
    }

    @Test
    @DisplayName("Should add product to empty branch")
    void shouldAddProductToEmptyBranch() {
        Branch emptyBranch = new Branch("branch2", "empty branch", new ArrayList<>());
        Franchise franchiseWithEmptyBranch = new Franchise("franchise1", "franchise 1", List.of(emptyBranch));
        Product newProduct = new Product(null, "first product", 5);
        
        when(franchiseRepositoryPort.findById("franchise1")).thenReturn(Mono.just(franchiseWithEmptyBranch));
        when(franchiseRepositoryPort.save(any(Franchise.class))).thenReturn(Mono.just(franchiseWithEmptyBranch));

        Mono<Product> result = service.addProductToBranch("franchise1", "branch2", newProduct);

        StepVerifier.create(result)
                .expectNextMatches(product -> 
                        product.getId() != null &&
                        "first product".equals(product.getName()) &&
                        product.getStock() == 5
                )
                .verifyComplete();
        
        assertEquals(1, emptyBranch.getProducts().size());
    }

    @Test
    @DisplayName("Should throw BranchNotFoundException when branch does not exist")
    void shouldThrowBranchNotFoundExceptionWhenBranchDoesNotExist() {
        Product newProduct = new Product(null, "new product", 15);
        
        when(franchiseRepositoryPort.findById("franchise1")).thenReturn(Mono.just(franchise));

        Mono<Product> result = service.addProductToBranch("franchise1", "nonexistent", newProduct);

        StepVerifier.create(result)
                .expectError(BranchNotFoundException.class)
                .verify();
    }

    @Test
    @DisplayName("Should throw DuplicateProductException when product name already exists")
    void shouldThrowDuplicateProductExceptionWhenProductNameAlreadyExists() {
        Product duplicateProduct = new Product(null, "existing product", 20);
        
        when(franchiseRepositoryPort.findById("franchise1")).thenReturn(Mono.just(franchise));

        Mono<Product> result = service.addProductToBranch("franchise1", "branch1", duplicateProduct);

        StepVerifier.create(result)
                .expectError(DuplicateProductException.class)
                .verify();
    }

    @Test
    @DisplayName("Should handle franchise repository error")
    void shouldHandleFranchiseRepositoryError() {
        Product newProduct = new Product(null, "new product", 15);
        
        when(franchiseRepositoryPort.findById("franchise1"))
                .thenReturn(Mono.error(new RuntimeException("Database error")));

        Mono<Product> result = service.addProductToBranch("franchise1", "branch1", newProduct);

        StepVerifier.create(result)
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    @DisplayName("Should handle save error")
    void shouldHandleSaveError() {
        Product newProduct = new Product(null, "new product", 15);
        
        when(franchiseRepositoryPort.findById("franchise1")).thenReturn(Mono.just(franchise));
        when(franchiseRepositoryPort.save(any(Franchise.class)))
                .thenReturn(Mono.error(new RuntimeException("Save error")));

        Mono<Product> result = service.addProductToBranch("franchise1", "branch1", newProduct);

        StepVerifier.create(result)
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    @DisplayName("Should handle franchise not found")
    void shouldHandleFranchiseNotFound() {
        Product newProduct = new Product(null, "new product", 15);
        
        when(franchiseRepositoryPort.findById("nonexistent"))
                .thenReturn(Mono.empty());

        Mono<Product> result = service.addProductToBranch("nonexistent", "branch1", newProduct);

        StepVerifier.create(result)
                .verifyComplete();
    }

    @Test
    @DisplayName("Should generate unique ID for new product")
    void shouldGenerateUniqueIdForNewProduct() {
        Product newProduct1 = new Product(null, "product 1", 10);
        Product newProduct2 = new Product(null, "product 2", 20);
        
        when(franchiseRepositoryPort.findById("franchise1")).thenReturn(Mono.just(franchise));
        when(franchiseRepositoryPort.save(any(Franchise.class))).thenReturn(Mono.just(franchise));

        Mono<Product> result1 = service.addProductToBranch("franchise1", "branch1", newProduct1);
        Mono<Product> result2 = service.addProductToBranch("franchise1", "branch1", newProduct2);

        StepVerifier.create(result1)
                .expectNextMatches(product -> product.getId() != null)
                .verifyComplete();

        StepVerifier.create(result2)
                .expectNextMatches(product -> 
                        product.getId() != null && 
                        !product.getId().equals(newProduct1.getId())
                )
                .verifyComplete();
    }

    @Test
    @DisplayName("Should add product with null stock")
    void shouldAddProductWithNullStock() {
        Product newProduct = new Product(null, "null stock product", null);
        
        when(franchiseRepositoryPort.findById("franchise1")).thenReturn(Mono.just(franchise));
        when(franchiseRepositoryPort.save(any(Franchise.class))).thenReturn(Mono.just(franchise));

        Mono<Product> result = service.addProductToBranch("franchise1", "branch1", newProduct);

        StepVerifier.create(result)
                .expectNextMatches(product -> 
                        product.getId() != null &&
                        "null stock product".equals(product.getName()) &&
                        product.getStock() == null
                )
                .verifyComplete();
    }
}