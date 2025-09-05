package co.com.bancolombia.api;

import co.com.bancolombia.api.router.BranchHandler;
import co.com.bancolombia.api.router.FranchiseHandler;
import co.com.bancolombia.api.router.ProductHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.path;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouterRest {

    @Bean
    public RouterFunction<ServerResponse> routerFunction(
            FranchiseHandler franchiseHandler,
            BranchHandler branchHandler,
            ProductHandler productHandler
    ) {
        return route()
                .nest(path("api/v1/franchise"), builder -> builder
                        .POST("", franchiseHandler::createFranchise)
                        .PUT("/{id}", franchiseHandler::updateFranchiseName)
                        .POST("/{franchiseId}/branch", branchHandler::addBranchToFranchise)
                        .PUT("/{franchiseId}/branch/{branchId}", branchHandler::updateBranchName)
                        .POST("/{franchiseId}/branch/{branchId}/product", productHandler::addProductToBranch)
                ).build();

    }
}
