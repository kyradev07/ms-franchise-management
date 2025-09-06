package co.com.bancolombia.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MaxStockDTO {
     private String branchId;
     private String branchName;
     private ProductDTO product;
}
