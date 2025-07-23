package com.zeven.springcloud.msvc.products.services;

import com.zeven.libs.msvc.commons.entities.Product;
import com.zeven.springcloud.msvc.products.repositories.ProductRepository;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService{

    private final ProductRepository productRepository;
    private final Environment environment;

    ProductServiceImpl(ProductRepository productRepository, Environment environment){
        this.productRepository = productRepository;
        this.environment = environment;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> findAll() {
        return ((List<Product>) productRepository.findAll())
                .stream()
                .peek(product -> product.setPort(Integer.parseInt(Objects.requireNonNull(environment.getProperty("local.server.port"))))).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Product> findById(Long id) {
        return productRepository.findById(id).map(product -> {
            product.setPort(Integer.parseInt(Objects.requireNonNull(this.environment.getProperty("local.server.port"))));
            return product;
        });
    }

    @Override
    @Transactional
    public Product save(Product product) {
        return this.productRepository.save(product);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        this.productRepository.deleteById(id);
    }
}
