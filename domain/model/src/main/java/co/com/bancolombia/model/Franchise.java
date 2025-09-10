package co.com.bancolombia.model;

import java.util.List;

public final class Franchise {
    private String id;
    private String name;
    private List<Branch> branches;

    public Franchise(String id, String name, List<Branch> branches) {
        this.id = id;
        this.name = name;
        this.branches = branches;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Branch> getBranches() {
        return branches;
    }

    public void setBranches(List<Branch> branches) {
        this.branches = branches;
    }

    public Branch findBranchById(String branchId) {
        return this.branches.stream()
                .filter(br -> br.getId().equals(branchId))
                .findFirst().orElse(null);
    }

    public boolean existsBranchByName(String name) {
        return this.branches
                .stream()
                .anyMatch(fBranch -> fBranch.getName().equals(name));
    }
}
