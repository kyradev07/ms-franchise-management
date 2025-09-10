package co.com.bancolombia.service.base;

import co.com.bancolombia.model.Branch;
import co.com.bancolombia.model.Franchise;
import co.com.bancolombia.model.Product;
import co.com.bancolombia.model.gateway.FranchiseRepositoryPort;
import co.com.bancolombia.usecase.exceptions.BranchNotFoundException;
import co.com.bancolombia.usecase.exceptions.DuplicateBranchException;
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

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BaseFranchiseServiceTest {

    @Mock
    private FranchiseRepositoryPort franchiseRepositoryPort;

    private TestBaseFranchiseService service;
    private Franchise franchise;
    private Branch branch;
    private Product product;

    @BeforeEach
    void setUp() {
        service = new TestBaseFranchiseService(franchiseRepositoryPort);
        
        product = new Product("product1", "product 1", 10);
        branch = new Branch("branch1", "branch 1", List.of(product));
        franchise = new Franchise("franchise1", "franchise 1", List.of(branch));
    }

    @Test
    @DisplayName("Should find branch successfully")
    void shouldFindBranchSuccessfully() {
        Branch result = service.findBranchOrThrow(franchise, "branch1");

        assertNotNull(result);
        assertEquals("branch1", result.getId());
        assertEquals("branch 1", result.getName());
    }

    @Test
    @DisplayName("Should throw BranchNotFoundException when branch not found")
    void shouldThrowBranchNotFoundExceptionWhenBranchNotFound() {
        assertThrows(BranchNotFoundException.class, 
            () -> service.findBranchOrThrow(franchise, "nonexistent"));
    }

    @Test
    @DisplayName("Should find product successfully")
    void shouldFindProductSuccessfully() {
        Product result = service.findProductOrThrow(branch, "product1");

        assertNotNull(result);
        assertEquals("product1", result.getId());
        assertEquals("product 1", result.getName());
    }

    @Test
    @DisplayName("Should throw ProductNotFoundException when product not found")
    void shouldThrowProductNotFoundExceptionWhenProductNotFound() {
        assertThrows(ProductNotFoundException.class, 
            () -> service.findProductOrThrow(branch, "nonexistent"));
    }

    @Test
    @DisplayName("Should validate branch name not duplicated successfully")
    void shouldValidateBranchNameNotDuplicatedSuccessfully() {
        assertDoesNotThrow(() -> service.validateBranchNameNotDuplicated(franchise, "new branch"));
    }

    @Test
    @DisplayName("Should throw DuplicateBranchException when branch name exists")
    void shouldThrowDuplicateBranchExceptionWhenBranchNameExists() {
        assertThrows(DuplicateBranchException.class, 
            () -> service.validateBranchNameNotDuplicated(franchise, "branch 1"));
    }

    @Test
    @DisplayName("Should validate product name not duplicated successfully")
    void shouldValidateProductNameNotDuplicatedSuccessfully() {
        assertDoesNotThrow(() -> service.validateProductNameNotDuplicated(branch, "new product"));
    }

    @Test
    @DisplayName("Should validate product name not duplicated when name is null")
    void shouldValidateProductNameNotDuplicatedWhenNameIsNull() {
        assertDoesNotThrow(() -> service.validateProductNameNotDuplicated(branch, null));
    }

    @Test
    @DisplayName("Should validate product name not duplicated when name is blank")
    void shouldValidateProductNameNotDuplicatedWhenNameIsBlank() {
        assertDoesNotThrow(() -> service.validateProductNameNotDuplicated(branch, ""));
    }

    @Test
    @DisplayName("Should throw DuplicateProductException when product name exists")
    void shouldThrowDuplicateProductExceptionWhenProductNameExists() {
        assertThrows(DuplicateProductException.class, 
            () -> service.validateProductNameNotDuplicated(branch, "product 1"));
    }

    @Test
    @DisplayName("Should save franchise and return value")
    void shouldSaveFranchiseAndReturnValue() {
        String returnValue = "test";
        when(franchiseRepositoryPort.save(franchise)).thenReturn(Mono.just(franchise));

        Mono<String> result = service.saveFranchiseAndReturn(franchise, returnValue);

        StepVerifier.create(result)
                .expectNext(returnValue)
                .verifyComplete();
    }

    @Test
    @DisplayName("Should save franchise")
    void shouldSaveFranchise() {
        when(franchiseRepositoryPort.save(franchise)).thenReturn(Mono.just(franchise));

        Mono<Void> result = service.saveFranchise(franchise);

        StepVerifier.create(result)
                .verifyComplete();
    }

    private static class TestBaseFranchiseService extends BaseFranchiseService {
        protected TestBaseFranchiseService(FranchiseRepositoryPort franchiseRepositoryPort) {
            super(franchiseRepositoryPort);
        }
    }
}