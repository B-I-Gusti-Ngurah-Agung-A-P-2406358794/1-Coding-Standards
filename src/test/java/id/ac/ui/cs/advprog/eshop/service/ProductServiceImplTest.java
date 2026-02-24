package id.ac.ui.cs.advprog.eshop.service;

import id.ac.ui.cs.advprog.eshop.model.Product;
import id.ac.ui.cs.advprog.eshop.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Iterator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    ProductRepository productRepository;

    @InjectMocks
    ProductServiceImpl productService;

    @Test
    void create_callsRepositoryCreate() {
        Product p = new Product();
        p.setProductName("A");
        p.setProductQuantity(10);

        when(productRepository.create(any(Product.class))).thenReturn(p);

        Product result = productService.create(p);

        assertSame(p, result);
        verify(productRepository, times(1)).create(p);
    }

    @Test
    void findAll_convertsIteratorToList() {
        Product p1 = new Product(); p1.setProductId("1");
        Product p2 = new Product(); p2.setProductId("2");

        Iterator<Product> it = List.of(p1, p2).iterator();
        when(productRepository.findAll()).thenReturn(it);

        List<Product> result = productService.findAll();

        assertEquals(2, result.size());
        assertEquals("1", result.get(0).getProductId());
        assertEquals("2", result.get(1).getProductId());
        verify(productRepository, times(1)).findAll();
    }

    @Test
    void findById_delegatesToRepository() {
        Product p = new Product();
        p.setProductId("id-1");

        when(productRepository.findById("id-1")).thenReturn(p);

        Product result = productService.findById("id-1");

        assertNotNull(result);
        assertEquals("id-1", result.getProductId());
        verify(productRepository, times(1)).findById("id-1");
    }

    @Test
    void update_delegatesToRepository() {
        Product p = new Product();
        p.setProductId("id-1");

        when(productRepository.update(p)).thenReturn(p);

        Product result = productService.update(p);

        assertNotNull(result);
        verify(productRepository, times(1)).update(p);
    }

    @Test
    void delete_delegatesToRepository() {
        when(productRepository.delete("id-1")).thenReturn(true);

        boolean result = productService.delete("id-1");

        assertTrue(result);
        verify(productRepository, times(1)).delete("id-1");
    }
}