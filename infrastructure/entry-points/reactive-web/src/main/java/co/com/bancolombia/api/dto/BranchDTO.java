package co.com.bancolombia.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BranchDTO {

    private String id;

    @NotNull(message = "Branch name is required")
    @NotBlank(message = "Branch must not be blank")
    private String name;

    private List<ProductDTO>  productStocksStockDTOS;

}
