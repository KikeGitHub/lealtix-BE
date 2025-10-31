package com.lealtixservice.controller;

import com.lealtixservice.dto.CategoryDTO;
import com.lealtixservice.dto.GenericResponse;
import com.lealtixservice.dto.TenantMenuCategoryDTO;
import com.lealtixservice.dto.TenantMenuProductDTO;
import com.lealtixservice.entity.TenantMenuCategory;
import com.lealtixservice.service.TenantMenuCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
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

    @Operation(summary = "Obtener una categoría por tenantId")
    @GetMapping("/tenant/{tenantId}")
    public ResponseEntity<GenericResponse> getByTenantId(@PathVariable Long tenantId) {
        try{
             List<TenantMenuProductDTO> menuCategoryList = categoryService.getByTenantId(tenantId);
             if(menuCategoryList != null && !menuCategoryList.isEmpty()){
                 return ResponseEntity.ok(new GenericResponse(200, "SUCCESS", menuCategoryList));
             }else{
                    return ResponseEntity.ok(new GenericResponse(404, "No se encontró la categoría para el tenantId proporcionado", null));
             }
        }catch (Exception e){
            return ResponseEntity.ok(new GenericResponse(500, "Error interno del servidor", null));
        }
    }

    @Operation(summary = "Obtener diferentes  categorías por tenantId")
    @GetMapping("/categories/{tenantId}")
    public ResponseEntity<GenericResponse> getCategoriesByTenantId(@PathVariable Long tenantId) {
        try{
            List<CategoryDTO> distinctCategories = categoryService.getCategoriesByTenantId(tenantId);
            if(distinctCategories != null && !distinctCategories.isEmpty()){
                return ResponseEntity.ok(new GenericResponse(200, "SUCCESS", distinctCategories));
            }else{
                return ResponseEntity.ok(new GenericResponse(404, "No se encontró la categoría para el tenantId proporcionado", null));
            }
        }catch (Exception e){
            return ResponseEntity.ok(new GenericResponse(500, "Error interno del servidor", null));
        }
    }

    @Operation(summary = "Crear una nueva categoría")
    @PostMapping
    public ResponseEntity<GenericResponse> createCategoryProduct(@RequestBody TenantMenuCategoryDTO category) {
        try{
            TenantMenuCategoryDTO resp = categoryService.createCategoryProduct(category);
            if(resp != null){
                return ResponseEntity.ok(new GenericResponse(201, "Categoría creada exitosamente", resp));
            }else{
                return ResponseEntity.ok(new GenericResponse(400, "No se pudo crear la categoría", null));
            }
        }catch (Exception e){
            log.error("Error al crear la categoría: ", e);
            return ResponseEntity.ok(new GenericResponse(500, "Error interno del servidor", null));
        }
    }

    @Operation(summary = "Eliminar una categoría por ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        categoryService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}

