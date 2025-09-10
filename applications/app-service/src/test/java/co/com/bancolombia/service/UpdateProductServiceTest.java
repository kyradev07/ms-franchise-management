package co.com.bancolombia.service;

import co.com.bancolombia.model.Branch;
import co.com.bancolombia.model.Franchise;
import co.com.bancolombia.model.Product;
import co.com.bancolombia.model.gateway.FranchiseRepositoryPort;
import co.com.bancolombia.usecase.exceptions.BranchNotFoundException;
import co.com.bancolombia.usecase.exceptions.DuplicateProductException;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateProductServiceTest {

    @Mock
    private FranchiseRepositoryPort franchiseRepositoryPort;

    private UpdateProductService service;
    private Franchise franchise;
    private Branch branch;
    private Product existingProduct;

    @BeforeEach
    void setUp() {
        service = new UpdateProductService(franchiseRepositoryPort);
        
        existingProduct = new Product("product1", "existing product", 10);
        branch = new Branch("branch1", "branch 1", new ArrayList<>(List.of(existingProduct)));
        franchise = new Franchise("franchise1", "franchise 1", List.of(branch));
    }

    @Test
    @DisplayName("Should update product name and stock successfully")
    void shouldUpdateProductNameAndStockSuccessfully() {
        Product updateData = new Product("product1", "updated product", 1);
        
        when(franchiseRepositoryPort.findById("franchise1")).thenReturn(Mono.just(franchise));
        when(franchiseRepositoryPort.save(any(Franchise.class))).thenReturn(Mono.just(franchise));

        Mono<Product> result = service.updateProduct("franchise1", "branch1", updateData);

        StepVerifier.create(result)
                .expectNextMatches(product -> 
                        "product1".equals(product.getId()) &&
                        "updated product".equals(product.getName()) &&
                        product.getStock() == 11
                )
                .verifyComplete();
    }

    @Test
    @DisplayName("Should update only stock when name is null")
    void shouldUpdateOnlyStockWhenNameIsNull() {
        Product updateData = new Product("product1", null, 5);
        
        when(franchiseRepositoryPort.findById("franchise1")).thenReturn(Mono.just(franchise));
        when(franchiseRepositoryPort.save(any(Franchise.class))).thenReturn(Mono.just(franchise));

        Mono<Product> result = service.updateProduct("franchise1", "branch1", updateData);

        StepVerifier.create(result)
                .expectNextMatches(product -> 
                        "existing product".equals(product.getName()) &&
                        product.getStock() == 15
                )
                .verifyComplete();
    }

    @Test
    @DisplayName("Should update only stock when name is blank")
    void shouldUpdateOnlyStockWhenNameIsBlank() {
        Product updateData = new Product("product1", "", 5);
        
        when(franchiseRepositoryPort.findById("franchise1")).thenReturn(Mono.just(franchise));
        when(franchiseRepositoryPort.save(any(Franchise.class))).thenReturn(Mono.just(franchise));

        Mono<Product> result = service.updateProduct("franchise1", "branch1", updateData);

        StepVerifier.create(result)
                .expectNextMatches(product -> 
                        "existing product".equals(product.getName()) &&
                        product.getStock() == 15
                )
                .verifyComplete();
    }

    @Test
    @DisplayName("Should update stock when increment is zero")
    void shouldUpdateStockWhenIncrementIsZero() {
        Product updateData = new Product("product1", "updated product", 0);
        
        when(franchiseRepositoryPort.findById("franchise1")).thenReturn(Mono.just(franchise));
        when(franchiseRepositoryPort.save(any(Franchise.class))).thenReturn(Mono.just(franchise));

        Mono<Product> result = service.updateProduct("franchise1", "branch1", updateData);

        StepVerifier.create(result)
                .expectNextMatches(product -> 
                        "product1".equals(product.getId()) &&
                        "updated product".equals(product.getName()) &&
                        product.getStock() == 10  // Should remain 10 (10 + 0)
                )
                .verifyComplete();
    }

    @Test
    @DisplayName("Should not update stock when negative value provided")
    void shouldNotUpdateStockWhenNegativeValueProvided() {
        Product updateData = new Product("product1", "updated product", -1);
        
        when(franchiseRepositoryPort.findById("franchise1")).thenReturn(Mono.just(franchise));
        when(franchiseRepositoryPort.save(any(Franchise.class))).thenReturn(Mono.just(franchise));

        Mono<Product> result = service.updateProduct("franchise1", "branch1", updateData);

        StepVerifier.create(result)
                .expectNextMatches(product -> 
                        "updated product".equals(product.getName()) &&
                        product.getStock() == 10
                )
                .verifyComplete();
    }

    @Test
    @DisplayName("Should not update stock when null value provided")
    void shouldNotUpdateStockWhenNullValueProvided() {
        Product updateData = new Product("product1", "updated product", null);
        
        when(franchiseRepositoryPort.findById("franchise1")).thenReturn(Mono.just(franchise));
        when(franchiseRepositoryPort.save(any(Franchise.class))).thenReturn(Mono.just(franchise));

        Mono<Product> result = service.updateProduct("franchise1", "branch1", updateData);

        StepVerifier.create(result)
                .expectNextMatches(product -> 
                        "updated product".equals(product.getName()) &&
                        product.getStock() == 10
                )
                .verifyComplete();
    }

    @Test
    @DisplayName("Should throw BranchNotFoundException when branch does not exist")
    void shouldThrowBranchNotFoundExceptionWhenBranchDoesNotExist() {
        Product updateData = new Product("product1", "updated product", 5);
        
        when(franchiseRepositoryPort.findById("franchise1")).thenReturn(Mono.just(franchise));

        Mono<Product> result = service.updateProduct("franchise1", "nonexistent", updateData);

        StepVerifier.create(result)
                .expectError(BranchNotFoundException.class)
                .verify();
    }

    @Test
    @DisplayName("Should throw ProductNotFoundException when product does not exist")
    void shouldThrowProductNotFoundExceptionWhenProductDoesNotExist() {
        Product updateData = new Product("nonexistent", "updated product", 5);
        
        when(franchiseRepositoryPort.findById("franchise1")).thenReturn(Mono.just(franchise));

        Mono<Product> result = service.updateProduct("franchise1", "branch1", updateData);

        StepVerifier.create(result)
                .expectError(ProductNotFoundException.class)
                .verify();
    }

    @Test
    @DisplayName("Should throw DuplicateProductException when product name already exists")
    void shouldThrowDuplicateProductExceptionWhenProductNameAlreadyExists() {
        Product anotherProduct = new Product("product2", "another product", 5);
        branch.getProducts().add(anotherProduct);
        
        Product updateData = new Product("product1", "another product", 5);
        
        when(franchiseRepositoryPort.findById("franchise1")).thenReturn(Mono.just(franchise));

        Mono<Product> result = service.updateProduct("franchise1", "branch1", updateData);

        StepVerifier.create(result)
                .expectError(DuplicateProductException.class)
                .verify();
    }

    @Test
    @DisplayName("Should handle franchise repository error")
    void shouldHandleFranchiseRepositoryError() {
        Product updateData = new Product("product1", "updated product", 5);
        
        when(franchiseRepositoryPort.findById("franchise1"))
                .thenReturn(Mono.error(new RuntimeException("Database error")));

        Mono<Product> result = service.updateProduct("franchise1", "branch1", updateData);

        StepVerifier.create(result)
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    @DisplayName("Should handle save error")
    void shouldHandleSaveError() {
        Product updateData = new Product("product1", "updated product", 5);
        
        when(franchiseRepositoryPort.findById("franchise1")).thenReturn(Mono.just(franchise));
        when(franchiseRepositoryPort.save(any(Franchise.class)))
                .thenReturn(Mono.error(new RuntimeException("Save error")));

        Mono<Product> result = service.updateProduct("franchise1", "branch1", updateData);

        StepVerifier.create(result)
                .expectError(RuntimeException.class)
                .verify();
    }
}