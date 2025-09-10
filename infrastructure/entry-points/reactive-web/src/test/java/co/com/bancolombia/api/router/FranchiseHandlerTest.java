package co.com.bancolombia.api.router;

import co.com.bancolombia.api.dto.FranchiseDTO;
import co.com.bancolombia.api.validations.FieldsValidator;
import co.com.bancolombia.api.validations.MissingRequestBodyException;
import co.com.bancolombia.model.Franchise;
import co.com.bancolombia.usecase.exceptions.DuplicateFranchiseException;
import co.com.bancolombia.usecase.in.franchise.CreateFranchiseUseCase;
import co.com.bancolombia.usecase.in.franchise.UpdateFranchiseNameUseCase;
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
class FranchiseHandlerTest {

    @Mock
    private CreateFranchiseUseCase createFranchiseUseCase;

    @Mock
    private UpdateFranchiseNameUseCase updateFranchiseNameUseCase;

    @Mock
    private FieldsValidator fieldsValidator;

    private FranchiseHandler franchiseHandler;
    private FranchiseDTO franchiseDTO;
    private Franchise franchise;

    @BeforeEach
    void setUp() {
        franchiseHandler = new FranchiseHandler(
                createFranchiseUseCase, 
                updateFranchiseNameUseCase, 
                fieldsValidator
        );
        
        franchiseDTO = new FranchiseDTO("Test Franchise", "");
        franchise = new Franchise("franchise1", "Test Franchise", new ArrayList<>());
    }

    @Test
    @DisplayName("Should create franchise successfully")
    void shouldCreateFranchiseSuccessfully() {
        ServerRequest request = MockServerRequest.builder()
                .body(Mono.just(franchiseDTO));

        when(fieldsValidator.validate(any(FranchiseDTO.class))).thenReturn(franchiseDTO);
        when(createFranchiseUseCase.create(any(Franchise.class))).thenReturn(Mono.just(franchise));

        Mono<ServerResponse> response = franchiseHandler.createFranchise(request);

        StepVerifier.create(response)
                .expectNextMatches(serverResponse -> 
                        serverResponse.statusCode() == HttpStatus.CREATED
                )
                .verifyComplete();

        verify(fieldsValidator).validate(franchiseDTO);
        verify(createFranchiseUseCase).create(any(Franchise.class));
    }

    @Test
    @DisplayName("Should handle missing request body in create franchise")
    void shouldHandleMissingRequestBodyInCreateFranchise() {
        ServerRequest request = MockServerRequest.builder()
                .body(Mono.empty());

        Mono<ServerResponse> response = franchiseHandler.createFranchise(request);

        StepVerifier.create(response)
                .expectError(MissingRequestBodyException.class)
                .verify();

        verify(fieldsValidator, never()).validate(any());
        verify(createFranchiseUseCase, never()).create(any());
    }

    @Test
    @DisplayName("Should handle validation error in create franchise")
    void shouldHandleValidationErrorInCreateFranchise() {
        ServerRequest request = MockServerRequest.builder()
                .body(Mono.just(franchiseDTO));

        when(fieldsValidator.validate(any(FranchiseDTO.class)))
                .thenThrow(new RuntimeException("Validation failed"));

        Mono<ServerResponse> response = franchiseHandler.createFranchise(request);

        StepVerifier.create(response)
                .expectError(RuntimeException.class)
                .verify();

        verify(fieldsValidator).validate(franchiseDTO);
        verify(createFranchiseUseCase, never()).create(any());
    }

    @Test
    @DisplayName("Should handle duplicate franchise exception in create")
    void shouldHandleDuplicateFranchiseExceptionInCreate() {
        ServerRequest request = MockServerRequest.builder()
                .body(Mono.just(franchiseDTO));

        when(fieldsValidator.validate(any(FranchiseDTO.class))).thenReturn(franchiseDTO);
        when(createFranchiseUseCase.create(any(Franchise.class)))
                .thenReturn(Mono.error(new DuplicateFranchiseException("Franchise already exists")));

        Mono<ServerResponse> response = franchiseHandler.createFranchise(request);

        StepVerifier.create(response)
                .expectError(DuplicateFranchiseException.class)
                .verify();

        verify(fieldsValidator).validate(franchiseDTO);
        verify(createFranchiseUseCase).create(any(Franchise.class));
    }

