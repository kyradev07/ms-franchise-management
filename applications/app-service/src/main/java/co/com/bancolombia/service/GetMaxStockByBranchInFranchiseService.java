package co.com.bancolombia.service;

import co.com.bancolombia.model.Branch;
import co.com.bancolombia.model.Franchise;
import co.com.bancolombia.model.Product;
import co.com.bancolombia.model.gateway.FranchiseRepositoryPort;
import co.com.bancolombia.service.base.BaseFranchiseService;
import co.com.bancolombia.usecase.in.product.GetMaxStockByBranchInFranchiseUseCase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
public class GetMaxStockByBranchInFranchiseService extends BaseFranchiseService implements GetMaxStockByBranchInFranchiseUseCase {

    public GetMaxStockByBranchInFranchiseService(FranchiseRepositoryPort franchiseRepositoryPort) {
        super(franchiseRepositoryPort);
    }

    @Override
    public Mono<Franchise> getMaxStockByBranchInFranchise(String franchiseId) {
        logOperationStart("Calculating Products with max stock for Franchise %s", franchiseId);

        return franchiseRepositoryPort.findById(franchiseId)
                .map(this::buildFranchiseWithMaxStockProducts);
    }

    private Franchise buildFranchiseWithMaxStockProducts(Franchise franchise) {
        List<Branch> branchesWithMaxStock = franchise.getBranches()
                .stream()
                .map(this::buildBranchWithMaxStockProduct)
                .toList();
        
        return new Franchise(franchise.getId(), franchise.getName(), branchesWithMaxStock);
    }

    private Branch buildBranchWithMaxStockProduct(Branch branch) {
        List<Product> maxStockProduct = findMaxStockProduct(branch.getProducts());
        return new Branch(branch.getId(), branch.getName(), maxStockProduct);
    }

    private List<Product> findMaxStockProduct(List<Product> products) {
        return products.stream()
                .max(Comparator.comparingInt(Product::getStock))
                .map(List::of)
                .orElse(List.of());
    }
}
