package co.com.bancolombia.api.mappers;

import co.com.bancolombia.api.dto.BranchDTO;
import co.com.bancolombia.api.dto.ProductDTO;
import co.com.bancolombia.model.Branch;
import co.com.bancolombia.model.Product;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class BranchMapperDTO {

    public static Branch toDomain(BranchDTO branchDTO) {
        log.debug("Converting BranchDTO to BranchModel");

        List<Product> productDTOS = new ArrayList<>();

        return new Branch(branchDTO.getId(), branchDTO.getName().trim().toLowerCase(), productDTOS);

    }

    public static BranchDTO toDTO(Branch branch) {
        log.debug("Converting BranchModel to BranchDTO");

        List<ProductDTO> productDTOS = new ArrayList<>();

        return new BranchDTO(branch.getId(), branch.getName(), productDTOS);
    }
}
