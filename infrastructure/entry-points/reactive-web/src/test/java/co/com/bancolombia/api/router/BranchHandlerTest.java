package co.com.bancolombia.api.router;

import co.com.bancolombia.api.dto.BranchDTO;
import co.com.bancolombia.api.validations.FieldsValidator;
import co.com.bancolombia.api.validations.MissingRequestBodyException;
import co.com.bancolombia.model.Branch;
import co.com.bancolombia.usecase.exceptions.BranchNotFoundException;
import co.com.bancolombia.usecase.exceptions.DuplicateBranchException;
import co.com.bancolombia.usecase.in.branch.AddBranchToFranchiseUseCase;
import co.com.bancolombia.usecase.in.branch.UpdateBranchNameUseCase;
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
class BranchHandlerTest {

    @Mock
    private AddBranchToFranchiseUseCase addBranchToFranchiseUseCase;

    @Mock
    private UpdateBranchNameUseCase updateBranchNameUseCase;

    @Mock
    private FieldsValidator fieldsValidator;

    private BranchHandler branchHandler;
    private BranchDTO branchDTO;
    private Branch branch;

    @BeforeEach
    void setUp() {
        branchHandler = new BranchHandler(
                addBranchToFranchiseUseCase,
                updateBranchNameUseCase,
                fieldsValidator
        );
        
        branchDTO = new BranchDTO(null, "Test Branch", new ArrayList<>());
        branch = new Branch("branch1", "Test Branch", new ArrayList<>());
    }

    @Test
    @DisplayName("Should add branch to franchise successfully")
    void shouldAddBranchToFranchiseSuccessfully() {
        ServerRequest request = MockServerRequest.builder()
                .pathVariable("franchiseId", "franchise1")
                .body(Mono.just(branchDTO));

        when(fieldsValidator.validate(any(BranchDTO.class))).thenReturn(branchDTO);
        when(addBranchToFranchiseUseCase.addBranchToFranchise(anyString(), any(Branch.class)))
                .thenReturn(Mono.just(branch));

        Mono<ServerResponse> response = branchHandler.addBranchToFranchise(request);

        StepVerifier.create(response)
                .expectNextMatches(serverResponse -> 
                        serverResponse.statusCode() == HttpStatus.CREATED
                )
                .verifyComplete();

        verify(fieldsValidator).validate(branchDTO);
        verify(addBranchToFranchiseUseCase).addBranchToFranchise(eq("franchise1"), any(Branch.class));
    }

    @Test
    @DisplayName("Should handle missing request body in add branch")
    void shouldHandleMissingRequestBodyInAddBranch() {
        ServerRequest request = MockServerRequest.builder()
                .pathVariable("franchiseId", "franchise1")
                .body(Mono.empty());

        Mono<ServerResponse> response = branchHandler.addBranchToFranchise(request);

        StepVerifier.create(response)
                .expectError(MissingRequestBodyException.class)
                .verify();

        verify(fieldsValidator, never()).validate(any());
        verify(addBranchToFranchiseUseCase, never()).addBranchToFranchise(anyString(), any());
    }

    @Test
    @DisplayName("Should handle validation error in add branch")
    void shouldHandleValidationErrorInAddBranch() {
        ServerRequest request = MockServerRequest.builder()
                .pathVariable("franchiseId", "franchise1")
                .body(Mono.just(branchDTO));

        when(fieldsValidator.validate(any(BranchDTO.class)))
                .thenThrow(new RuntimeException("Validation failed"));

        Mono<ServerResponse> response = branchHandler.addBranchToFranchise(request);

        StepVerifier.create(response)
                .expectError(RuntimeException.class)
                .verify();

        verify(fieldsValidator).validate(branchDTO);
        verify(addBranchToFranchiseUseCase, never()).addBranchToFranchise(anyString(), any());
    }

    @Test
    @DisplayName("Should handle duplicate branch exception in add")
    void shouldHandleDuplicateBranchExceptionInAdd() {
        ServerRequest request = MockServerRequest.builder()
                .pathVariable("franchiseId", "franchise1")
                .body(Mono.just(branchDTO));

        when(fieldsValidator.validate(any(BranchDTO.class))).thenReturn(branchDTO);
        when(addBranchToFranchiseUseCase.addBranchToFranchise(anyString(), any(Branch.class)))
                .thenReturn(Mono.error(new DuplicateBranchException("Branch already exists", "Franchise")));

        Mono<ServerResponse> response = branchHandler.addBranchToFranchise(request);

        StepVerifier.create(response)
                .expectError(DuplicateBranchException.class)
                .verify();

        verify(fieldsValidator).validate(branchDTO);
        verify(addBranchToFranchiseUseCase).addBranchToFranchise(eq("franchise1"), any(Branch.class));
    }

