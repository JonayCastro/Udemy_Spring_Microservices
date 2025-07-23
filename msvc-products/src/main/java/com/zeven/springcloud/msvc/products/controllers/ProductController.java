package com.zeven.springcloud.msvc.products.controllers;

import com.zeven.libs.msvc.commons.entities.Product;
import com.zeven.springcloud.msvc.products.services.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@RestController
public class ProductController {

    private final Logger logger = LoggerFactory.getLogger(ProductController.class);


    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public List<Product> list(@RequestHeader(name="message-request", required = false) String message){
        logger.info("Método lista en ProductController");
        logger.info("message: {}", message);
        return this.productService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> details(@PathVariable Long id) throws InterruptedException {
        logger.info("Detalles para producto con id {}", id);

        if (id.equals(10L)) {
            throw new IllegalStateException("Error para generado para probar CircuitBreaker");
        }

        if (id.equals(7L)){
            TimeUnit.SECONDS.sleep(3L);
        }

        ResponseEntity<Product> response = ResponseEntity.notFound().build();
        Optional<Product> productOptional = this.productService.findById(id);
        if (productOptional.isPresent()){
            response = ResponseEntity.ok(productOptional.orElseThrow());
        }
        return response;
    }

    @PostMapping
    public ResponseEntity<Product> create(@RequestBody Product product) {
        Product productCreated = this.productService.save(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(productCreated);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update (@PathVariable Long id, @RequestBody Product product){
        Optional<Product> productOptional = this.productService.findById(id);
        if (productOptional.isPresent()){
            Product productDb = productOptional.orElseThrow();
            productDb.setName(product.getName());
            productDb.setPrice(product.getPrice());
            productDb.setCreatedAt(product.getCreatedAt());
            return ResponseEntity.status(HttpStatus.CREATED).body(this.productService.save(productDb));
        }
        return ResponseEntity.notFound().build();

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete (@PathVariable Long id) {
        logger.info("Producto eliminado con id {}", id);

        Optional<Product> productOptional = this.productService.findById(id);
        if (productOptional.isPresent()){
            this.productService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }


}
