package co.com.bancolombia.api;

import co.com.bancolombia.api.router.BranchHandler;
import co.com.bancolombia.api.router.FranchiseHandler;
import co.com.bancolombia.api.router.ProductHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RouterRestTest {

    @Mock
    private FranchiseHandler franchiseHandler;
    
    @Mock
    private BranchHandler branchHandler;
    
    @Mock
    private ProductHandler productHandler;

    private RouterRest routerRest;
    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        routerRest = new RouterRest();
        RouterFunction<ServerResponse> routerFunction = routerRest.routerFunction(
                franchiseHandler, branchHandler, productHandler);
        webTestClient = WebTestClient.bindToRouterFunction(routerFunction).build();
    }

    @Test
    @DisplayName("Should create router function with all handlers")
    void shouldCreateRouterFunctionWithAllHandlers() {
        RouterFunction<ServerResponse> routerFunction = routerRest.routerFunction(
                franchiseHandler, branchHandler, productHandler);

        assertNotNull(routerFunction);
    }

    @Test
    @DisplayName("Should route POST /api/v1/franchise to franchiseHandler createFranchise")
    void shouldRoutePOSTFranchiseToCreateFranchise() {
        when(franchiseHandler.createFranchise(any())).thenReturn(
                ServerResponse.ok().bodyValue("Created"));

        webTestClient.post()
                .uri("/api/v1/franchise")
                .bodyValue("{\"name\":\"Test Franchise\"}")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    @DisplayName("Should route PUT /api/v1/franchise/{id} to franchiseHandler updateFranchiseName")
    void shouldRoutePUTFranchiseToUpdateFranchiseName() {
        when(franchiseHandler.updateFranchiseName(any())).thenReturn(
                ServerResponse.ok().bodyValue("Updated"));

        webTestClient.put()
                .uri("/api/v1/franchise/franchise1")
                .bodyValue("{\"name\":\"Updated Franchise\"}")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    @DisplayName("Should route POST /api/v1/franchise/{franchiseId}/branch to branchHandler addBranchToFranchise")
    void shouldRoutePOSTBranchToAddBranchToFranchise() {
        when(branchHandler.addBranchToFranchise(any())).thenReturn(
                ServerResponse.ok().bodyValue("Created"));

        webTestClient.post()
                .uri("/api/v1/franchise/franchise1/branch")
                .bodyValue("{\"name\":\"Test Branch\"}")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    @DisplayName("Should route PUT /api/v1/franchise/{franchiseId}/branch/{branchId} to branchHandler updateBranchName")
    void shouldRoutePUTBranchToUpdateBranchName() {
        when(branchHandler.updateBranchName(any())).thenReturn(
                ServerResponse.ok().bodyValue("Updated"));

        webTestClient.put()
                .uri("/api/v1/franchise/franchise1/branch/branch1")
                .bodyValue("{\"name\":\"Updated Branch\"}")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    @DisplayName("Should route POST /api/v1/franchise/{franchiseId}/branch/{branchId}/product to productHandler addProductToBranch")
    void shouldRoutePOSTProductToAddProductToBranch() {
        when(productHandler.addProductToBranch(any())).thenReturn(
                ServerResponse.ok().bodyValue("Created"));

        webTestClient.post()
                .uri("/api/v1/franchise/franchise1/branch/branch1/product")
                .bodyValue("{\"name\":\"Test Product\",\"stock\":10}")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    @DisplayName("Should route DELETE /api/v1/franchise/{franchiseId}/branch/{branchId}/product/{productId} to productHandler deleteProductFromBranch")
    void shouldRouteDELETEProductToDeleteProductFromBranch() {
        when(productHandler.deleteProductFromBranch(any())).thenReturn(
                ServerResponse.noContent().build());

        webTestClient.delete()
                .uri("/api/v1/franchise/franchise1/branch/branch1/product/product1")
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    @DisplayName("Should route PUT /api/v1/franchise/{franchiseId}/branch/{branchId}/product/{productId} to productHandler updateProduct")
    void shouldRoutePUTProductToUpdateProduct() {
        when(productHandler.updateProduct(any())).thenReturn(
                ServerResponse.ok().bodyValue("Updated"));

        webTestClient.put()
                .uri("/api/v1/franchise/franchise1/branch/branch1/product/product1")
                .bodyValue("{\"name\":\"Updated Product\",\"stock\":20}")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    @DisplayName("Should route GET /api/v1/franchise/{franchiseId} to productHandler getMaxStock")
    void shouldRouteGETFranchiseToGetMaxStock() {
        when(productHandler.getMaxStock(any())).thenReturn(
                ServerResponse.ok().bodyValue("Max Stock Data"));

        webTestClient.get()
                .uri("/api/v1/franchise/franchise1")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    @DisplayName("Should return 404 for non-existing routes")
    void shouldReturn404ForNonExistingRoutes() {
        webTestClient.get()
                .uri("/api/v1/nonexistent")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @DisplayName("Should return 404 for wrong HTTP method")
    void shouldReturn404ForWrongHttpMethod() {
        webTestClient.patch()
                .uri("/api/v1/franchise/franchise1")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @DisplayName("Should handle nested path structure correctly")
    void shouldHandleNestedPathStructureCorrectly() {
        when(productHandler.addProductToBranch(any())).thenReturn(
                ServerResponse.ok().bodyValue("Created"));

        // Test deeply nested path
        webTestClient.post()
                .uri("/api/v1/franchise/fr123/branch/br456/product")
                .bodyValue("{\"name\":\"Nested Product\"}")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    @DisplayName("Should handle path variables in correct positions")
    void shouldHandlePathVariablesInCorrectPositions() {
        when(productHandler.updateProduct(any())).thenReturn(
                ServerResponse.ok().bodyValue("Updated"));

        // Test with multiple path variables
        webTestClient.put()
                .uri("/api/v1/franchise/franchise123/branch/branch456/product/product789")
                .bodyValue("{\"name\":\"Product with IDs\"}")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    @DisplayName("Should handle requests without base path prefix")
    void shouldHandleRequestsWithoutBasePathPrefix() {
        webTestClient.get()
                .uri("/franchise/franchise1")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @DisplayName("Should handle requests with wrong API version")
    void shouldHandleRequestsWithWrongApiVersion() {
        webTestClient.get()
                .uri("/api/v2/franchise/franchise1")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @DisplayName("Should handle empty path parameters")
    void shouldHandleEmptyPathParameters() {
        webTestClient.get()
                .uri("/api/v1/franchise/")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @DisplayName("Should handle special characters in path parameters")
    void shouldHandleSpecialCharactersInPathParameters() {
        when(productHandler.getMaxStock(any())).thenReturn(
                ServerResponse.ok().bodyValue("Max Stock Data"));

        webTestClient.get()
                .uri("/api/v1/franchise/franchise-123_test")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    @DisplayName("Should handle multiple consecutive slashes")
    void shouldHandleMultipleConsecutiveSlashes() {
        webTestClient.get()
                .uri("/api/v1//franchise//franchise1")
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Test
    @DisplayName("Should handle case sensitive paths")
    void shouldHandleCaseSensitivePaths() {
        webTestClient.get()
                .uri("/API/V1/FRANCHISE/franchise1")
                .exchange()
                .expectStatus().isNotFound();
    }
}