    @Test
    @DisplayName("Should update branch name successfully")
    void shouldUpdateBranchNameSuccessfully() {
        ServerRequest request = MockServerRequest.builder()
                .pathVariable("franchiseId", "franchise1")
                .pathVariable("branchId", "branch1")
                .body(Mono.just(branchDTO));

        when(fieldsValidator.validate(any(BranchDTO.class))).thenReturn(branchDTO);
        when(updateBranchNameUseCase.updateName(anyString(), any(Branch.class)))
                .thenReturn(Mono.just(branch));

        Mono<ServerResponse> response = branchHandler.updateBranchName(request);

        StepVerifier.create(response)
                .expectNextMatches(serverResponse -> 
                        serverResponse.statusCode() == HttpStatus.CREATED
                )
                .verifyComplete();

        verify(fieldsValidator).validate(branchDTO);
        verify(updateBranchNameUseCase).updateName(eq("franchise1"), any(Branch.class));
    }

    @Test
    @DisplayName("Should set branch id from path variable in update")
    void shouldSetBranchIdFromPathVariableInUpdate() {
        ServerRequest request = MockServerRequest.builder()
                .pathVariable("franchiseId", "franchise1")
                .pathVariable("branchId", "branch123")
                .body(Mono.just(branchDTO));

        when(fieldsValidator.validate(any(BranchDTO.class))).thenReturn(branchDTO);
        when(updateBranchNameUseCase.updateName(anyString(), any(Branch.class)))
                .thenReturn(Mono.just(branch));

        Mono<ServerResponse> response = branchHandler.updateBranchName(request);

        StepVerifier.create(response)
                .expectNextMatches(serverResponse -> 
                        serverResponse.statusCode() == HttpStatus.CREATED
                )
                .verifyComplete();

        // Verify that the branch id is set correctly from path variable
        verify(updateBranchNameUseCase).updateName(eq("franchise1"), argThat(b -> 
                "branch123".equals(b.getId())
        ));
    }

    @Test
    @DisplayName("Should handle missing request body in update branch name")
    void shouldHandleMissingRequestBodyInUpdateBranchName() {
        ServerRequest request = MockServerRequest.builder()
                .pathVariable("franchiseId", "franchise1")
                .pathVariable("branchId", "branch1")
                .body(Mono.empty());

        Mono<ServerResponse> response = branchHandler.updateBranchName(request);

        StepVerifier.create(response)
                .expectError(MissingRequestBodyException.class)
                .verify();

        verify(fieldsValidator, never()).validate(any());
        verify(updateBranchNameUseCase, never()).updateName(anyString(), any());
    }

    @Test
    @DisplayName("Should handle validation error in update branch name")
    void shouldHandleValidationErrorInUpdateBranchName() {
        ServerRequest request = MockServerRequest.builder()
                .pathVariable("franchiseId", "franchise1")
                .pathVariable("branchId", "branch1")
                .body(Mono.just(branchDTO));

        when(fieldsValidator.validate(any(BranchDTO.class)))
                .thenThrow(new RuntimeException("Validation failed"));

        Mono<ServerResponse> response = branchHandler.updateBranchName(request);

        StepVerifier.create(response)
                .expectError(RuntimeException.class)
                .verify();

        verify(fieldsValidator).validate(branchDTO);
        verify(updateBranchNameUseCase, never()).updateName(anyString(), any());
    }

    @Test
    @DisplayName("Should handle branch not found exception in update")
    void shouldHandleBranchNotFoundExceptionInUpdate() {
        ServerRequest request = MockServerRequest.builder()
                .pathVariable("franchiseId", "franchise1")
                .pathVariable("branchId", "branch1")
                .body(Mono.just(branchDTO));

        when(fieldsValidator.validate(any(BranchDTO.class))).thenReturn(branchDTO);
        when(updateBranchNameUseCase.updateName(anyString(), any(Branch.class)))
                .thenReturn(Mono.error(new BranchNotFoundException("branch1")));

        Mono<ServerResponse> response = branchHandler.updateBranchName(request);

        StepVerifier.create(response)
                .expectError(BranchNotFoundException.class)
                .verify();

        verify(fieldsValidator).validate(branchDTO);
        verify(updateBranchNameUseCase).updateName(eq("franchise1"), any(Branch.class));
    }

