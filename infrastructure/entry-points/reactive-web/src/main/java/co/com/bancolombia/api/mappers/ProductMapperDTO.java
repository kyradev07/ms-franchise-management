package co.com.bancolombia.api.mappers;

import co.com.bancolombia.api.dto.ProductDTO;
import co.com.bancolombia.model.Product;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ProductMapperDTO {
    public static Product toDomain(ProductDTO productDTO) {
        log.debug("Converting ProductDTO to ProductModel");

        return new Product(null, productDTO.getName(), productDTO.getStock());
    }

    public static ProductDTO toDTO(Product product) {
        log.debug("Converting ProductModel to ProductDTO");
        return new ProductDTO(product.getId(), product.getName(), product.getStock());
    }
}
