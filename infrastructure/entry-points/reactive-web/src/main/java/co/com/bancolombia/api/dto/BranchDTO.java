package co.com.bancolombia.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class BranchDTO {

    @NotNull(message = "Branch name is required")
    @NotBlank(message = "Branch must not be blank")
    private String name;

    private List<ProductDTO>  productStocksStockDTOS;

    public BranchDTO(String name, List<ProductDTO> productStocksStockDTOS) {
        this.name = name;
        this.productStocksStockDTOS = productStocksStockDTOS;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ProductDTO> getProductStocksStockDTOS() {
        return productStocksStockDTOS;
    }

    public void setProductStocksStockDTOS(List<ProductDTO> productStocksStockDTOS) {
        this.productStocksStockDTOS = productStocksStockDTOS;
    }
}