    @Test
    @DisplayName("Should handle use case error in create franchise")
    void shouldHandleUseCaseErrorInCreateFranchise() {
        ServerRequest request = MockServerRequest.builder()
                .body(Mono.just(franchiseDTO));

        when(fieldsValidator.validate(any(FranchiseDTO.class))).thenReturn(franchiseDTO);
        when(createFranchiseUseCase.create(any(Franchise.class)))
                .thenReturn(Mono.error(new RuntimeException("Database error")));

        Mono<ServerResponse> response = franchiseHandler.createFranchise(request);

        StepVerifier.create(response)
                .expectError(RuntimeException.class)
                .verify();

        verify(fieldsValidator).validate(franchiseDTO);
        verify(createFranchiseUseCase).create(any(Franchise.class));
    }

    @Test
    @DisplayName("Should update franchise name successfully")
    void shouldUpdateFranchiseNameSuccessfully() {
        ServerRequest request = MockServerRequest.builder()
                .pathVariable("id", "franchise1")
                .body(Mono.just(franchiseDTO));

        when(fieldsValidator.validate(any(FranchiseDTO.class))).thenReturn(franchiseDTO);
        when(updateFranchiseNameUseCase.updateName(anyString(), any(Franchise.class)))
                .thenReturn(Mono.just(franchise));

        Mono<ServerResponse> response = franchiseHandler.updateFranchiseName(request);

        StepVerifier.create(response)
                .expectNextMatches(serverResponse -> 
                        serverResponse.statusCode() == HttpStatus.CREATED
                )
                .verifyComplete();

        verify(fieldsValidator).validate(franchiseDTO);
        verify(updateFranchiseNameUseCase).updateName(eq("franchise1"), any(Franchise.class));
    }

    @Test
    @DisplayName("Should handle missing request body in update franchise name")
    void shouldHandleMissingRequestBodyInUpdateFranchiseName() {
        ServerRequest request = MockServerRequest.builder()
                .pathVariable("id", "franchise1")
                .body(Mono.empty());

        Mono<ServerResponse> response = franchiseHandler.updateFranchiseName(request);

        StepVerifier.create(response)
                .expectError(MissingRequestBodyException.class)
                .verify();

        verify(fieldsValidator, never()).validate(any());
        verify(updateFranchiseNameUseCase, never()).updateName(anyString(), any());
    }

    @Test
    @DisplayName("Should handle validation error in update franchise name")
    void shouldHandleValidationErrorInUpdateFranchiseName() {
        ServerRequest request = MockServerRequest.builder()
                .pathVariable("id", "franchise1")
                .body(Mono.just(franchiseDTO));

        when(fieldsValidator.validate(any(FranchiseDTO.class)))
                .thenThrow(new RuntimeException("Validation failed"));

        Mono<ServerResponse> response = franchiseHandler.updateFranchiseName(request);

        StepVerifier.create(response)
                .expectError(RuntimeException.class)
                .verify();

        verify(fieldsValidator).validate(franchiseDTO);
        verify(updateFranchiseNameUseCase, never()).updateName(anyString(), any());
    }

    @Test
    @DisplayName("Should handle duplicate franchise exception in update")
    void shouldHandleDuplicateFranchiseExceptionInUpdate() {
        ServerRequest request = MockServerRequest.builder()
                .pathVariable("id", "franchise1")
                .body(Mono.just(franchiseDTO));

        when(fieldsValidator.validate(any(FranchiseDTO.class))).thenReturn(franchiseDTO);
        when(updateFranchiseNameUseCase.updateName(anyString(), any(Franchise.class)))
                .thenReturn(Mono.error(new DuplicateFranchiseException("Franchise name already exists")));

        Mono<ServerResponse> response = franchiseHandler.updateFranchiseName(request);

        StepVerifier.create(response)
                .expectError(DuplicateFranchiseException.class)
                .verify();

        verify(fieldsValidator).validate(franchiseDTO);
        verify(updateFranchiseNameUseCase).updateName(eq("franchise1"), any(Franchise.class));
    }

