package com.lealtixservice.controller;

import com.lealtixservice.entity.TenantMenuProduct;
import com.lealtixservice.service.TenantMenuProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tenant-menu-products")
@Tag(name = "TenantMenuProduct", description = "Operaciones sobre productos del menú del tenant")
public class TenantMenuProductController {

    private final TenantMenuProductService productService;

    @Autowired
    public TenantMenuProductController(TenantMenuProductService productService) {
        this.productService = productService;
    }

    @Operation(summary = "Obtener todos los productos")
    @GetMapping
    public ResponseEntity<List<TenantMenuProduct>> getAll() {
        return ResponseEntity.ok(productService.findAll());
    }

    @Operation(summary = "Obtener un producto por ID")
    @GetMapping("/{id}")
    public ResponseEntity<TenantMenuProduct> getById(@PathVariable Long id) {
        return productService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Crear un nuevo producto")
    @PostMapping
    public ResponseEntity<TenantMenuProduct> create(@RequestBody TenantMenuProduct product) {
        return ResponseEntity.ok(productService.save(product));
    }

    @Operation(summary = "Eliminar un producto por ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}

