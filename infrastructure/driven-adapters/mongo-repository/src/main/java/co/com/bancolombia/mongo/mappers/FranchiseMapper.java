package co.com.bancolombia.mongo.mappers;

import co.com.bancolombia.model.Branch;
import co.com.bancolombia.model.Franchise;
import co.com.bancolombia.model.Product;
import co.com.bancolombia.mongo.documents.BranchDocument;
import co.com.bancolombia.mongo.documents.FranchiseDocument;
import co.com.bancolombia.mongo.documents.ProductDocument;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class FranchiseMapper {

    public static FranchiseDocument toDocument(Franchise franchise) {
        List<BranchDocument> branches = franchise.getBranches() == null
                ? new ArrayList<>()
                : franchise.getBranches().stream()
                .map(FranchiseMapper::toBranchDocument)
                .collect(Collectors.toCollection(ArrayList::new));

        return new FranchiseDocument(franchise.getId(), franchise.getName(), branches);
    }

    private static BranchDocument toBranchDocument(Branch branch) {
        List<ProductDocument> products = branch.getProducts() == null
                ? new ArrayList<>()
                : branch.getProducts().stream()
                .map(FranchiseMapper::toProductDocument)
                .collect(Collectors.toCollection(ArrayList::new));
        return new BranchDocument(branch.getId(), branch.getName(), products);
    }

    private static ProductDocument toProductDocument(Product product) {
        return new ProductDocument(product.getId(), product.getName(), product.getStock());
    }

    public static Franchise toDomain(FranchiseDocument franchise) {
        log.debug("Converting Franchise Document to Franchise.");
        List<Branch> branches = franchise.getBranches() == null
                ? new ArrayList<>()
                : franchise.getBranches().stream().map(FranchiseMapper::toBranchDomain)
                .collect(Collectors.toCollection(ArrayList::new));

        return new Franchise(franchise.getId(), franchise.getName(), branches);
    }

    private static Branch toBranchDomain(BranchDocument branchDocument) {
        List<Product> products = branchDocument.getProducts() == null
                ? new ArrayList<>()
                : branchDocument.getProducts().stream().map(FranchiseMapper::toProductDomain).collect(Collectors.toCollection(ArrayList::new));
        return new Branch(branchDocument.getId(), branchDocument.getName(), products);
    }

    private static Product toProductDomain(ProductDocument productDocument) {
        return new Product(productDocument.getId(), productDocument.getName(), productDocument.getStock());
    }

}
