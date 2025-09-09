package co.com.bancolombia.api.mappers;

import co.com.bancolombia.api.dto.FranchiseDTO;
import co.com.bancolombia.model.Branch;
import co.com.bancolombia.model.Franchise;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class FranchisMapperDTO {

    public static Franchise toDomain(FranchiseDTO franchiseDTO) {
        log.debug("Converting FranchiseDTO to FranchiseModel");

        List<Branch> branches = new ArrayList<>();

        return new Franchise(franchiseDTO.getId(), franchiseDTO.getName().trim().toLowerCase(), branches);
    }

    public static FranchiseDTO toDTO(Franchise franchise) {
        return new FranchiseDTO(franchise.getId(), franchise.getName());
    }
}