    @Test
    @DisplayName("Should handle duplicate branch exception in update")
    void shouldHandleDuplicateBranchExceptionInUpdate() {
        ServerRequest request = MockServerRequest.builder()
                .pathVariable("franchiseId", "franchise1")
                .pathVariable("branchId", "branch1")
                .body(Mono.just(branchDTO));

        when(fieldsValidator.validate(any(BranchDTO.class))).thenReturn(branchDTO);
        when(updateBranchNameUseCase.updateName(anyString(), any(Branch.class)))
                .thenReturn(Mono.error(new DuplicateBranchException("Branch name already exists", "Franchise")));

        Mono<ServerResponse> response = branchHandler.updateBranchName(request);

        StepVerifier.create(response)
                .expectError(DuplicateBranchException.class)
                .verify();

        verify(fieldsValidator).validate(branchDTO);
        verify(updateBranchNameUseCase).updateName(eq("franchise1"), any(Branch.class));
    }

    @Test
    @DisplayName("Should extract path variables correctly")
    void shouldExtractPathVariablesCorrectly() {
        ServerRequest request = MockServerRequest.builder()
                .pathVariable("franchiseId", "special-franchise-123")
                .pathVariable("branchId", "special-branch-456")
                .body(Mono.just(branchDTO));

        when(fieldsValidator.validate(any(BranchDTO.class))).thenReturn(branchDTO);
        when(updateBranchNameUseCase.updateName(anyString(), any(Branch.class)))
                .thenReturn(Mono.just(branch));

        Mono<ServerResponse> response = branchHandler.updateBranchName(request);

        StepVerifier.create(response)
                .expectNextMatches(serverResponse -> 
                        serverResponse.statusCode() == HttpStatus.CREATED
                )
                .verifyComplete();

        verify(updateBranchNameUseCase).updateName(eq("special-franchise-123"), argThat(b -> 
                "special-branch-456".equals(b.getId())
        ));
    }

    @Test
    @DisplayName("Should handle use case error in add branch")
    void shouldHandleUseCaseErrorInAddBranch() {
        ServerRequest request = MockServerRequest.builder()
                .pathVariable("franchiseId", "franchise1")
                .body(Mono.just(branchDTO));

        when(fieldsValidator.validate(any(BranchDTO.class))).thenReturn(branchDTO);
        when(addBranchToFranchiseUseCase.addBranchToFranchise(anyString(), any(Branch.class)))
                .thenReturn(Mono.error(new RuntimeException("Database error")));

        Mono<ServerResponse> response = branchHandler.addBranchToFranchise(request);

        StepVerifier.create(response)
                .expectError(RuntimeException.class)
                .verify();

        verify(fieldsValidator).validate(branchDTO);
        verify(addBranchToFranchiseUseCase).addBranchToFranchise(eq("franchise1"), any(Branch.class));
    }

    @Test
    @DisplayName("Should handle use case error in update branch name")
    void shouldHandleUseCaseErrorInUpdateBranchName() {
        ServerRequest request = MockServerRequest.builder()
                .pathVariable("franchiseId", "franchise1")
                .pathVariable("branchId", "branch1")
                .body(Mono.just(branchDTO));

        when(fieldsValidator.validate(any(BranchDTO.class))).thenReturn(branchDTO);
        when(updateBranchNameUseCase.updateName(anyString(), any(Branch.class)))
                .thenReturn(Mono.error(new RuntimeException("Database error")));

        Mono<ServerResponse> response = branchHandler.updateBranchName(request);

        StepVerifier.create(response)
                .expectError(RuntimeException.class)
                .verify();

        verify(fieldsValidator).validate(branchDTO);
        verify(updateBranchNameUseCase).updateName(eq("franchise1"), any(Branch.class));
    }

    @Test
    @DisplayName("Should handle empty branch name in add")
    void shouldHandleEmptyBranchNameInAdd() {
        BranchDTO emptyNameDTO = new BranchDTO(null, "", new ArrayList<>());
        ServerRequest request = MockServerRequest.builder()
                .pathVariable("franchiseId", "franchise1")
                .body(Mono.just(emptyNameDTO));

        when(fieldsValidator.validate(any(BranchDTO.class))).thenReturn(emptyNameDTO);
        when(addBranchToFranchiseUseCase.addBranchToFranchise(anyString(), any(Branch.class)))
                .thenReturn(Mono.just(branch));

        Mono<ServerResponse> response = branchHandler.addBranchToFranchise(request);

        StepVerifier.create(response)
                .expectNextMatches(serverResponse -> 
                        serverResponse.statusCode() == HttpStatus.CREATED
                )
                .verifyComplete();

        verify(fieldsValidator).validate(emptyNameDTO);
        verify(addBranchToFranchiseUseCase).addBranchToFranchise(eq("franchise1"), any(Branch.class));
    }
}