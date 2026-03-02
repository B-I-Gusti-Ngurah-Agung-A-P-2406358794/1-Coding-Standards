package id.ac.ui.cs.advprog.eshop.repository;

import id.ac.ui.cs.advprog.eshop.model.Product;

import java.util.Iterator;

public interface ProductRepository {
    Product create(Product product);

    Product findById(String id);

    Product update(Product updatedProduct);

    boolean delete(String id);

    Iterator<Product> findAll();
}