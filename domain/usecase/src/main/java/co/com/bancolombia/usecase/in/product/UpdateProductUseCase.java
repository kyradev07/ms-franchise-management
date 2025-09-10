package co.com.bancolombia.usecase.in.product;

import co.com.bancolombia.model.Product;
import reactor.core.publisher.Mono;

public interface UpdateProductUseCase {
    Mono<Product> updateProduct(String franchiseId, String branchId, Product product);
}
