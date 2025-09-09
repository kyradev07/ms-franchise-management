package co.com.bancolombia.api.mappers;

import co.com.bancolombia.api.dto.ProductDTO;
import co.com.bancolombia.model.Product;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ProductMapperDTO {
    public static Product toDomain(ProductDTO productDTO) {
        log.debug("Converting ProductDTO to ProductModel");

        Integer INITIAL_STOCK = 0;
        return new Product(productDTO.getProductId(), productDTO.getName(), INITIAL_STOCK);
    }

    public static ProductDTO toDTO(Product product) {
        log.debug("Converting ProductModel to ProductDTO");
        return new ProductDTO(product.getId(), product.getName(), product.getStock());
    }
}
