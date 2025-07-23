package com.zeven.springcloud.msvc.items.controllers;

import com.zeven.libs.msvc.commons.entities.Product;
import com.zeven.springcloud.msvc.items.models.Item;
import com.zeven.springcloud.msvc.items.services.ItemService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@RestController
// Con la anotación @RefreshScope conseguimos que el componente donde esté refresque su configuración sin tener que refrescar el microservicio
@RefreshScope
public class ItemController {

    private final Logger logger = LoggerFactory.getLogger(ItemController.class);

    @Value("${configuration.text}")
    private String text;

    @Autowired
    private Environment env;

    private final ItemService itemService;
    private final CircuitBreakerFactory cBreakerFactory;


    /* Para usar feign */
    /*public ItemController(@Qualifier("itemServiceFeign") ItemService itemService,
                          CircuitBreakerFactory cBreakerFactory) {
        this.itemService = itemService;
        this.cBreakerFactory = cBreakerFactory;
    }*/

    /* Para usar WebClient */
    public ItemController(@Qualifier("itemServiceWebClient") ItemService itemService,
                          CircuitBreakerFactory cBreakerFactory) {
        this.itemService = itemService;
        this.cBreakerFactory = cBreakerFactory;
    }

    @GetMapping("/fetch-configs")
    public ResponseEntity<?> fetchConfigs(@Value("${server.port}") String port){
        Map<String, String> json = new HashMap<>();
        json.put("text", this.text);
        json.put("port", port);

        String portMsg = "Puerto: " + port;
        logger.info(portMsg);

        String textMsg = "Texto: " + this.text;
        logger.info(textMsg);

        if (env.getActiveProfiles().length > 0 && env.getActiveProfiles()[0].equals("dev")) {
            json.put("autor.nombre", env.getProperty("configuration.autor.name"));
            json.put("autor.email", env.getProperty("configuration.autor.email"));
        }

        return ResponseEntity.ok(json);
    }

    @GetMapping
    public List<Item> list(@RequestParam(name = "name", required = false) String name,
                           @RequestHeader(name="token-request", required = false) String token) {
        String requestNameMsg = "Request Header: " + name;
        String tokenMsg = "Token: " + token;

        logger.info("LLamada a método del controller ItemController::list");
        logger.info(requestNameMsg);
        logger.info(tokenMsg);

        return this.itemService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> details(@PathVariable Long id) {
        Optional<Item> itemOptional = cBreakerFactory.create("items").run(() ->
            this.itemService.findById(id),
                e -> {
                    logger.error(e.getMessage());
                    return generateItem();
                }
        );

        if (itemOptional.isPresent()) {
            return ResponseEntity.ok(itemOptional.get());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("message", "Item not found"));
    }

    @CircuitBreaker(name = "items", fallbackMethod = "getFallbackMethodProduct2")
    @GetMapping("/details2/{id}")
    public ResponseEntity<?> details2(@PathVariable Long id) {
        Optional<Item> itemOptional = this.itemService.findById(id);

        if (itemOptional.isPresent()) {
            return ResponseEntity.ok(itemOptional.get());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("message", "Item not found"));
    }

    public ResponseEntity<?> getFallbackMethodProduct2 (Throwable throwable) {
        String msg = "Con anotación @CircuitBreaker: " + throwable.getMessage();
        logger.error(msg);

        Product product = new Product();
        product.setCreatedAt(LocalDate.now());
        product.setId(1L);
        product.setName("Cámara Sony");
        product.setPrice(500.00);
        return ResponseEntity.ok(new Item(product, 5));

    }

    @TimeLimiter(name = "items", fallbackMethod = "getFallbackMethodProduct3")
    @GetMapping("/details3/{id}")
    public CompletableFuture<?> details3(@PathVariable Long id) {
        return CompletableFuture.supplyAsync(() -> {
            Optional<Item> itemOptional = this.itemService.findById(id);

            if (itemOptional.isPresent()) {
                return ResponseEntity.ok(itemOptional.get());
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("message", "Item not found"));
        });
    }

    public CompletableFuture<?> getFallbackMethodProduct3 (Throwable throwable) {
        String msg = "Con anotación @TimeLimiter: " + throwable.getMessage();
        logger.error(msg);
        return CompletableFuture.supplyAsync(() -> {
            Product product = new Product();
            product.setCreatedAt(LocalDate.now());
            product.setId(1L);
            product.setName("Cámara Sony");
            product.setPrice(500.00);
            return ResponseEntity.ok(new Item(product, 5));
        });
    }

    @CircuitBreaker(name = "items", fallbackMethod = "getFallbackMethodProduct4")
    @TimeLimiter(name = "items", fallbackMethod = "getFallbackMethodProduct4")
    @GetMapping("/details4/{id}")
    public CompletableFuture<?> details4(@PathVariable Long id) {
        return CompletableFuture.supplyAsync(() -> {
            Optional<Item> itemOptional = this.itemService.findById(id);

            if (itemOptional.isPresent()) {
                return ResponseEntity.ok(itemOptional.get());
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("message", "Item not found"));
        });
    }

    public CompletableFuture<?> getFallbackMethodProduct4 (Throwable throwable) {
        String msg = "Con anotaciones @TimeLimiter y @CircuitBreaker: " + throwable.getMessage();
        logger.error(msg);
        return CompletableFuture.supplyAsync(() -> {
            Product product = new Product();
            product.setCreatedAt(LocalDate.now());
            product.setId(1L);
            product.setName("Cámara Sony");
            product.setPrice(500.00);
            return ResponseEntity.ok(new Item(product, 5));
        });
    }

    private Optional<Item> generateItem() {
        Product product = new Product();
        product.setCreatedAt(LocalDate.now());
        product.setId(1L);
        product.setName("Cámara Sony");
        product.setPrice(500.00);
        return Optional.of(new Item(product, 5));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Product create(@RequestBody Product product){
        logger.info("Producto creando: {}", product);
        return this.itemService.save(product);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public Product update (@RequestBody Product product, @PathVariable Long id){
        logger.info("Producto actualizando: {}", product);
        return this.itemService.update(product, id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        logger.info("Producto eliminado con id : {}", id);
        this.itemService.delete(id);
    }
}
