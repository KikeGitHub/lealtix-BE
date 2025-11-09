package com.lealtixservice.controller;

import com.lealtixservice.dto.GenericResponse;
import com.lealtixservice.dto.GenericResponseProd;
import com.lealtixservice.dto.TenantMenuCategoryDTO;
import com.lealtixservice.dto.TenantMenuProductDTO;
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

    @Operation(summary = "Obtener un producto por tenantId")
    @GetMapping("/tenant/{tenantId}")
    public ResponseEntity<GenericResponseProd> getProductsByTenantId(@PathVariable Long tenantId) {
        try {
            List<TenantMenuProductDTO>  products = productService.getProductsByTenantId(tenantId);
            if (products != null && !products.isEmpty()) {
                return ResponseEntity.ok(new GenericResponseProd(200, "Productos obtenidos exitosamente", products, products.size()));
            } else {
                return ResponseEntity.ok(new GenericResponseProd(400, "No se pudo obtener  Productos", null, 0));
            }
        } catch (Exception e) {
            return ResponseEntity.ok(new GenericResponseProd(500, "Error interno del servidor", null, 0));
        }
    }

    @Operation(summary = "Crear un nuevo producto")
    @PostMapping
    public ResponseEntity<GenericResponse> create(@RequestBody TenantMenuProductDTO product) {
        try{
            TenantMenuProductDTO resp = productService.create(product);
            if(resp != null){
                return ResponseEntity.ok(new GenericResponse(201, "Producto creado exitosamente", resp));
            }else{
                return ResponseEntity.ok(new GenericResponse(400, "No se pudo crear la Producto", null));
            }
        }catch (Exception e){
            return ResponseEntity.ok(new GenericResponse(500, "Error interno del servidor", null));
        }
    }

    @Operation(summary = "Eliminar un producto por ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}