    @Test
    @DisplayName("Should handle use case error in update franchise name")
    void shouldHandleUseCaseErrorInUpdateFranchiseName() {
        ServerRequest request = MockServerRequest.builder()
                .pathVariable("id", "franchise1")
                .body(Mono.just(franchiseDTO));

        when(fieldsValidator.validate(any(FranchiseDTO.class))).thenReturn(franchiseDTO);
        when(updateFranchiseNameUseCase.updateName(anyString(), any(Franchise.class)))
                .thenReturn(Mono.error(new RuntimeException("Database error")));

        Mono<ServerResponse> response = franchiseHandler.updateFranchiseName(request);

        StepVerifier.create(response)
                .expectError(RuntimeException.class)
                .verify();

        verify(fieldsValidator).validate(franchiseDTO);
        verify(updateFranchiseNameUseCase).updateName(eq("franchise1"), any(Franchise.class));
    }

    @Test
    @DisplayName("Should extract path variable correctly in update")
    void shouldExtractPathVariableCorrectlyInUpdate() {
        ServerRequest request = MockServerRequest.builder()
                .pathVariable("id", "special-franchise-123")
                .body(Mono.just(franchiseDTO));

        when(fieldsValidator.validate(any(FranchiseDTO.class))).thenReturn(franchiseDTO);
        when(updateFranchiseNameUseCase.updateName(anyString(), any(Franchise.class)))
                .thenReturn(Mono.just(franchise));

        Mono<ServerResponse> response = franchiseHandler.updateFranchiseName(request);

        StepVerifier.create(response)
                .expectNextMatches(serverResponse -> 
                        serverResponse.statusCode() == HttpStatus.CREATED
                )
                .verifyComplete();

        verify(updateFranchiseNameUseCase).updateName(eq("special-franchise-123"), any(Franchise.class));
    }

    @Test
    @DisplayName("Should handle null franchise DTO in create")
    void shouldHandleNullFranchiseDTOInCreate() {
        ServerRequest request = MockServerRequest.builder()
                .body(Mono.empty());

        Mono<ServerResponse> response = franchiseHandler.createFranchise(request);

        StepVerifier.create(response)
                .expectError(MissingRequestBodyException.class)
                .verify();
    }

    @Test
    @DisplayName("Should handle null franchise DTO in update")
    void shouldHandleNullFranchiseDTOInUpdate() {
        ServerRequest request = MockServerRequest.builder()
                .pathVariable("id", "franchise1")
                .body(Mono.empty());

        Mono<ServerResponse> response = franchiseHandler.updateFranchiseName(request);

        StepVerifier.create(response)
                .expectError(MissingRequestBodyException.class)
                .verify();
    }

    @Test
    @DisplayName("Should handle empty franchise name in create")
    void shouldHandleEmptyFranchiseNameInCreate() {
        FranchiseDTO emptyNameDTO = new FranchiseDTO("", "");
        ServerRequest request = MockServerRequest.builder()
                .body(Mono.just(emptyNameDTO));

        when(fieldsValidator.validate(any(FranchiseDTO.class))).thenReturn(emptyNameDTO);
        when(createFranchiseUseCase.create(any(Franchise.class))).thenReturn(Mono.just(franchise));

        Mono<ServerResponse> response = franchiseHandler.createFranchise(request);

        StepVerifier.create(response)
                .expectNextMatches(serverResponse -> 
                        serverResponse.statusCode() == HttpStatus.CREATED
                )
                .verifyComplete();

        verify(fieldsValidator).validate(emptyNameDTO);
        verify(createFranchiseUseCase).create(any(Franchise.class));
    }
}