package co.com.bancolombia.usecase.in.product;

import co.com.bancolombia.model.Product;
import reactor.core.publisher.Mono;

public interface UpdateProductNameUseCase {
    Mono<Product> updateName(String name);
}
