package co.com.bancolombia.api.mappers;

import co.com.bancolombia.api.dto.MaxStockDTO;
import co.com.bancolombia.model.Franchise;

import java.util.List;

public class MaxStockMapper {

    public static List<MaxStockDTO> toMaxStockDTO(Franchise franchise) {
        return franchise.getBranches()
                .stream()
                .filter(branch -> !branch.getProducts().isEmpty())
                .map(branch -> new MaxStockDTO(branch.getId(), branch.getName(), ProductMapperDTO.toDTO(branch.getProducts().getFirst())))
                .toList();
    }
}
