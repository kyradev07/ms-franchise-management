package co.com.bancolombia.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class FranchiseDTO {

    @NotNull(message = "Franchise name is required")
    @NotBlank(message = "Franchise must not be blank")
    private String name;


    public FranchiseDTO() {

    }

    public FranchiseDTO(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
