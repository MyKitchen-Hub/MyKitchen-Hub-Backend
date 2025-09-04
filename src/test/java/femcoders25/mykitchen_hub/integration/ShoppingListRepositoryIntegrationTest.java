package femcoders25.mykitchen_hub.integration;

import femcoders25.mykitchen_hub.shoppinglist.entity.ShoppingList;
import femcoders25.mykitchen_hub.shoppinglist.repository.ShoppingListRepository;
import femcoders25.mykitchen_hub.user.entity.Role;
import femcoders25.mykitchen_hub.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class ShoppingListRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ShoppingListRepository shoppingListRepository;

    private User testUser;
    private ShoppingList testShoppingList;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("password");
        testUser.setRole(Role.USER);
        entityManager.persistAndFlush(testUser);

        testShoppingList = new ShoppingList();
        testShoppingList.setName("Test Shopping List");
        testShoppingList.setGeneratedBy(testUser);
        entityManager.persistAndFlush(testShoppingList);
    }

    @Test
    void findByGeneratedByOrderByCreatedAtDesc_ExistingUser_ReturnsShoppingLists() {
        List<ShoppingList> result = shoppingListRepository.findByGeneratedByOrderByCreatedAtDesc(testUser);
        
        assertEquals(1, result.size());
        assertEquals("Test Shopping List", result.get(0).getName());
    }

    @Test
    void findByGeneratedByAndNameContainingIgnoreCaseOrderByCreatedAtDesc_ExistingName_ReturnsShoppingList() {
        List<ShoppingList> result = shoppingListRepository.findByGeneratedByAndNameContainingIgnoreCaseOrderByCreatedAtDesc(testUser, "test");
        
        assertEquals(1, result.size());
        assertEquals("Test Shopping List", result.get(0).getName());
    }

    @Test
    void findByGeneratedByAndNameContainingIgnoreCaseOrderByCreatedAtDesc_NonExistingName_ReturnsEmpty() {
        List<ShoppingList> result = shoppingListRepository.findByGeneratedByAndNameContainingIgnoreCaseOrderByCreatedAtDesc(testUser, "nonexistent");
        
        assertTrue(result.isEmpty());
    }

    @Test
    void save_NewShoppingList_SavesSuccessfully() {
        ShoppingList newShoppingList = new ShoppingList();
        newShoppingList.setName("New Shopping List");
        newShoppingList.setGeneratedBy(testUser);
        
        ShoppingList saved = shoppingListRepository.save(newShoppingList);
        
        assertNotNull(saved.getId());
        assertEquals("New Shopping List", saved.getName());
        assertEquals(testUser, saved.getGeneratedBy());
    }
}
