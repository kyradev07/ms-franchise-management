package co.com.bancolombia.service;

import co.com.bancolombia.model.Branch;
import co.com.bancolombia.model.Franchise;
import co.com.bancolombia.model.Product;
import co.com.bancolombia.model.gateway.FranchiseRepositoryPort;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetMaxStockByBranchInFranchiseServiceTest {

    @Mock
    private FranchiseRepositoryPort franchiseRepositoryPort;

    private GetMaxStockByBranchInFranchiseService service;
    private Franchise franchise;

    @BeforeEach
    void setUp() {
        service = new GetMaxStockByBranchInFranchiseService(franchiseRepositoryPort);
        
        // Branch 1 with products having different stock levels
        Product product1 = new Product("product1", "product 1", 10);
        Product product2 = new Product("product2", "product 2", 25);
        Product product3 = new Product("product3", "product 3", 15);
        Branch branch1 = new Branch("branch1", "branch 1", List.of(product1, product2, product3));
        
        // Branch 2 with products having different stock levels
        Product product4 = new Product("product4", "product 4", 30);
        Product product5 = new Product("product5", "product 5", 5);
        Branch branch2 = new Branch("branch2", "branch 2", List.of(product4, product5));
        
        // Branch 3 with single product
        Product product6 = new Product("product6", "product 6", 20);
        Branch branch3 = new Branch("branch3", "branch 3", List.of(product6));
        
        franchise = new Franchise("franchise1", "franchise 1", List.of(branch1, branch2, branch3));
    }

    @Test
    @DisplayName("Should get max stock product by branch successfully")
    void shouldGetMaxStockProductByBranchSuccessfully() {
        when(franchiseRepositoryPort.findById("franchise1")).thenReturn(Mono.just(franchise));

        Mono<Franchise> result = service.getMaxStockByBranchInFranchise("franchise1");

        StepVerifier.create(result)
                .expectNextMatches(resultFranchise -> {
                    assertEquals("franchise1", resultFranchise.getId());
                    assertEquals("franchise 1", resultFranchise.getName());
                    assertEquals(3, resultFranchise.getBranches().size());
                    
                    // Check branch 1 - should have product2 with stock 25
                    Branch resultBranch1 = resultFranchise.getBranches().get(0);
                    assertEquals("branch1", resultBranch1.getId());
                    assertEquals("branch 1", resultBranch1.getName());
                    assertEquals(1, resultBranch1.getProducts().size());
                    Product maxProduct1 = resultBranch1.getProducts().get(0);
                    assertEquals("product2", maxProduct1.getId());
                    assertEquals(25, maxProduct1.getStock());
                    
                    // Check branch 2 - should have product4 with stock 30
                    Branch resultBranch2 = resultFranchise.getBranches().get(1);
                    assertEquals("branch2", resultBranch2.getId());
                    assertEquals("branch 2", resultBranch2.getName());
                    assertEquals(1, resultBranch2.getProducts().size());
                    Product maxProduct2 = resultBranch2.getProducts().get(0);
                    assertEquals("product4", maxProduct2.getId());
                    assertEquals(30, maxProduct2.getStock());
                    
                    // Check branch 3 - should have product6 with stock 20
                    Branch resultBranch3 = resultFranchise.getBranches().get(2);
                    assertEquals("branch3", resultBranch3.getId());
                    assertEquals("branch 3", resultBranch3.getName());
                    assertEquals(1, resultBranch3.getProducts().size());
                    Product maxProduct3 = resultBranch3.getProducts().get(0);
                    assertEquals("product6", maxProduct3.getId());
                    assertEquals(20, maxProduct3.getStock());
                    
                    return true;
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Should handle branch with empty products list")
    void shouldHandleBranchWithEmptyProductsList() {
        Branch emptyBranch = new Branch("empty", "empty branch", new ArrayList<>());
        Franchise franchiseWithEmptyBranch = new Franchise("franchise2", "franchise 2", List.of(emptyBranch));
        
        when(franchiseRepositoryPort.findById("franchise2")).thenReturn(Mono.just(franchiseWithEmptyBranch));

        Mono<Franchise> result = service.getMaxStockByBranchInFranchise("franchise2");

        StepVerifier.create(result)
                .expectNextMatches(resultFranchise -> {
                    assertEquals(1, resultFranchise.getBranches().size());
                    Branch resultBranch = resultFranchise.getBranches().get(0);
                    assertEquals("empty", resultBranch.getId());
                    assertTrue(resultBranch.getProducts().isEmpty());
                    return true;
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Should handle franchise with no branches")
    void shouldHandleFranchiseWithNoBranches() {
        Franchise emptyFranchise = new Franchise("franchise3", "franchise 3", new ArrayList<>());
        
        when(franchiseRepositoryPort.findById("franchise3")).thenReturn(Mono.just(emptyFranchise));

        Mono<Franchise> result = service.getMaxStockByBranchInFranchise("franchise3");

        StepVerifier.create(result)
                .expectNextMatches(resultFranchise -> {
                    assertEquals("franchise3", resultFranchise.getId());
                    assertEquals("franchise 3", resultFranchise.getName());
                    assertTrue(resultFranchise.getBranches().isEmpty());
                    return true;
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Should handle products with same max stock")
    void shouldHandleProductsWithSameMaxStock() {
        Product product1 = new Product("product1", "product 1", 20);
        Product product2 = new Product("product2", "product 2", 20);
        Product product3 = new Product("product3", "product 3", 10);
        Branch branch = new Branch("branch1", "branch 1", List.of(product1, product2, product3));
        Franchise testFranchise = new Franchise("franchise4", "franchise 4", List.of(branch));
        
        when(franchiseRepositoryPort.findById("franchise4")).thenReturn(Mono.just(testFranchise));

        Mono<Franchise> result = service.getMaxStockByBranchInFranchise("franchise4");

        StepVerifier.create(result)
                .expectNextMatches(resultFranchise -> {
                    Branch resultBranch = resultFranchise.getBranches().get(0);
                    assertEquals(1, resultBranch.getProducts().size());
                    Product maxProduct = resultBranch.getProducts().get(0);
                    assertEquals(20, maxProduct.getStock());
                    // Should return the first product found with max stock
                    assertEquals("product1", maxProduct.getId());
                    return true;
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Should handle products with zero stock")
    void shouldHandleProductsWithZeroStock() {
        Product product1 = new Product("product1", "product 1", 0);
        Product product2 = new Product("product2", "product 2", 0);
        Branch branch = new Branch("branch1", "branch 1", List.of(product1, product2));
        Franchise testFranchise = new Franchise("franchise5", "franchise 5", List.of(branch));
        
        when(franchiseRepositoryPort.findById("franchise5")).thenReturn(Mono.just(testFranchise));

        Mono<Franchise> result = service.getMaxStockByBranchInFranchise("franchise5");

        StepVerifier.create(result)
                .expectNextMatches(resultFranchise -> {
                    Branch resultBranch = resultFranchise.getBranches().get(0);
                    assertEquals(1, resultBranch.getProducts().size());
                    Product maxProduct = resultBranch.getProducts().get(0);
                    assertEquals(0, maxProduct.getStock());
                    return true;
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Should handle single product in branch")
    void shouldHandleSingleProductInBranch() {
        Product singleProduct = new Product("single", "single product", 50);
        Branch singleProductBranch = new Branch("branch1", "branch 1", List.of(singleProduct));
        Franchise testFranchise = new Franchise("franchise6", "franchise 6", List.of(singleProductBranch));
        
        when(franchiseRepositoryPort.findById("franchise6")).thenReturn(Mono.just(testFranchise));

        Mono<Franchise> result = service.getMaxStockByBranchInFranchise("franchise6");

        StepVerifier.create(result)
                .expectNextMatches(resultFranchise -> {
                    Branch resultBranch = resultFranchise.getBranches().get(0);
                    assertEquals(1, resultBranch.getProducts().size());
                    Product maxProduct = resultBranch.getProducts().get(0);
                    assertEquals("single", maxProduct.getId());
                    assertEquals(50, maxProduct.getStock());
                    return true;
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Should handle franchise repository error")
    void shouldHandleFranchiseRepositoryError() {
        when(franchiseRepositoryPort.findById("franchise1"))
                .thenReturn(Mono.error(new RuntimeException("Database error")));

        Mono<Franchise> result = service.getMaxStockByBranchInFranchise("franchise1");

        StepVerifier.create(result)
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    @DisplayName("Should handle franchise not found")
    void shouldHandleFranchiseNotFound() {
        when(franchiseRepositoryPort.findById("nonexistent"))
                .thenReturn(Mono.empty());

        Mono<Franchise> result = service.getMaxStockByBranchInFranchise("nonexistent");

        StepVerifier.create(result)
                .verifyComplete();
    }

    @Test
    @DisplayName("Should handle products with negative stock")
    void shouldHandleProductsWithNegativeStock() {
        Product product1 = new Product("product1", "product 1", -5);
        Product product2 = new Product("product2", "product 2", -10);
        Product product3 = new Product("product3", "product 3", -2);
        Branch branch = new Branch("branch1", "branch 1", List.of(product1, product2, product3));
        Franchise testFranchise = new Franchise("franchise7", "franchise 7", List.of(branch));
        
        when(franchiseRepositoryPort.findById("franchise7")).thenReturn(Mono.just(testFranchise));

        Mono<Franchise> result = service.getMaxStockByBranchInFranchise("franchise7");

        StepVerifier.create(result)
                .expectNextMatches(resultFranchise -> {
                    Branch resultBranch = resultFranchise.getBranches().get(0);
                    assertEquals(1, resultBranch.getProducts().size());
                    Product maxProduct = resultBranch.getProducts().get(0);
                    assertEquals(-2, maxProduct.getStock()); // Highest among negative values
                    assertEquals("product3", maxProduct.getId());
                    return true;
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Should preserve branch and product names and IDs")
    void shouldPreserveBranchAndProductNamesAndIds() {
        when(franchiseRepositoryPort.findById("franchise1")).thenReturn(Mono.just(franchise));

        Mono<Franchise> result = service.getMaxStockByBranchInFranchise("franchise1");

        StepVerifier.create(result)
                .expectNextMatches(resultFranchise -> {
                    // Verify that all original branch information is preserved
                    for (int i = 0; i < resultFranchise.getBranches().size(); i++) {
                        Branch originalBranch = franchise.getBranches().get(i);
                        Branch resultBranch = resultFranchise.getBranches().get(i);
                        
                        assertEquals(originalBranch.getId(), resultBranch.getId());
                        assertEquals(originalBranch.getName(), resultBranch.getName());
                        
                        // Verify product information is preserved for max stock product
                        if (!resultBranch.getProducts().isEmpty()) {
                            Product maxProduct = resultBranch.getProducts().get(0);
                            assertNotNull(maxProduct.getId());
                            assertNotNull(maxProduct.getName());
                            assertTrue(maxProduct.getStock() != null);
                        }
                    }
                    return true;
                })
                .verifyComplete();
    }
}