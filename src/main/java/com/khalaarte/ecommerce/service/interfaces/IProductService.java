package com.khalaarte.ecommerce.service.interfaces;

import com.khalaarte.ecommerce.model.Product;

import java.util.List;
import java.util.Optional;

public interface IProductService {

    Optional<Product> getProductById(Long id);
    Optional<Product> findByName(String name);
    List<Product> getAllProducts();

    Product createProduct(Product product);
    Product updateProduct(Product product);

    void deleteProductById(Long id);
}
