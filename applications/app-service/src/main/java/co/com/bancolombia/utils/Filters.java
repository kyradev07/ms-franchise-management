package co.com.bancolombia.utils;

import co.com.bancolombia.model.Franchise;

public class Filters {
    public static boolean filterByName(Franchise franchise, String name) {
        return franchise.getBranches()
                .stream()
                .anyMatch(fBranch -> fBranch.getName().equals(name));
    }
}
