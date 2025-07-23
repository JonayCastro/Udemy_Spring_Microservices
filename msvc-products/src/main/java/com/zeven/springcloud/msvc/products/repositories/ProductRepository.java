package com.zeven.springcloud.msvc.products.repositories;

import com.zeven.libs.msvc.commons.entities.Product;
import org.springframework.data.repository.CrudRepository;

public interface ProductRepository extends CrudRepository<Product, Long> {

    // Custom query methods can be defined here if needed
    // For example, findByName(String name) or findByPriceBetween(Double min, Double max)
}
