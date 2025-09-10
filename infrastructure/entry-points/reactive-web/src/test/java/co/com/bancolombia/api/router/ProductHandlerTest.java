package co.com.bancolombia.api.router;

import co.com.bancolombia.api.dto.ProductDTO;
import co.com.bancolombia.api.validations.FieldsValidator;
import co.com.bancolombia.api.validations.MissingRequestBodyException;
import co.com.bancolombia.model.Franchise;
import co.com.bancolombia.model.Product;
import co.com.bancolombia.usecase.exceptions.BranchNotFoundException;
import co.com.bancolombia.usecase.exceptions.DuplicateProductException;
import co.com.bancolombia.usecase.exceptions.ProductNotFoundException;
import co.com.bancolombia.usecase.in.product.AddProductToBranchUseCase;
import co.com.bancolombia.usecase.in.product.DeleteProductFromBranchUseCase;
import co.com.bancolombia.usecase.in.product.GetMaxStockByBranchInFranchiseUseCase;
import co.com.bancolombia.usecase.in.product.UpdateProductUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.reactive.function.server.MockServerRequest;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductHandlerTest {

    @Mock
    private AddProductToBranchUseCase addProductToBranchUseCase;

    @Mock
    private GetMaxStockByBranchInFranchiseUseCase getMaxStockByBranchInFranchiseUseCase;

    @Mock
    private DeleteProductFromBranchUseCase deleteProductFromBranchUseCase;

    @Mock
    private UpdateProductUseCase updateProductUseCase;

    @Mock
    private FieldsValidator fieldsValidator;

    private ProductHandler productHandler;
    private ProductDTO productDTO;
    private Product product;
    private Franchise franchise;

    @BeforeEach
    void setUp() {
        productHandler = new ProductHandler(
                addProductToBranchUseCase,
                getMaxStockByBranchInFranchiseUseCase,
                deleteProductFromBranchUseCase,
                updateProductUseCase,
                fieldsValidator
        );
        
        productDTO = new ProductDTO("p1", "Test Product", 10);
        product = new Product("product1", "Test Product", 10);
        franchise = new Franchise("franchise1", "Test Franchise", new ArrayList<>());
    }

    @Test
    @DisplayName("Should add product to branch successfully")
    void shouldAddProductToBranchSuccessfully() {
        ServerRequest request = MockServerRequest.builder()
                .pathVariable("franchiseId", "franchise1")
                .pathVariable("branchId", "branch1")
                .body(Mono.just(productDTO));

        when(fieldsValidator.validate(any(ProductDTO.class))).thenReturn(productDTO);
        when(addProductToBranchUseCase.addProductToBranch(anyString(), anyString(), any(Product.class)))
                .thenReturn(Mono.just(product));

        Mono<ServerResponse> response = productHandler.addProductToBranch(request);

        StepVerifier.create(response)
                .expectNextMatches(serverResponse -> 
                        serverResponse.statusCode() == HttpStatus.CREATED
                )
                .verifyComplete();

        verify(fieldsValidator).validate(productDTO);
        verify(addProductToBranchUseCase).addProductToBranch(eq("franchise1"), eq("branch1"), any(Product.class));
    }

    @Test
    @DisplayName("Should handle missing request body in add product")
    void shouldHandleMissingRequestBodyInAddProduct() {
        ServerRequest request = MockServerRequest.builder()
                .pathVariable("franchiseId", "franchise1")
                .pathVariable("branchId", "branch1")
                .body(Mono.empty());

        Mono<ServerResponse> response = productHandler.addProductToBranch(request);

        StepVerifier.create(response)
                .expectError(MissingRequestBodyException.class)
                .verify();

        verify(fieldsValidator, never()).validate(any());
        verify(addProductToBranchUseCase, never()).addProductToBranch(anyString(), anyString(), any());
    }

    @Test
    @DisplayName("Should handle validation error in add product")
    void shouldHandleValidationErrorInAddProduct() {
        ServerRequest request = MockServerRequest.builder()
                .pathVariable("franchiseId", "franchise1")
                .pathVariable("branchId", "branch1")
                .body(Mono.just(productDTO));

        when(fieldsValidator.validate(any(ProductDTO.class)))
                .thenThrow(new RuntimeException("Validation failed"));

        Mono<ServerResponse> response = productHandler.addProductToBranch(request);

        StepVerifier.create(response)
                .expectError(RuntimeException.class)
                .verify();

        verify(fieldsValidator).validate(productDTO);
        verify(addProductToBranchUseCase, never()).addProductToBranch(anyString(), anyString(), any());
    }

    @Test
    @DisplayName("Should handle duplicate product exception in add")
    void shouldHandleDuplicateProductExceptionInAdd() {
        ServerRequest request = MockServerRequest.builder()
                .pathVariable("franchiseId", "franchise1")
                .pathVariable("branchId", "branch1")
                .body(Mono.just(productDTO));

        when(fieldsValidator.validate(any(ProductDTO.class))).thenReturn(productDTO);
        when(addProductToBranchUseCase.addProductToBranch(anyString(), anyString(), any(Product.class)))
                .thenReturn(Mono.error(new DuplicateProductException("Product already exists", "Branch")));

        Mono<ServerResponse> response = productHandler.addProductToBranch(request);

        StepVerifier.create(response)
                .expectError(DuplicateProductException.class)
                .verify();

        verify(fieldsValidator).validate(productDTO);
        verify(addProductToBranchUseCase).addProductToBranch(eq("franchise1"), eq("branch1"), any(Product.class));
    }

    @Test
    @DisplayName("Should get max stock successfully")
    void shouldGetMaxStockSuccessfully() {
        ServerRequest request = MockServerRequest.builder()
                .pathVariable("franchiseId", "franchise1")
                .build();

        when(getMaxStockByBranchInFranchiseUseCase.getMaxStockByBranchInFranchise(anyString()))
                .thenReturn(Mono.just(franchise));

        Mono<ServerResponse> response = productHandler.getMaxStock(request);

        StepVerifier.create(response)
                .expectNextMatches(serverResponse -> 
                        serverResponse.statusCode() == HttpStatus.OK
                )
                .verifyComplete();

        verify(getMaxStockByBranchInFranchiseUseCase).getMaxStockByBranchInFranchise("franchise1");
    }

    @Test
    @DisplayName("Should handle use case error in get max stock")
    void shouldHandleUseCaseErrorInGetMaxStock() {
        ServerRequest request = MockServerRequest.builder()
                .pathVariable("franchiseId", "franchise1")
                .build();

        when(getMaxStockByBranchInFranchiseUseCase.getMaxStockByBranchInFranchise(anyString()))
                .thenReturn(Mono.error(new RuntimeException("Database error")));

        Mono<ServerResponse> response = productHandler.getMaxStock(request);

        StepVerifier.create(response)
                .expectError(RuntimeException.class)
                .verify();

        verify(getMaxStockByBranchInFranchiseUseCase).getMaxStockByBranchInFranchise("franchise1");
    }

    @Test
    @DisplayName("Should delete product from branch successfully")
    void shouldDeleteProductFromBranchSuccessfully() {
        ServerRequest request = MockServerRequest.builder()
                .pathVariable("franchiseId", "franchise1")
                .pathVariable("branchId", "branch1")
                .pathVariable("productId", "product1")
                .build();

        when(deleteProductFromBranchUseCase.deleteProductFromBranch(anyString(), anyString(), anyString()))
                .thenReturn(Mono.empty());

        Mono<ServerResponse> response = productHandler.deleteProductFromBranch(request);

        StepVerifier.create(response)
                .expectNextMatches(serverResponse -> 
                        serverResponse.statusCode() == HttpStatus.NO_CONTENT
                )
                .verifyComplete();

        verify(deleteProductFromBranchUseCase).deleteProductFromBranch("franchise1", "branch1", "product1");
    }

    @Test
    @DisplayName("Should handle product not found exception in delete")
    void shouldHandleProductNotFoundExceptionInDelete() {
        ServerRequest request = MockServerRequest.builder()
                .pathVariable("franchiseId", "franchise1")
                .pathVariable("branchId", "branch1")
                .pathVariable("productId", "product1")
                .build();

        when(deleteProductFromBranchUseCase.deleteProductFromBranch(anyString(), anyString(), anyString()))
                .thenReturn(Mono.error(new ProductNotFoundException("product1")));

        Mono<ServerResponse> response = productHandler.deleteProductFromBranch(request);

        StepVerifier.create(response)
                .expectError(ProductNotFoundException.class)
                .verify();

        verify(deleteProductFromBranchUseCase).deleteProductFromBranch("franchise1", "branch1", "product1");
    }

    @Test
    @DisplayName("Should handle branch not found exception in delete")
    void shouldHandleBranchNotFoundExceptionInDelete() {
        ServerRequest request = MockServerRequest.builder()
                .pathVariable("franchiseId", "franchise1")
                .pathVariable("branchId", "branch1")
                .pathVariable("productId", "product1")
                .build();

        when(deleteProductFromBranchUseCase.deleteProductFromBranch(anyString(), anyString(), anyString()))
                .thenReturn(Mono.error(new BranchNotFoundException("branch1")));

        Mono<ServerResponse> response = productHandler.deleteProductFromBranch(request);

        StepVerifier.create(response)
                .expectError(BranchNotFoundException.class)
                .verify();

        verify(deleteProductFromBranchUseCase).deleteProductFromBranch("franchise1", "branch1", "product1");
    }

    @Test
    @DisplayName("Should update product successfully")
    void shouldUpdateProductSuccessfully() {
        ServerRequest request = MockServerRequest.builder()
                .pathVariable("franchiseId", "franchise1")
                .pathVariable("branchId", "branch1")
                .pathVariable("productId", "product1")
                .body(Mono.just(productDTO));

        when(updateProductUseCase.updateProduct(anyString(), anyString(), any(Product.class)))
                .thenReturn(Mono.just(product));

        Mono<ServerResponse> response = productHandler.updateProduct(request);

        StepVerifier.create(response)
                .expectNextMatches(serverResponse -> 
                        serverResponse.statusCode() == HttpStatus.CREATED
                )
                .verifyComplete();

        verify(updateProductUseCase).updateProduct(eq("franchise1"), eq("branch1"), argThat(p -> 
                "product1".equals(p.getId())
        ));
    }

    @Test
    @DisplayName("Should set product id from path variable in update")
    void shouldSetProductIdFromPathVariableInUpdate() {
        ServerRequest request = MockServerRequest.builder()
                .pathVariable("franchiseId", "franchise1")
                .pathVariable("branchId", "branch1")
                .pathVariable("productId", "product123")
                .body(Mono.just(productDTO));

        when(updateProductUseCase.updateProduct(anyString(), anyString(), any(Product.class)))
                .thenReturn(Mono.just(product));

        Mono<ServerResponse> response = productHandler.updateProduct(request);

        StepVerifier.create(response)
                .expectNextMatches(serverResponse -> 
                        serverResponse.statusCode() == HttpStatus.CREATED
                )
                .verifyComplete();

        verify(updateProductUseCase).updateProduct(eq("franchise1"), eq("branch1"), argThat(p -> 
                "product123".equals(p.getId())
        ));
    }

    @Test
    @DisplayName("Should handle missing request body in update product")
    void shouldHandleMissingRequestBodyInUpdateProduct() {
        ServerRequest request = MockServerRequest.builder()
                .pathVariable("franchiseId", "franchise1")
                .pathVariable("branchId", "branch1")
                .pathVariable("productId", "product1")
                .body(Mono.empty());

        Mono<ServerResponse> response = productHandler.updateProduct(request);

        StepVerifier.create(response)
                .expectError(MissingRequestBodyException.class)
                .verify();

        verify(updateProductUseCase, never()).updateProduct(anyString(), anyString(), any());
    }

    @Test
    @DisplayName("Should handle product not found exception in update")
    void shouldHandleProductNotFoundExceptionInUpdate() {
        ServerRequest request = MockServerRequest.builder()
                .pathVariable("franchiseId", "franchise1")
                .pathVariable("branchId", "branch1")
                .pathVariable("productId", "product1")
                .body(Mono.just(productDTO));

        when(updateProductUseCase.updateProduct(anyString(), anyString(), any(Product.class)))
                .thenReturn(Mono.error(new ProductNotFoundException("product1")));

        Mono<ServerResponse> response = productHandler.updateProduct(request);

        StepVerifier.create(response)
                .expectError(ProductNotFoundException.class)
                .verify();

        verify(updateProductUseCase).updateProduct(eq("franchise1"), eq("branch1"), any(Product.class));
    }

    @Test
    @DisplayName("Should extract path variables correctly in delete")
    void shouldExtractPathVariablesCorrectlyInDelete() {
        ServerRequest request = MockServerRequest.builder()
                .pathVariable("franchiseId", "special-franchise-123")
                .pathVariable("branchId", "special-branch-456")
                .pathVariable("productId", "special-product-789")
                .build();

        when(deleteProductFromBranchUseCase.deleteProductFromBranch(anyString(), anyString(), anyString()))
                .thenReturn(Mono.empty());

        Mono<ServerResponse> response = productHandler.deleteProductFromBranch(request);

        StepVerifier.create(response)
                .expectNextMatches(serverResponse -> 
                        serverResponse.statusCode() == HttpStatus.NO_CONTENT
                )
                .verifyComplete();

        verify(deleteProductFromBranchUseCase).deleteProductFromBranch(
                "special-franchise-123", 
                "special-branch-456", 
                "special-product-789"
        );
    }

    @Test
    @DisplayName("Should extract path variables correctly in add product")
    void shouldExtractPathVariablesCorrectlyInAddProduct() {
        ServerRequest request = MockServerRequest.builder()
                .pathVariable("franchiseId", "franchise-abc")
                .pathVariable("branchId", "branch-def")
                .body(Mono.just(productDTO));

        when(fieldsValidator.validate(any(ProductDTO.class))).thenReturn(productDTO);
        when(addProductToBranchUseCase.addProductToBranch(anyString(), anyString(), any(Product.class)))
                .thenReturn(Mono.just(product));

        Mono<ServerResponse> response = productHandler.addProductToBranch(request);

        StepVerifier.create(response)
                .expectNextMatches(serverResponse -> 
                        serverResponse.statusCode() == HttpStatus.CREATED
                )
                .verifyComplete();

        verify(addProductToBranchUseCase).addProductToBranch(
                eq("franchise-abc"), 
                eq("branch-def"), 
                any(Product.class)
        );
    }

    @Test
    @DisplayName("Should handle use case error in add product")
    void shouldHandleUseCaseErrorInAddProduct() {
        ServerRequest request = MockServerRequest.builder()
                .pathVariable("franchiseId", "franchise1")
                .pathVariable("branchId", "branch1")
                .body(Mono.just(productDTO));

        when(fieldsValidator.validate(any(ProductDTO.class))).thenReturn(productDTO);
        when(addProductToBranchUseCase.addProductToBranch(anyString(), anyString(), any(Product.class)))
                .thenReturn(Mono.error(new RuntimeException("Database error")));

        Mono<ServerResponse> response = productHandler.addProductToBranch(request);

        StepVerifier.create(response)
                .expectError(RuntimeException.class)
                .verify();

        verify(fieldsValidator).validate(productDTO);
        verify(addProductToBranchUseCase).addProductToBranch(eq("franchise1"), eq("branch1"), any(Product.class));
    }

    @Test
    @DisplayName("Should handle use case error in update product")
    void shouldHandleUseCaseErrorInUpdateProduct() {
        ServerRequest request = MockServerRequest.builder()
                .pathVariable("franchiseId", "franchise1")
                .pathVariable("branchId", "branch1")
                .pathVariable("productId", "product1")
                .body(Mono.just(productDTO));

        when(updateProductUseCase.updateProduct(anyString(), anyString(), any(Product.class)))
                .thenReturn(Mono.error(new RuntimeException("Database error")));

        Mono<ServerResponse> response = productHandler.updateProduct(request);

        StepVerifier.create(response)
                .expectError(RuntimeException.class)
                .verify();

        verify(updateProductUseCase).updateProduct(eq("franchise1"), eq("branch1"), any(Product.class));
    }

    @Test
    @DisplayName("Should handle null product DTO in add product")
    void shouldHandleNullProductDTOInAddProduct() {
        ServerRequest request = MockServerRequest.builder()
                .pathVariable("franchiseId", "franchise1")
                .pathVariable("branchId", "branch1")
                .body(Mono.empty());

        Mono<ServerResponse> response = productHandler.addProductToBranch(request);

        StepVerifier.create(response)
                .expectError(MissingRequestBodyException.class)
                .verify();
    }

    @Test
    @DisplayName("Should handle null product DTO in update product")
    void shouldHandleNullProductDTOInUpdateProduct() {
        ServerRequest request = MockServerRequest.builder()
                .pathVariable("franchiseId", "franchise1")
                .pathVariable("branchId", "branch1")
                .pathVariable("productId", "product1")
                .body(Mono.empty());

        Mono<ServerResponse> response = productHandler.updateProduct(request);

        StepVerifier.create(response)
                .expectError(MissingRequestBodyException.class)
                .verify();
    }

    @Test
    @DisplayName("Should handle zero stock product in add")
    void shouldHandleZeroStockProductInAdd() {
        ProductDTO zeroStockDTO = new ProductDTO("p1", "Zero Stock Product", 0);
        ServerRequest request = MockServerRequest.builder()
                .pathVariable("franchiseId", "franchise1")
                .pathVariable("branchId", "branch1")
                .body(Mono.just(zeroStockDTO));

        when(fieldsValidator.validate(any(ProductDTO.class))).thenReturn(zeroStockDTO);
        when(addProductToBranchUseCase.addProductToBranch(anyString(), anyString(), any(Product.class)))
                .thenReturn(Mono.just(product));

        Mono<ServerResponse> response = productHandler.addProductToBranch(request);

        StepVerifier.create(response)
                .expectNextMatches(serverResponse -> 
                        serverResponse.statusCode() == HttpStatus.CREATED
                )
                .verifyComplete();

        verify(fieldsValidator).validate(zeroStockDTO);
        verify(addProductToBranchUseCase).addProductToBranch(eq("franchise1"), eq("branch1"), any(Product.class));
    }
}