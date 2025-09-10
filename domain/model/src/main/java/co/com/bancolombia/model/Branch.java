package co.com.bancolombia.model;

import java.util.List;

public final class Branch {
    private String id;
    private String name;
    private List<Product> products;

    public Branch(String id, String name, List<Product> products) {
        this.id = id;
        this.name = name.trim().toLowerCase();
        this.products = products;
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

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public boolean existsProductByName(Branch branch, String name) {
        return branch.getProducts()
                .stream()
                .anyMatch(product -> product.getName().equals(name));
    }

    public Product findProductById(String id) {
        return this.products
                .stream()
                .filter(product -> product.getId().equals(id))
                .findFirst().orElse(null);
    }
}
