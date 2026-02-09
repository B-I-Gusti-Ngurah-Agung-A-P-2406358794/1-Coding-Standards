package id.ac.ui.cs.advprog.eshop.repository;

import id.ac.ui.cs.advprog.eshop.model.Product;
import org. springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

@Repository
public class ProductRepository {
    private List<Product> productData = new ArrayList<>();

    public Product create(Product product) {
        if (product.getProductId() == null || product.getProductId().isEmpty()) {
            product.setProductId(UUID.randomUUID().toString());
        }
        productData.add(product);
        return product;
    }

    public Product findById(String id) {
        for (Product p : productData) {
            if (p.getProductId().equals(id)) return p;
        }
        return null;
    }

    public Product update(Product updatedProduct) {
        for (int i = 0; i < productData.size(); i++) {
            Product p = productData.get(i);
            if (p.getProductId().equals(updatedProduct.getProductId())) {
                p.setProductName(updatedProduct.getProductName());
                p.setProductQuantity(updatedProduct.getProductQuantity());
                return p;
            }
        }
        return null;
    }

    public boolean delete(String id) {
        for (int i = 0; i < productData.size(); i++) {
            if (productData.get(i).getProductId().equals(id)) {
                productData.remove(i);
                return true;
            }
        }
        return false;
    }

    public Iterator<Product> findAll() {
        return productData.iterator();
    }
}