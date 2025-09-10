package co.com.bancolombia.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProductTest {

    @Test
    @DisplayName("Should create product with all parameters")
    void shouldCreateProductWithAllParameters() {
        Product product = new Product("prod1", "Test Product", 10);

        assertEquals("prod1", product.getId());
        assertEquals("Test Product", product.getName());
        assertEquals(10, product.getStock());
    }

    @Test
    @DisplayName("Should create product with null id")
    void shouldCreateProductWithNullId() {
        Product product = new Product(null, "Test Product", 10);

        assertNull(product.getId());
        assertEquals("Test Product", product.getName());
        assertEquals(10, product.getStock());
    }

    @Test
    @DisplayName("Should create product with null name")
    void shouldCreateProductWithNullName() {
        Product product = new Product("prod1", null, 10);

        assertEquals("prod1", product.getId());
        assertNull(product.getName());
        assertEquals(10, product.getStock());
    }

    @Test
    @DisplayName("Should create product with null stock")
    void shouldCreateProductWithNullStock() {
        Product product = new Product("prod1", "Test Product", null);

        assertEquals("prod1", product.getId());
        assertEquals("Test Product", product.getName());
        assertNull(product.getStock());
    }

    @Test
    @DisplayName("Should create product with zero stock")
    void shouldCreateProductWithZeroStock() {
        Product product = new Product("prod1", "Test Product", 0);

        assertEquals("prod1", product.getId());
        assertEquals("Test Product", product.getName());
        assertEquals(0, product.getStock());
    }

    @Test
    @DisplayName("Should create product with negative stock")
    void shouldCreateProductWithNegativeStock() {
        Product product = new Product("prod1", "Test Product", -5);

        assertEquals("prod1", product.getId());
        assertEquals("Test Product", product.getName());
        assertEquals(-5, product.getStock());
    }

    @Test
    @DisplayName("Should create product with empty string name")
    void shouldCreateProductWithEmptyStringName() {
        Product product = new Product("prod1", "", 10);

        assertEquals("prod1", product.getId());
        assertEquals("", product.getName());
        assertEquals(10, product.getStock());
    }

    @Test
    @DisplayName("Should create product with blank string name")
    void shouldCreateProductWithBlankStringName() {
        Product product = new Product("prod1", "   ", 10);

        assertEquals("prod1", product.getId());
        assertEquals("   ", product.getName());
        assertEquals(10, product.getStock());
    }

    @Test
    @DisplayName("Should update product id")
    void shouldUpdateProductId() {
        Product product = new Product("prod1", "Test Product", 10);
        
        product.setId("newId");
        
        assertEquals("newId", product.getId());
    }

    @Test
    @DisplayName("Should update product id to null")
    void shouldUpdateProductIdToNull() {
        Product product = new Product("prod1", "Test Product", 10);
        
        product.setId(null);
        
        assertNull(product.getId());
    }

    @Test
    @DisplayName("Should update product name")
    void shouldUpdateProductName() {
        Product product = new Product("prod1", "Test Product", 10);
        
        product.setName("Updated Product");
        
        assertEquals("Updated Product", product.getName());
    }

    @Test
    @DisplayName("Should update product name to null")
    void shouldUpdateProductNameToNull() {
        Product product = new Product("prod1", "Test Product", 10);
        
        product.setName(null);
        
        assertNull(product.getName());
    }

    @Test
    @DisplayName("Should update product name to empty string")
    void shouldUpdateProductNameToEmptyString() {
        Product product = new Product("prod1", "Test Product", 10);
        
        product.setName("");
        
        assertEquals("", product.getName());
    }

    @Test
    @DisplayName("Should update product stock")
    void shouldUpdateProductStock() {
        Product product = new Product("prod1", "Test Product", 10);
        
        product.setStock(20);
        
        assertEquals(20, product.getStock());
    }

    @Test
    @DisplayName("Should update product stock to zero")
    void shouldUpdateProductStockToZero() {
        Product product = new Product("prod1", "Test Product", 10);
        
        product.setStock(0);
        
        assertEquals(0, product.getStock());
    }

    @Test
    @DisplayName("Should update product stock to negative value")
    void shouldUpdateProductStockToNegativeValue() {
        Product product = new Product("prod1", "Test Product", 10);
        
        product.setStock(-5);
        
        assertEquals(-5, product.getStock());
    }

    @Test
    @DisplayName("Should update product stock to null")
    void shouldUpdateProductStockToNull() {
        Product product = new Product("prod1", "Test Product", 10);
        
        product.setStock(null);
        
        assertNull(product.getStock());
    }

    @Test
    @DisplayName("Should handle large stock values")
    void shouldHandleLargeStockValues() {
        Product product = new Product("prod1", "Test Product", Integer.MAX_VALUE);

        assertEquals("prod1", product.getId());
        assertEquals("Test Product", product.getName());
        assertEquals(Integer.MAX_VALUE, product.getStock());
    }

    @Test
    @DisplayName("Should handle minimum stock values")
    void shouldHandleMinimumStockValues() {
        Product product = new Product("prod1", "Test Product", Integer.MIN_VALUE);

        assertEquals("prod1", product.getId());
        assertEquals("Test Product", product.getName());
        assertEquals(Integer.MIN_VALUE, product.getStock());
    }

    @Test
    @DisplayName("Should handle special characters in name")
    void shouldHandleSpecialCharactersInName() {
        Product product = new Product("prod1", "Product@#$%^&*()_+-=[]{}|;':\",./<>?", 10);

        assertEquals("prod1", product.getId());
        assertEquals("Product@#$%^&*()_+-=[]{}|;':\",./<>?", product.getName());
        assertEquals(10, product.getStock());
    }

    @Test
    @DisplayName("Should handle Unicode characters in name")
    void shouldHandleUnicodeCharactersInName() {
        Product product = new Product("prod1", "Producto ñáéíóúü 中文 🎉", 10);

        assertEquals("prod1", product.getId());
        assertEquals("Producto ñáéíóúü 中文 🎉", product.getName());
        assertEquals(10, product.getStock());
    }

    @Test
    @DisplayName("Should handle very long product name")
    void shouldHandleVeryLongProductName() {
        String longName = "a".repeat(1000);
        Product product = new Product("prod1", longName, 10);

        assertEquals("prod1", product.getId());
        assertEquals(longName, product.getName());
        assertEquals(10, product.getStock());
    }

    @Test
    @DisplayName("Should handle multiple updates to same product")
    void shouldHandleMultipleUpdatesToSameProduct() {
        Product product = new Product("prod1", "Initial Product", 10);

        // First update
        product.setName("Updated Product");
        product.setStock(20);
        
        assertEquals("Updated Product", product.getName());
        assertEquals(20, product.getStock());

        // Second update
        product.setId("newProd1");
        product.setName("Final Product");
        product.setStock(30);
        
        assertEquals("newProd1", product.getId());
        assertEquals("Final Product", product.getName());
        assertEquals(30, product.getStock());
    }
}