package com.zeven.springcloud.msvc.items.services;

import com.zeven.libs.msvc.commons.entities.Product;
import com.zeven.springcloud.msvc.items.clients.ProductFeignClient;
import com.zeven.springcloud.msvc.items.models.Item;
import feign.FeignException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class ItemServiceFeign implements ItemService{

    @Autowired
    private ProductFeignClient productFeignClient;


    @Override
    public List<Item> findAll() {
        return this.productFeignClient.findAll()
                .stream()
                .map(product -> new Item(product, new Random()
                        .nextInt(10+1)))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Item> findById(Long id) {
        try {
            Product product = this.productFeignClient.details(id);
            return Optional.of(new Item(product, new Random()
                    .nextInt(10+1)));
        } catch (FeignException fe){
            return Optional.empty();
        }
    }

    @Override
    public Product save(Product product) {
        return this.productFeignClient.create(product);
    }

    @Override
    public Product update(Product product, Long id) {
        return this.productFeignClient.update(product, id);
    }

    @Override
    public void delete(Long id) {
        this.productFeignClient.delete(id);
    }
}
