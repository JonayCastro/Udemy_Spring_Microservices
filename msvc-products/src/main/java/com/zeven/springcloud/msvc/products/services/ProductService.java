package com.zeven.springcloud.msvc.products.services;



import com.zeven.libs.msvc.commons.entities.Product;

import java.util.List;
import java.util.Optional;

public interface ProductService {

    List<Product> findAll();
    Optional<Product> findById(Long id);
    Product save(Product product);
    void deleteById(Long id);
}
