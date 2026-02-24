package id.ac.ui.cs.advprog.eshop.repository;

import id.ac.ui.cs.advprog.eshop.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org. junit.jupiter.api.Test;
import org. junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ProductRepositoryTest {

    @InjectMocks
    ProductRepository productRepository;

    @BeforeEach
    void setUp() {
        // Setup
    }

    @Test
    void testCreateAndFind() {
        Product product = new Product();
        product.setProductId("eb558e9f-1c39-460e-8860-71af6af63bd6");
        product.setProductName("Sampo Cap Bambang");
        product.setProductQuantity(100);
        productRepository.create(product);
        Iterator<Product> productIterator = productRepository.findAll();
        assertTrue(productIterator.hasNext());
        Product savedProduct = productIterator.next();
        assertEquals(product.getProductId(), savedProduct.getProductId());
        assertEquals(product.getProductName(), savedProduct.getProductName());
        assertEquals(product.getProductQuantity(), savedProduct.getProductQuantity());
    }

    @Test
    void testFindAllIfEmpty() {
        Iterator<Product> productIterator = productRepository.findAll();
        assertFalse(productIterator.hasNext());
    }

    @Test
        void testFindAllIfMoreThanOneProduct() {
            Product product1 = new Product();
            product1.setProductId("eb558e9f-1c39-460e-8860-71af6af63bd6");
            product1.setProductName ("Sampo Cap Bambang"); product1. setProductQuantity (100); productRepository.create(product1);

            Product product2 = new Product();
            product2.setProductId("a0f9de46-90b1-437d-a0bf-d0821dde9096");
            product2. setProductName ("Sampo Cap Usep"); product2. setProductQuantity (50); productRepository.create(product2);
            Iterator<Product> productIterator = productRepository.findAll();
            assertTrue(productIterator.hasNext());
            Product savedProduct = productIterator.next();
            assertEquals(product1.getProductId(), savedProduct.getProductId());
            savedProduct = productIterator.next();
            assertEquals(product2.getProductId(), savedProduct.getProductId());
            assertFalse(productIterator.hasNext());
        }

    @Test
    void testFindByIdPositive() {
        Product product = new Product();
        product.setProductId("id-1");
        product.setProductName("Product A");
        product.setProductQuantity(10);

        productRepository.create(product);

        Product found = productRepository.findById("id-1");
        assertNotNull(found);
        assertEquals("id-1", found.getProductId());
        assertEquals("Product A", found.getProductName());
        assertEquals(10, found.getProductQuantity());
    }

    @Test
    void testFindByIdNegative() {
        Product found = productRepository.findById("not-exist");
        assertNull(found);
    }

    @Test
    void testUpdatePositive() {
        Product product = new Product();
        product.setProductId("id-1");
        product.setProductName("Old Name");
        product.setProductQuantity(10);
        productRepository.create(product);

        Product updated = new Product();
        updated.setProductId("id-1");
        updated.setProductName("New Name");
        updated.setProductQuantity(99);

        Product result = productRepository.update(updated);

        assertNotNull(result);
        assertEquals("id-1", result.getProductId());
        assertEquals("New Name", result.getProductName());
        assertEquals(99, result.getProductQuantity());

        Product fetchedAgain = productRepository.findById("id-1");
        assertNotNull(fetchedAgain);
        assertEquals("New Name", fetchedAgain.getProductName());
        assertEquals(99, fetchedAgain.getProductQuantity());
    }

    @Test
    void testUpdateNegative() {
        Product updated = new Product();
        updated.setProductId("not-exist");
        updated.setProductName("Doesn't Matter");
        updated.setProductQuantity(1);

        Product result = productRepository.update(updated);

        assertNull(result);
    }

    @Test
    void testDeletePositive() {
        Product product = new Product();
        product.setProductId("id-1");
        product.setProductName("To Delete");
        product.setProductQuantity(10);
        productRepository.create(product);

        boolean deleted = productRepository.delete("id-1");
        assertTrue(deleted);

        Product found = productRepository.findById("id-1");
        assertNull(found);
    }

    @Test
    void testDeleteNegative() {
        boolean deleted = productRepository.delete("not-exist");
        assertFalse(deleted);
    }

    @Test
    void testCreate_shouldGenerateId_whenIdIsNull() {
        Product product = new Product();
        product.setProductId(null);
        product.setProductName("No ID Product");
        product.setProductQuantity(1);

        Product saved = productRepository.create(product);

        assertNotNull(saved.getProductId());
        assertFalse(saved.getProductId().isEmpty());
    }

    @Test
    void testCreate_shouldGenerateId_whenIdIsEmpty() {
        Product product = new Product();
        product.setProductId("");
        product.setProductName("Empty ID Product");
        product.setProductQuantity(1);

        Product saved = productRepository.create(product);

        assertNotNull(saved.getProductId());
        assertFalse(saved.getProductId().isEmpty());
    }

    @Test
    void testFindById_whenProductExistsButIdDoesNotMatch_shouldReturnNull() {
        Product product = new Product();
        product.setProductId("id-1");
        product.setProductName("A");
        product.setProductQuantity(10);

        productRepository.create(product);

        // repository is NOT empty, but id does not match
        Product found = productRepository.findById("different-id");

        assertNull(found);
    }

    @Test
    void testUpdate_whenListNotEmptyButIdNotMatching_shouldReturnNull() {
        Product existing = new Product();
        existing.setProductId("id-1");
        existing.setProductName("Old");
        existing.setProductQuantity(10);
        productRepository.create(existing);

        Product updated = new Product();
        updated.setProductId("different-id"); // not matching
        updated.setProductName("New");
        updated.setProductQuantity(99);

        Product result = productRepository.update(updated);

        assertNull(result);

        // also confirm original is unchanged
        Product stillThere = productRepository.findById("id-1");
        assertNotNull(stillThere);
        assertEquals("Old", stillThere.getProductName());
        assertEquals(10, stillThere.getProductQuantity());
    }

    @Test
    void testDelete_whenListNotEmptyButIdNotMatching_shouldReturnFalse() {
        Product existing = new Product();
        existing.setProductId("id-1");
        existing.setProductName("A");
        existing.setProductQuantity(10);
        productRepository.create(existing);

        boolean deleted = productRepository.delete("different-id"); // not matching

        assertFalse(deleted);

        // confirm the product is still there
        assertNotNull(productRepository.findById("id-1"));
    }
}