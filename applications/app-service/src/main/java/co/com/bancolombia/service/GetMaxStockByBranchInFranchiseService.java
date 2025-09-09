package co.com.bancolombia.service;

import co.com.bancolombia.model.Branch;
import co.com.bancolombia.model.Franchise;
import co.com.bancolombia.model.Product;
import co.com.bancolombia.model.gateway.FranchiseRepositoryPort;
import co.com.bancolombia.usecase.in.product.GetMaxStockByBranchInFranchiseUseCase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
public class GetMaxStockByBranchInFranchiseService implements GetMaxStockByBranchInFranchiseUseCase {

    private final FranchiseRepositoryPort franchiseRepositoryPort;

    public GetMaxStockByBranchInFranchiseService(FranchiseRepositoryPort franchiseRepositoryPort) {
        this.franchiseRepositoryPort = franchiseRepositoryPort;
    }

    @Override
    public Mono<Franchise> getMaxStockByBranchInFranchise(String franchiseId) {
        log.info("Calculating Products with max stock");

        return this.franchiseRepositoryPort.findById(franchiseId)
                .map(franchise -> {
                            List<Branch> branches = franchise.getBranches()
                                    .stream()
                                    .map(branch -> {
                                        List<Product> product = branch.getProducts()
                                                .stream()
                                                .max(Comparator.comparingInt(Product::getStock))
                                                .map(List::of).orElse(List.of());
                                        return new Branch(branch.getId(), branch.getName(), product);
                                    })
                                    .toList();
                            return new Franchise(franchise.getId(), franchise.getName(), branches);
                        }
                );
    }
}
