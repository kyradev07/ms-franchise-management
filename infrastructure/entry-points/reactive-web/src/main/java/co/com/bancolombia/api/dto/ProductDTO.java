package co.com.bancolombia.api.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public class ProductDTO {

    private String productId;

    @NotNull(message = "Stock is required")
    @PositiveOrZero(message = "Stock must be equals or greater than 0")
    private Integer stock;


}
