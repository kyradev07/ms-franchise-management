package co.com.bancolombia.utils;

import co.com.bancolombia.model.Branch;
import co.com.bancolombia.model.Franchise;
import co.com.bancolombia.model.Product;

public class Filters {
    public static boolean filterBranchByName(Franchise franchise, String name) {
        return franchise.getBranches()
                .stream()
                .anyMatch(fBranch -> fBranch.getName().equals(name));
    }

    public static boolean filterProductByName(Branch branch, String name) {
        return branch.getProducts()
                .stream()
                .anyMatch(product -> product.getName().equals(name));
    }

    public static Branch filterBranchById(Franchise franchise, String branchId) {
        return franchise.getBranches()
                .stream()
                .filter(br -> br.getId().equals(branchId))
                .findFirst().orElse(null);
    }

    public static Branch findBranch(Franchise franchise, Branch branch) {
        return franchise.getBranches()
                .stream()
                .filter(br -> br.getId().equals(branch.getId()))
                .findFirst().orElse(null);
    }

    public static Product findProduct(Franchise franchise, String brId, String prId) {
        return franchise.getBranches()
                .stream()
                .filter(br -> br.getId().equals(brId))
                .flatMap(pr -> pr.getProducts().stream())
                .filter(pr -> pr.getId().equals(prId))
                .findFirst().orElse(null);
    }
}
