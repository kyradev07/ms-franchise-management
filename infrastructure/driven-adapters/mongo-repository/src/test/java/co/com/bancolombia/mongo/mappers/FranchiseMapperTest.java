package co.com.bancolombia.mongo.mappers;

import co.com.bancolombia.model.Branch;
import co.com.bancolombia.model.Franchise;
import co.com.bancolombia.model.Product;
import co.com.bancolombia.mongo.documents.BranchDocument;
import co.com.bancolombia.mongo.documents.FranchiseDocument;
import co.com.bancolombia.mongo.documents.ProductDocument;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FranchiseMapperTest {

    @Test
    @DisplayName("Should convert Franchise to FranchiseDocument successfully")
    void shouldConvertFranchiseToDocument() {
        Product product = new Product("prod1", "product 1", 10);
        Branch branch = new Branch("branch1", "branch 1", List.of(product));
        Franchise franchise = new Franchise("franchise1", "franchise 1", List.of(branch));

        FranchiseDocument result = FranchiseMapper.toDocument(franchise);

        assertNotNull(result);
        assertEquals("franchise1", result.getId());
        assertEquals("franchise 1", result.getName());
        assertNotNull(result.getBranches());
        assertEquals(1, result.getBranches().size());
        
        BranchDocument branchDoc = result.getBranches().getFirst();
        assertEquals("branch1", branchDoc.getId());
        assertEquals("branch 1", branchDoc.getName());
        assertEquals(1, branchDoc.getProducts().size());
        
        ProductDocument productDoc = branchDoc.getProducts().getFirst();
        assertEquals("prod1", productDoc.getId());
        assertEquals("product 1", productDoc.getName());
        assertEquals(10, productDoc.getStock());
    }

    @Test
    @DisplayName("Should convert Franchise with null branches to FranchiseDocument")
    void shouldConvertFranchiseWithNullBranchesToDocument() {
        Franchise franchise = new Franchise("franchise1", "franchise 1", null);

        FranchiseDocument result = FranchiseMapper.toDocument(franchise);

        assertNotNull(result);
        assertEquals("franchise1", result.getId());
        assertEquals("franchise 1", result.getName());
        assertNotNull(result.getBranches());
        assertTrue(result.getBranches().isEmpty());
    }

    @Test
    @DisplayName("Should convert Franchise with empty branches to FranchiseDocument")
    void shouldConvertFranchiseWithEmptyBranchesToDocument() {
        Franchise franchise = new Franchise("franchise1", "franchise 1", new ArrayList<>());

        FranchiseDocument result = FranchiseMapper.toDocument(franchise);

        assertNotNull(result);
        assertEquals("franchise1", result.getId());
        assertEquals("franchise 1", result.getName());
        assertNotNull(result.getBranches());
        assertTrue(result.getBranches().isEmpty());
    }

    @Test
    @DisplayName("Should convert Branch with null products to BranchDocument")
    void shouldConvertBranchWithNullProductsToDocument() {
        Branch branch = new Branch("branch1", "branch 1", null);
        Franchise franchise = new Franchise("franchise1", "franchise 1", List.of(branch));

        FranchiseDocument result = FranchiseMapper.toDocument(franchise);

        assertNotNull(result);
        assertEquals(1, result.getBranches().size());
        
        BranchDocument branchDoc = result.getBranches().getFirst();
        assertEquals("branch1", branchDoc.getId());
        assertEquals("branch 1", branchDoc.getName());
        assertNotNull(branchDoc.getProducts());
        assertTrue(branchDoc.getProducts().isEmpty());
    }

    @Test
    @DisplayName("Should convert FranchiseDocument to Franchise successfully")
    void shouldConvertDocumentToFranchise() {
        ProductDocument productDoc = new ProductDocument("prod1", "product 1", 10);
        BranchDocument branchDoc = new BranchDocument("branch1", "branch 1", List.of(productDoc));
        FranchiseDocument franchiseDoc = new FranchiseDocument("franchise1", "franchise 1", List.of(branchDoc));

        Franchise result = FranchiseMapper.toDomain(franchiseDoc);

        assertNotNull(result);
        assertEquals("franchise1", result.getId());
        assertEquals("franchise 1", result.getName());
        assertNotNull(result.getBranches());
        assertEquals(1, result.getBranches().size());
        
        Branch branch = result.getBranches().getFirst();
        assertEquals("branch1", branch.getId());
        assertEquals("branch 1", branch.getName());
        assertEquals(1, branch.getProducts().size());
        
        Product product = branch.getProducts().getFirst();
        assertEquals("prod1", product.getId());
        assertEquals("product 1", product.getName());
        assertEquals(10, product.getStock());
    }

    @Test
    @DisplayName("Should convert FranchiseDocument with null branches to Franchise")
    void shouldConvertDocumentWithNullBranchesToFranchise() {
        FranchiseDocument franchiseDoc = new FranchiseDocument("franchise1", "franchise 1", null);

        Franchise result = FranchiseMapper.toDomain(franchiseDoc);

        assertNotNull(result);
        assertEquals("franchise1", result.getId());
        assertEquals("franchise 1", result.getName());
        assertNotNull(result.getBranches());
        assertTrue(result.getBranches().isEmpty());
    }

    @Test
    @DisplayName("Should convert FranchiseDocument with empty branches to Franchise")
    void shouldConvertDocumentWithEmptyBranchesToFranchise() {
        FranchiseDocument franchiseDoc = new FranchiseDocument("franchise1", "franchise 1", new ArrayList<>());

        Franchise result = FranchiseMapper.toDomain(franchiseDoc);

        assertNotNull(result);
        assertEquals("franchise1", result.getId());
        assertEquals("franchise 1", result.getName());
        assertNotNull(result.getBranches());
        assertTrue(result.getBranches().isEmpty());
    }

    @Test
    @DisplayName("Should convert BranchDocument with null products to Branch")
    void shouldConvertBranchDocumentWithNullProductsToBranch() {
        BranchDocument branchDoc = new BranchDocument("branch1", "branch 1", null);
        FranchiseDocument franchiseDoc = new FranchiseDocument("franchise1", "franchise 1", List.of(branchDoc));

        Franchise result = FranchiseMapper.toDomain(franchiseDoc);

        assertNotNull(result);
        assertEquals(1, result.getBranches().size());
        
        Branch branch = result.getBranches().getFirst();
        assertEquals("branch1", branch.getId());
        assertEquals("branch 1", branch.getName());
        assertNotNull(branch.getProducts());
        assertTrue(branch.getProducts().isEmpty());
    }

    @Test
    @DisplayName("Should handle complex franchise structure with multiple branches and products")
    void shouldHandleComplexFranchiseStructure() {
        Product product1 = new Product("prod1", "product 1", 10);
        Product product2 = new Product("prod2", "product 2", 20);
        Product product3 = new Product("prod3", "product 3", 30);
        
        Branch branch1 = new Branch("branch1", "branch 1", List.of(product1, product2));
        Branch branch2 = new Branch("branch2", "branch 2", List.of(product3));
        
        Franchise franchise = new Franchise("franchise1", "franchise 1", List.of(branch1, branch2));

        FranchiseDocument document = FranchiseMapper.toDocument(franchise);
        Franchise reconstructedFranchise = FranchiseMapper.toDomain(document);

        assertNotNull(reconstructedFranchise);
        assertEquals(franchise.getId(), reconstructedFranchise.getId());
        assertEquals(franchise.getName(), reconstructedFranchise.getName());
        assertEquals(2, reconstructedFranchise.getBranches().size());
        
        Branch reconstructedBranch1 = reconstructedFranchise.getBranches().getFirst();
        assertEquals("branch1", reconstructedBranch1.getId());
        assertEquals("branch 1", reconstructedBranch1.getName());
        assertEquals(2, reconstructedBranch1.getProducts().size());
        
        Branch reconstructedBranch2 = reconstructedFranchise.getBranches().get(1);
        assertEquals("branch2", reconstructedBranch2.getId());
        assertEquals("branch 2", reconstructedBranch2.getName());
        assertEquals(1, reconstructedBranch2.getProducts().size());
    }

    @Test
    @DisplayName("Should handle edge case with zero stock product")
    void shouldHandleZeroStockProduct() {
        Product product = new Product("prod1", "product 1", 0);
        Branch branch = new Branch("branch1", "branch 1", List.of(product));
        Franchise franchise = new Franchise("franchise1", "franchise 1", List.of(branch));

        FranchiseDocument document = FranchiseMapper.toDocument(franchise);
        Franchise reconstructedFranchise = FranchiseMapper.toDomain(document);

        assertNotNull(reconstructedFranchise);
        Product reconstructedProduct = reconstructedFranchise.getBranches().getFirst().getProducts().getFirst();
        assertEquals(0, reconstructedProduct.getStock());
    }

    @Test
    @DisplayName("Should handle products with null stock")
    void shouldHandleProductsWithNullStock() {
        Product product = new Product("prod1", "product 1", null);
        Branch branch = new Branch("branch1", "branch 1", List.of(product));
        Franchise franchise = new Franchise("franchise1", "franchise 1", List.of(branch));

        FranchiseDocument document = FranchiseMapper.toDocument(franchise);
        Franchise reconstructedFranchise = FranchiseMapper.toDomain(document);

        assertNotNull(reconstructedFranchise);
        Product reconstructedProduct = reconstructedFranchise.getBranches().getFirst().getProducts().getFirst();
        assertNull(reconstructedProduct.getStock());
    }

}