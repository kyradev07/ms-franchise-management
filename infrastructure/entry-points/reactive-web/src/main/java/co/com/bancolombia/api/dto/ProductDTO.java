package co.com.bancolombia.api.dto;

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

    private Integer stock;

}
