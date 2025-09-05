package co.com.bancolombia.api.validations;

import co.com.bancolombia.usecase.exceptions.DuplicateBranchException;
import co.com.bancolombia.usecase.exceptions.DuplicateFranchiseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@Order(-2)
@RequiredArgsConstructor
public class GlobalErrorHandler implements ErrorWebExceptionHandler {

    private final ObjectMapper mapper;

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        if (exchange.getResponse().isCommitted()) return Mono.error(ex);

        HttpStatus status = toStatus(ex);
        Map<String, Object> body = Map.of(
                "path", exchange.getRequest().getPath().value(),
                "status", status.value(),
                "error", status.getReasonPhrase(),
                "message", toMessage(ex),
                "errors", (ex instanceof ConstraintViolationException cve)
                        ? cve.getConstraintViolations().stream()
                        .map(v -> Map.of("field", v.getPropertyPath().toString(), "error", v.getMessage()))
                        .toList()
                        : List.of()
        );

        byte[] bytes;
        try {
            bytes = mapper.writeValueAsBytes(body);
        } catch (Exception e) {
            bytes = ("{\"status\":" + status.value() + ",\"message\":\"" +
                    escapeJson(ex.getMessage()) + "\"}")
                    .getBytes(StandardCharsets.UTF_8);
        }

        var resp = exchange.getResponse();
        resp.setStatusCode(status);
        resp.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        return resp.writeWith(Mono.just(resp.bufferFactory().wrap(bytes)))
                .doOnTerminate(() -> log(ex, status));
    }

    private HttpStatus toStatus(Throwable ex) {
        if (ex instanceof ConstraintViolationException) return HttpStatus.BAD_REQUEST;
        if (ex instanceof MissingRequestBodyException) return HttpStatus.BAD_REQUEST;
        if (ex instanceof DuplicateFranchiseException) return HttpStatus.CONFLICT;
        if (ex instanceof DuplicateBranchException) return HttpStatus.CONFLICT;
        if (ex instanceof IllegalArgumentException) return HttpStatus.BAD_REQUEST;
        if (ex instanceof ResponseStatusException rse) return HttpStatus.valueOf(rse.getStatusCode().value());
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }

    private String toMessage(Throwable ex) {
        if (ex instanceof ConstraintViolationException cve) {
            return cve.getConstraintViolations().stream()
                    .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                    .reduce((a, b) -> a + "; " + b).orElse("Validation failed");
        }
        if (ex instanceof ResponseStatusException rse) {
            return rse.getReason() != null ? rse.getReason() : rse.getMessage();
        }
        return ex.getMessage() != null ? ex.getMessage() : ex.getClass().getSimpleName();
    }

    private void log(Throwable ex, HttpStatus status) {
        if (status.is5xxServerError()) log.error("Unhandled exception", ex);
        else log.warn("Handled exception: {}", toMessage(ex));
    }

    private static String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
