package co.com.bancolombia.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BranchTest {

    private Product product1;
    private Product product2;
    private Product product3;
    private List<Product> products;

    @BeforeEach
    void setUp() {
        product1 = new Product("prod1", "Product 1", 10);
        product2 = new Product("prod2", "Product 2", 20);
        product3 = new Product("prod3", "Product 3", 30);
        products = new ArrayList<>(List.of(product1, product2, product3));
    }

    @Test
    @DisplayName("Should create branch with all parameters")
    void shouldCreateBranchWithAllParameters() {
        Branch branch = new Branch("branch1", "Test Branch", products);

        assertEquals("branch1", branch.getId());
        assertEquals("test branch", branch.getName()); // Should be trimmed and lowercase
        assertEquals(3, branch.getProducts().size());
        assertEquals(products, branch.getProducts());
    }

    @Test
    @DisplayName("Should transform name to lowercase and trim whitespace")
    void shouldTransformNameToLowercaseAndTrimWhitespace() {
        Branch branch = new Branch("branch1", "  MAIN BRANCH  ", products);

        assertEquals("branch1", branch.getId());
        assertEquals("main branch", branch.getName());
    }

    @Test
    @DisplayName("Should handle name with mixed case and spaces")
    void shouldHandleNameWithMixedCaseAndSpaces() {
        Branch branch = new Branch("branch1", "Main Branch Office", products);

        assertEquals("main branch office", branch.getName());
    }

    @Test
    @DisplayName("Should handle name with leading and trailing tabs")
    void shouldHandleNameWithLeadingAndTrailingTabs() {
        Branch branch = new Branch("branch1", "\t\tBranch Name\t\t", products);

        assertEquals("branch name", branch.getName());
    }

    @Test
    @DisplayName("Should create branch with null id")
    void shouldCreateBranchWithNullId() {
        Branch branch = new Branch(null, "Test Branch", products);

        assertNull(branch.getId());
        assertEquals("test branch", branch.getName());
        assertEquals(products, branch.getProducts());
    }

    @Test
    @DisplayName("Should create branch with empty products list")
    void shouldCreateBranchWithEmptyProductsList() {
        Branch branch = new Branch("branch1", "Test Branch", new ArrayList<>());

        assertEquals("branch1", branch.getId());
        assertEquals("test branch", branch.getName());
        assertTrue(branch.getProducts().isEmpty());
    }

    @Test
    @DisplayName("Should create branch with null products list")
    void shouldCreateBranchWithNullProductsList() {
        Branch branch = new Branch("branch1", "Test Branch", null);

        assertEquals("branch1", branch.getId());
        assertEquals("test branch", branch.getName());
        assertNull(branch.getProducts());
    }

    @Test
    @DisplayName("Should update branch id")
    void shouldUpdateBranchId() {
        Branch branch = new Branch("branch1", "Test Branch", products);
        
        branch.setId("newBranch1");
        
        assertEquals("newBranch1", branch.getId());
    }

    @Test
    @DisplayName("Should update branch name without transformation")
    void shouldUpdateBranchNameWithoutTransformation() {
        Branch branch = new Branch("branch1", "Test Branch", products);
        
        branch.setName("NEW BRANCH NAME");
        
        assertEquals("NEW BRANCH NAME", branch.getName()); // Setter doesn't transform
    }

    @Test
    @DisplayName("Should update products list")
    void shouldUpdateProductsList() {
        Branch branch = new Branch("branch1", "Test Branch", products);
        Product newProduct = new Product("prod4", "Product 4", 40);
        List<Product> newProducts = List.of(newProduct);
        
        branch.setProducts(newProducts);
        
        assertEquals(1, branch.getProducts().size());
        assertEquals(newProduct, branch.getProducts().get(0));
    }

    @Test
    @DisplayName("Should find existing product by id")
    void shouldFindExistingProductById() {
        Branch branch = new Branch("branch1", "Test Branch", products);

        Product foundProduct = branch.findProductById("prod2");

        assertNotNull(foundProduct);
        assertEquals("prod2", foundProduct.getId());
        assertEquals("Product 2", foundProduct.getName());
        assertEquals(20, foundProduct.getStock());
    }

    @Test
    @DisplayName("Should return null when product not found by id")
    void shouldReturnNullWhenProductNotFoundById() {
        Branch branch = new Branch("branch1", "Test Branch", products);

        Product foundProduct = branch.findProductById("nonexistent");

        assertNull(foundProduct);
    }

    @Test
    @DisplayName("Should return null when searching in empty products list")
    void shouldReturnNullWhenSearchingInEmptyProductsList() {
        Branch branch = new Branch("branch1", "Test Branch", new ArrayList<>());

        Product foundProduct = branch.findProductById("prod1");

        assertNull(foundProduct);
    }

    @Test
    @DisplayName("Should throw exception when searching in null products list")
    void shouldThrowExceptionWhenSearchingInNullProductsList() {
        Branch branch = new Branch("branch1", "Test Branch", null);

        assertThrows(NullPointerException.class, () -> {
            branch.findProductById("prod1");
        });
    }

    @Test
    @DisplayName("Should find product with null id when searching for null")
    void shouldFindProductWithNullIdWhenSearchingForNull() {
        Product productWithNullId = new Product(null, "No ID Product", 15);
        List<Product> productsWithNull = new ArrayList<>(List.of(product1, productWithNullId));
        Branch branch = new Branch("branch1", "Test Branch", productsWithNull);

        assertThrows(NullPointerException.class, () -> {
            branch.findProductById(null);
        });
    }

    @Test
    @DisplayName("Should return true when product exists by name")
    void shouldReturnTrueWhenProductExistsByName() {
        Branch branch = new Branch("branch1", "Test Branch", products);

        boolean exists = branch.existsProductByName(branch, "Product 2");

        assertTrue(exists);
    }

    @Test
    @DisplayName("Should return false when product does not exist by name")
    void shouldReturnFalseWhenProductDoesNotExistByName() {
        Branch branch = new Branch("branch1", "Test Branch", products);

        boolean exists = branch.existsProductByName(branch, "Nonexistent Product");

        assertFalse(exists);
    }

    @Test
    @DisplayName("Should return false when checking in empty products list")
    void shouldReturnFalseWhenCheckingInEmptyProductsList() {
        Branch branch = new Branch("branch1", "Test Branch", new ArrayList<>());

        boolean exists = branch.existsProductByName(branch, "Product 1");

        assertFalse(exists);
    }

    @Test
    @DisplayName("Should throw exception when checking in null products list")
    void shouldThrowExceptionWhenCheckingInNullProductsList() {
        Branch branch = new Branch("branch1", "Test Branch", null);

        assertThrows(NullPointerException.class, () -> {
            branch.existsProductByName(branch, "Product 1");
        });
    }

    @Test
    @DisplayName("Should return true when product name matches exactly")
    void shouldReturnTrueWhenProductNameMatchesExactly() {
        Branch branch = new Branch("branch1", "Test Branch", products);

        boolean exists = branch.existsProductByName(branch, "Product 1");

        assertTrue(exists);
    }

    @Test
    @DisplayName("Should return false when product name case does not match")
    void shouldReturnFalseWhenProductNameCaseDoesNotMatch() {
        Branch branch = new Branch("branch1", "Test Branch", products);

        boolean exists = branch.existsProductByName(branch, "product 1");

        assertFalse(exists);
    }

    @Test
    @DisplayName("Should handle searching for null product name")
    void shouldHandleSearchingForNullProductName() {
        Branch branch = new Branch("branch1", "Test Branch", products);

        assertFalse(branch.existsProductByName(branch, null));

    }

    @Test
    @DisplayName("Should handle empty string product name")
    void shouldHandleEmptyStringProductName() {
        Product emptyNameProduct = new Product("prod4", "", 40);
        List<Product> productsWithEmpty = new ArrayList<>(List.of(product1, emptyNameProduct));
        Branch branch = new Branch("branch1", "Test Branch", productsWithEmpty);

        boolean exists = branch.existsProductByName(branch, "");

        assertTrue(exists);
    }

    @Test
    @DisplayName("Should handle special characters in branch name")
    void shouldHandleSpecialCharactersInBranchName() {
        Branch branch = new Branch("branch1", "  Branch@#$%^&*()  ", products);

        assertEquals("branch@#$%^&*()", branch.getName());
    }

    @Test
    @DisplayName("Should handle Unicode characters in branch name")
    void shouldHandleUnicodeCharactersInBranchName() {
        Branch branch = new Branch("branch1", "  Sucursal ñáéíóúü 中文 🎉  ", products);

        assertEquals("sucursal ñáéíóúü 中文 🎉", branch.getName());
    }

    @Test
    @DisplayName("Should handle very long branch name")
    void shouldHandleVeryLongBranchName() {
        String longName = "  " + "A".repeat(1000) + "  ";
        Branch branch = new Branch("branch1", longName, products);

        assertEquals("a".repeat(1000), branch.getName());
    }

    @Test
    @DisplayName("Should handle multiple product operations")
    void shouldHandleMultipleProductOperations() {
        Branch branch = new Branch("branch1", "Test Branch", new ArrayList<>(products));

        // Find existing product
        Product found1 = branch.findProductById("prod1");
        assertNotNull(found1);

        // Check if product exists by name
        boolean exists1 = branch.existsProductByName(branch, "Product 1");
        assertTrue(exists1);

        // Add new product to the branch
        Product newProduct = new Product("prod4", "Product 4", 40);
        branch.getProducts().add(newProduct);

        // Find the new product
        Product found2 = branch.findProductById("prod4");
        assertNotNull(found2);
        assertEquals("Product 4", found2.getName());

        // Check if new product exists by name
        boolean exists2 = branch.existsProductByName(branch, "Product 4");
        assertTrue(exists2);

        assertEquals(4, branch.getProducts().size());
    }

    @Test
    @DisplayName("Should preserve product order in list")
    void shouldPreserveProductOrderInList() {
        Branch branch = new Branch("branch1", "Test Branch", products);

        assertEquals("prod1", branch.getProducts().get(0).getId());
        assertEquals("prod2", branch.getProducts().get(1).getId());
        assertEquals("prod3", branch.getProducts().get(2).getId());
    }
}