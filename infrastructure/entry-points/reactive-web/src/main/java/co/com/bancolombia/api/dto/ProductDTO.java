package co.com.bancolombia.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public class ProductStockDTO {

    @NotNull(message = "ID Product is required")
    @NotBlank(message = "ID Product must not be blank")
    private String productId;

    @NotNull(message = "Stock is required")
    @PositiveOrZero(message = "Stock must be equals or greater than 0")
    private Integer stock;


}
