package com.lealtixservice.service.impl;

import com.lealtixservice.dto.TenantMenuCategoryDTO;
import com.lealtixservice.dto.TenantMenuProductDTO;
import com.lealtixservice.entity.Tenant;
import com.lealtixservice.entity.TenantMenuCategory;
import com.lealtixservice.entity.TenantMenuProduct;
import com.lealtixservice.repository.TenantMenuProductRepository;
import com.lealtixservice.repository.TenantRepository;
import com.lealtixservice.service.TenantMenuProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TenantMenuProductServiceImpl implements TenantMenuProductService {

    @Autowired
    private  TenantMenuProductRepository productRepository;

    @Autowired
    private TenantMenuCategoryServiceImpl categoryService;

    @Autowired
    private TenantRepository tenantRepository;

    @Override
    public TenantMenuProduct save(TenantMenuProduct product) {
        return productRepository.save(product);
    }

    @Override
    public Optional<TenantMenuProduct> findById(Long id) {
        return productRepository.findById(id);
    }

    @Override
    public List<TenantMenuProduct> findAll() {
        return productRepository.findAll();
    }

    @Override
    public void deleteById(Long id) {
        productRepository.deleteById(id);
    }

    @Override
   public TenantMenuProductDTO create(TenantMenuProductDTO product) {

            TenantMenuCategory category = Optional.ofNullable(product.getCategoryId())
                .filter(id -> id > 0)
                .flatMap(categoryService::findById)
                .orElseGet(() -> {
                    Tenant tenant = tenantRepository.findById(product.getTenantId())
                        .orElseThrow(() -> new RuntimeException("No se encontró el tenant con ID: " + product.getTenantId()));
                    TenantMenuCategory newCategory = new TenantMenuCategory();
                    newCategory.setTenant(tenant);
                    newCategory.setNombre(product.getCategoryName());
                    newCategory.setActive(true);
                    newCategory.setCreatedAt(LocalDateTime.now());
                    newCategory.setUpdatedAt(LocalDateTime.now());
                    return categoryService.save(newCategory);
                });

        TenantMenuProduct newProduct = new TenantMenuProduct();
            if(product != null && product.getId() != null){
                Optional<TenantMenuProduct> existingProduct = productRepository.findById(product.getId());
                if(existingProduct.isPresent()){
                    newProduct = existingProduct.get();
                }
            }

            newProduct.setCategory(category);
            if(product.getName() != null && !product.getName().isEmpty()){
                newProduct.setNombre(product.getName());
            }
            if(product.getDescription() != null && !product.getDescription().isEmpty()){
                newProduct.setDescripcion(product.getDescription());
            }
            if(product.getPrice() != null){
                newProduct.setPrecio(product.getPrice());
            }
            if(product.getImageUrl() != null && !product.getImageUrl().isEmpty()){
                newProduct.setImgUrl(product.getImageUrl());
            }
            if(product.getIsActive() != null){
                newProduct.setActive(product.getIsActive());

            }

            newProduct.setCreatedAt(LocalDateTime.now());
            newProduct.setUpdatedAt(LocalDateTime.now());


            TenantMenuProduct productCreated = productRepository.save(newProduct);
            product.setId(productCreated.getId());
            return product;
        }

    @Override
    public List<TenantMenuProductDTO> getProductsByTenantId(Long tenantId) {
        List<TenantMenuProductDTO> products = productRepository.findByCategoryTenantId(tenantId);
        if (products == null) return List.of();
        // El ordenamiento principal ya viene del query por displayOrder
        // Este ordenamiento adicional es por seguridad para casos donde displayOrder sea null
        products.sort((p1, p2) -> {
            Integer order1 = p1.getCategoryDisplayOrder() != null ? p1.getCategoryDisplayOrder() : Integer.MAX_VALUE;
            Integer order2 = p2.getCategoryDisplayOrder() != null ? p2.getCategoryDisplayOrder() : Integer.MAX_VALUE;
            int orderComparison = order1.compareTo(order2);
            if (orderComparison != 0) {
                return orderComparison;
            }
            // Si tienen el mismo displayOrder, ordenar por nombre de categoría
            String c1 = p1.getCategoryName() != null ? p1.getCategoryName() : "";
            String c2 = p2.getCategoryName() != null ? p2.getCategoryName() : "";
            return c1.compareToIgnoreCase(c2);
        });
        return products;
    }
}
