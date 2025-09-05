package co.com.bancolombia.utils;

import co.com.bancolombia.model.Branch;
import co.com.bancolombia.model.Franchise;

public class Filters {
    public static boolean filterByName(Franchise franchise, String name) {
        return franchise.getBranches()
                .stream()
                .anyMatch(fBranch -> fBranch.getName().equals(name));
    }

    public static Branch findBranch(Franchise franchise, Branch branch) {
        return franchise.getBranches()
                .stream()
                .filter(br -> br.getId().equals(branch.getId()))
                .findFirst().orElse(branch);
    }
}
