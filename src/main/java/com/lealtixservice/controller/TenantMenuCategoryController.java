package com.lealtixservice.controller;

import com.lealtixservice.entity.TenantMenuCategory;
import com.lealtixservice.service.TenantMenuCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tenant-menu-categories")
@Tag(name = "TenantMenuCategory", description = "Operaciones sobre categorías del menú del tenant")
public class TenantMenuCategoryController {

    private final TenantMenuCategoryService categoryService;

    @Autowired
    public TenantMenuCategoryController(TenantMenuCategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @Operation(summary = "Obtener todas las categorías")
    @GetMapping
    public ResponseEntity<List<TenantMenuCategory>> getAll() {
        return ResponseEntity.ok(categoryService.findAll());
    }

    @Operation(summary = "Obtener una categoría por ID")
    @GetMapping("/{id}")
    public ResponseEntity<TenantMenuCategory> getById(@PathVariable Long id) {
        return categoryService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Crear una nueva categoría")
    @PostMapping
    public ResponseEntity<TenantMenuCategory> create(@RequestBody TenantMenuCategory category) {
        return ResponseEntity.ok(categoryService.save(category));
    }

    @Operation(summary = "Eliminar una categoría por ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        categoryService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}

