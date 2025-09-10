package co.com.bancolombia.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FranchiseTest {

    private Product product1;
    private Product product2;
    private Product product3;
    private Branch branch1;
    private Branch branch2;
    private Branch branch3;
    private List<Branch> branches;

    @BeforeEach
    void setUp() {
        product1 = new Product("prod1", "Product 1", 10);
        product2 = new Product("prod2", "Product 2", 20);
        product3 = new Product("prod3", "Product 3", 30);

        branch1 = new Branch("branch1", "Main Branch", List.of(product1, product2));
        branch2 = new Branch("branch2", "Secondary Branch", List.of(product3));
        branch3 = new Branch("branch3", "Third Branch", new ArrayList<>());

        branches = new ArrayList<>(List.of(branch1, branch2, branch3));
    }

    @Test
    @DisplayName("Should create franchise with all parameters")
    void shouldCreateFranchiseWithAllParameters() {
        Franchise franchise = new Franchise("franchise1", "Test Franchise", branches);

        assertEquals("franchise1", franchise.getId());
        assertEquals("Test Franchise", franchise.getName());
        assertEquals(3, franchise.getBranches().size());
        assertEquals(branches, franchise.getBranches());
    }

    @Test
    @DisplayName("Should create franchise with null id")
    void shouldCreateFranchiseWithNullId() {
        Franchise franchise = new Franchise(null, "Test Franchise", branches);

        assertNull(franchise.getId());
        assertEquals("Test Franchise", franchise.getName());
        assertEquals(branches, franchise.getBranches());
    }

    @Test
    @DisplayName("Should create franchise with null name")
    void shouldCreateFranchiseWithNullName() {
        Franchise franchise = new Franchise("franchise1", null, branches);

        assertEquals("franchise1", franchise.getId());
        assertNull(franchise.getName());
        assertEquals(branches, franchise.getBranches());
    }

    @Test
    @DisplayName("Should create franchise with empty branches list")
    void shouldCreateFranchiseWithEmptyBranchesList() {
        Franchise franchise = new Franchise("franchise1", "Test Franchise", new ArrayList<>());

        assertEquals("franchise1", franchise.getId());
        assertEquals("Test Franchise", franchise.getName());
        assertTrue(franchise.getBranches().isEmpty());
    }

    @Test
    @DisplayName("Should create franchise with null branches list")
    void shouldCreateFranchiseWithNullBranchesList() {
        Franchise franchise = new Franchise("franchise1", "Test Franchise", null);

        assertEquals("franchise1", franchise.getId());
        assertEquals("Test Franchise", franchise.getName());
        assertNull(franchise.getBranches());
    }

    @Test
    @DisplayName("Should update franchise id")
    void shouldUpdateFranchiseId() {
        Franchise franchise = new Franchise("franchise1", "Test Franchise", branches);
        
        franchise.setId("newFranchise1");
        
        assertEquals("newFranchise1", franchise.getId());
    }

    @Test
    @DisplayName("Should update franchise name")
    void shouldUpdateFranchiseName() {
        Franchise franchise = new Franchise("franchise1", "Test Franchise", branches);
        
        franchise.setName("Updated Franchise");
        
        assertEquals("Updated Franchise", franchise.getName());
    }

    @Test
    @DisplayName("Should update branches list")
    void shouldUpdateBranchesList() {
        Franchise franchise = new Franchise("franchise1", "Test Franchise", branches);
        Branch newBranch = new Branch("branch4", "New Branch", new ArrayList<>());
        List<Branch> newBranches = List.of(newBranch);
        
        franchise.setBranches(newBranches);
        
        assertEquals(1, franchise.getBranches().size());
        assertEquals(newBranch, franchise.getBranches().get(0));
    }

    @Test
    @DisplayName("Should find existing branch by id")
    void shouldFindExistingBranchById() {
        Franchise franchise = new Franchise("franchise1", "Test Franchise", branches);

        Branch foundBranch = franchise.findBranchById("branch2");

        assertNotNull(foundBranch);
        assertEquals("branch2", foundBranch.getId());
        assertEquals("secondary branch", foundBranch.getName());
    }

    @Test
    @DisplayName("Should return null when branch not found by id")
    void shouldReturnNullWhenBranchNotFoundById() {
        Franchise franchise = new Franchise("franchise1", "Test Franchise", branches);

        Branch foundBranch = franchise.findBranchById("nonexistent");

        assertNull(foundBranch);
    }

    @Test
    @DisplayName("Should return null when searching in empty branches list")
    void shouldReturnNullWhenSearchingInEmptyBranchesList() {
        Franchise franchise = new Franchise("franchise1", "Test Franchise", new ArrayList<>());

        Branch foundBranch = franchise.findBranchById("branch1");

        assertNull(foundBranch);
    }

    @Test
    @DisplayName("Should throw exception when searching in null branches list")
    void shouldThrowExceptionWhenSearchingInNullBranchesList() {
        Franchise franchise = new Franchise("franchise1", "Test Franchise", null);

        assertThrows(NullPointerException.class, () -> {
            franchise.findBranchById("branch1");
        });
    }

    @Test
    @DisplayName("Should handle null branch id when searching for null")
    void shouldHandleNullBranchIdWhenSearchingForNull() {
        Franchise franchise = new Franchise("franchise1", "Test Franchise", branches);

        assertNull(franchise.findBranchById(null));
    }

    @Test
    @DisplayName("Should find branch with null id when searching for null")
    void shouldFindBranchWithNullIdWhenSearchingForNull() {
        Branch branchWithNullId = new Branch(null, "No ID Branch", new ArrayList<>());
        List<Branch> branchesWithNull = new ArrayList<>(List.of(branch1, branchWithNullId));
        Franchise franchise = new Franchise("franchise1", "Test Franchise", branchesWithNull);

        assertThrows(NullPointerException.class, () -> {
            franchise.findBranchById(null);
        });
    }

    @Test
    @DisplayName("Should return true when branch exists by name")
    void shouldReturnTrueWhenBranchExistsByName() {
        Franchise franchise = new Franchise("franchise1", "Test Franchise", branches);

        boolean exists = franchise.existsBranchByName("secondary branch");

        assertTrue(exists);
    }

    @Test
    @DisplayName("Should return false when branch does not exist by name")
    void shouldReturnFalseWhenBranchDoesNotExistByName() {
        Franchise franchise = new Franchise("franchise1", "Test Franchise", branches);

        boolean exists = franchise.existsBranchByName("nonexistent branch");

        assertFalse(exists);
    }

    @Test
    @DisplayName("Should return false when checking in empty branches list")
    void shouldReturnFalseWhenCheckingInEmptyBranchesList() {
        Franchise franchise = new Franchise("franchise1", "Test Franchise", new ArrayList<>());

        boolean exists = franchise.existsBranchByName("main branch");

        assertFalse(exists);
    }

    @Test
    @DisplayName("Should throw exception when checking in null branches list")
    void shouldThrowExceptionWhenCheckingInNullBranchesList() {
        Franchise franchise = new Franchise("franchise1", "Test Franchise", null);

        assertThrows(NullPointerException.class, () -> {
            franchise.existsBranchByName("main branch");
        });
    }

    @Test
    @DisplayName("Should return true when branch name matches exactly")
    void shouldReturnTrueWhenBranchNameMatchesExactly() {
        Franchise franchise = new Franchise("franchise1", "Test Franchise", branches);

        boolean exists = franchise.existsBranchByName("main branch");

        assertTrue(exists);
    }

    @Test
    @DisplayName("Should return false when branch name case does not match")
    void shouldReturnFalseWhenBranchNameCaseDoesNotMatch() {
        Franchise franchise = new Franchise("franchise1", "Test Franchise", branches);

        boolean exists = franchise.existsBranchByName("Main Branch");

        assertFalse(exists);
    }

    @Test
    @DisplayName("Should handle searching for null branch name")
    void shouldHandleSearchingForNullBranchName() {
        Franchise franchise = new Franchise("franchise1", "Test Franchise", branches);

        assertFalse(franchise.existsBranchByName(null));
    }

    @Test
    @DisplayName("Should handle empty string branch name")
    void shouldHandleEmptyStringBranchName() {
        Branch emptyNameBranch = new Branch("branch4", "", new ArrayList<>());
        List<Branch> branchesWithEmpty = new ArrayList<>(List.of(branch1, emptyNameBranch));
        Franchise franchise = new Franchise("franchise1", "Test Franchise", branchesWithEmpty);

        boolean exists = franchise.existsBranchByName("");

        assertTrue(exists);
    }

    @Test
    @DisplayName("Should handle special characters in franchise name")
    void shouldHandleSpecialCharactersInFranchiseName() {
        Franchise franchise = new Franchise("franchise1", "Franchise@#$%^&*()", branches);

        assertEquals("Franchise@#$%^&*()", franchise.getName());
    }

    @Test
    @DisplayName("Should handle Unicode characters in franchise name")
    void shouldHandleUnicodeCharactersInFranchiseName() {
        Franchise franchise = new Franchise("franchise1", "Franquicia ñáéíóúü 中文 🎉", branches);

        assertEquals("Franquicia ñáéíóúü 中文 🎉", franchise.getName());
    }

    @Test
    @DisplayName("Should handle very long franchise name")
    void shouldHandleVeryLongFranchiseName() {
        String longName = "F".repeat(1000);
        Franchise franchise = new Franchise("franchise1", longName, branches);

        assertEquals(longName, franchise.getName());
    }

    @Test
    @DisplayName("Should handle multiple branch operations")
    void shouldHandleMultipleBranchOperations() {
        Franchise franchise = new Franchise("franchise1", "Test Franchise", new ArrayList<>(branches));

        // Find existing branch
        Branch found1 = franchise.findBranchById("branch1");
        assertNotNull(found1);

        // Check if branch exists by name
        boolean exists1 = franchise.existsBranchByName("main branch");
        assertTrue(exists1);

        // Add new branch to the franchise
        Branch newBranch = new Branch("branch4", "Fourth Branch", new ArrayList<>());
        franchise.getBranches().add(newBranch);

        // Find the new branch
        Branch found2 = franchise.findBranchById("branch4");
        assertNotNull(found2);
        assertEquals("fourth branch", found2.getName());

        // Check if new branch exists by name
        boolean exists2 = franchise.existsBranchByName("fourth branch");
        assertTrue(exists2);

        assertEquals(4, franchise.getBranches().size());
    }

    @Test
    @DisplayName("Should preserve branch order in list")
    void shouldPreserveBranchOrderInList() {
        Franchise franchise = new Franchise("franchise1", "Test Franchise", branches);

        assertEquals("branch1", franchise.getBranches().get(0).getId());
        assertEquals("branch2", franchise.getBranches().get(1).getId());
        assertEquals("branch3", franchise.getBranches().get(2).getId());
    }

    @Test
    @DisplayName("Should handle complex franchise structure")
    void shouldHandleComplexFranchiseStructure() {
        Franchise franchise = new Franchise("franchise1", "Complex Franchise", branches);

        // Verify franchise structure
        assertEquals(3, franchise.getBranches().size());

        // Verify first branch has 2 products
        Branch firstBranch = franchise.findBranchById("branch1");
        assertNotNull(firstBranch);
        assertEquals(2, firstBranch.getProducts().size());

        // Verify second branch has 1 product
        Branch secondBranch = franchise.findBranchById("branch2");
        assertNotNull(secondBranch);
        assertEquals(1, secondBranch.getProducts().size());

        // Verify third branch has no products
        Branch thirdBranch = franchise.findBranchById("branch3");
        assertNotNull(thirdBranch);
        assertTrue(thirdBranch.getProducts().isEmpty());

        // Verify branch names exist
        assertTrue(franchise.existsBranchByName("main branch"));
        assertTrue(franchise.existsBranchByName("secondary branch"));
        assertTrue(franchise.existsBranchByName("third branch"));
    }

    @Test
    @DisplayName("Should handle franchise with single branch")
    void shouldHandleFranchiseWithSingleBranch() {
        List<Branch> singleBranch = List.of(branch1);
        Franchise franchise = new Franchise("franchise1", "Single Branch Franchise", singleBranch);

        assertEquals(1, franchise.getBranches().size());
        
        Branch found = franchise.findBranchById("branch1");
        assertNotNull(found);
        
        boolean exists = franchise.existsBranchByName("main branch");
        assertTrue(exists);
        
        boolean notExists = franchise.existsBranchByName("secondary branch");
        assertFalse(notExists);
    }

    @Test
    @DisplayName("Should handle franchise updates")
    void shouldHandleFranchiseUpdates() {
        Franchise franchise = new Franchise("franchise1", "Original Franchise", branches);

        // Update franchise properties
        franchise.setId("updatedFranchise1");
        franchise.setName("Updated Franchise Name");

        // Verify updates
        assertEquals("updatedFranchise1", franchise.getId());
        assertEquals("Updated Franchise Name", franchise.getName());

        // Verify branches are preserved
        assertEquals(3, franchise.getBranches().size());
        assertNotNull(franchise.findBranchById("branch1"));
        assertTrue(franchise.existsBranchByName("main branch"));
    }
}