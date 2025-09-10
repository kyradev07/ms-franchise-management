package co.com.bancolombia.api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTO {

    private String productId;

    @NotNull(message = "Product name is required")
    @NotBlank(message = "Product must not be blank")
    private String name;

    @NotNull(message = "Product stock is required")
    @Min(value = 0, message = "Product must be greater or equals to ZERO - 0")
    private Integer stock;

}
