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
            newProduct.setCategory(category);
            newProduct.setNombre(product.getName());
            newProduct.setDescripcion(product.getDescription());
            newProduct.setPrecio(BigDecimal.valueOf(product.getPrice()));
            newProduct.setImgUrl(product.getImageUrl());

            TenantMenuProduct productCreated = productRepository.save(newProduct);
            product.setId(productCreated.getId());
            return product;
        }

    @Override
    public List<TenantMenuProductDTO> getProductsByTenantId(Long tenantId) {
        List<TenantMenuProduct> products = productRepository.findAll();
        List<TenantMenuProductDTO> productDTOs = products.stream()
            .filter(product -> product.getCategory() != null && product.getCategory().getTenant() != null &&
                    product.getCategory().getTenant().getId().equals(tenantId))
            .map(product -> TenantMenuProductDTO.builder()
                .id(product.getId())
                .name(product.getNombre())
                .description(product.getDescripcion())
                .price(product.getPrecio() != null ? product.getPrecio().doubleValue() : 0.00)
                .imageUrl(product.getImgUrl())
                .tenantId(product.getCategory().getTenant().getId())
                .categoryId(product.getCategory().getId())
                .categoryName(product.getCategory().getNombre())
                .build())
            .sorted((p1, p2) -> p1.getCategoryName().compareToIgnoreCase(p2.getCategoryName()))
            .toList();
        return productDTOs;
    }
}
