package com.lealtixservice.service.impl;

import com.lealtixservice.dto.CategoryDTO;
import com.lealtixservice.dto.TenantMenuCategoryDTO;
import com.lealtixservice.dto.TenantMenuProductDTO;
import com.lealtixservice.entity.Tenant;
import com.lealtixservice.entity.TenantMenuCategory;
import com.lealtixservice.entity.TenantMenuProduct;
import com.lealtixservice.repository.TenantMenuCategoryRepository;
import com.lealtixservice.repository.TenantMenuProductRepository;
import com.lealtixservice.service.TenantMenuCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@Service
public class TenantMenuCategoryServiceImpl implements TenantMenuCategoryService {

    @Autowired
    private TenantMenuCategoryRepository categoryRepository;

    @Autowired
    private TenantMenuProductRepository productRepository;

    @Override
    public TenantMenuCategory save(TenantMenuCategory category) {
        return categoryRepository.save(category);
    }

    @Override
    public Optional<TenantMenuCategory> findById(Long id) {
        return categoryRepository.findById(id);
    }

    @Override
    public List<TenantMenuCategory> findAll() {
        return categoryRepository.findAll();
    }

    @Override
    public void deleteById(Long id) {
        categoryRepository.deleteById(id);
    }

    @Override
    public List<TenantMenuProductDTO> getByTenantId(Long tenantId) {
        List<TenantMenuCategory> categoryList = categoryRepository.findByTenantId(tenantId);
        if (categoryList == null || categoryList.isEmpty()) {
            return null;
        }
        return categoryList.stream()
                .flatMap(category -> {
                    List<TenantMenuProduct> products = productRepository.findByCategoryId(category.getId());
                    return products.stream().map(product -> TenantMenuProductDTO.builder()
                            .id(product.getId())
                            .categoryId(category.getId())
                            .name(product.getNombre())
                            .description(product.getDescripcion())
                            .price(product.getPrecio() != null ? product.getPrecio() : BigDecimal.ZERO)
                            .imageUrl(product.getImgUrl())
                            .tenantId(category.getTenant().getId())
                            .categoryName(category.getNombre())
                            .categoryDescription(category.getDescripcion())
                            .build());
                })
                .collect(toList());
    }

    @Override
    public TenantMenuCategoryDTO createCategoryProduct(TenantMenuCategoryDTO categoryDTO) {
        if (categoryDTO == null || categoryDTO.getTenantId() == null) {
            throw new IllegalArgumentException("El objeto category o tenantId no puede ser nulo");
        }

        // Buscar si ya existe la categoría
        Optional<TenantMenuCategory> existingCategoryOpt = categoryRepository.findByTenantId(categoryDTO.getTenantId())
                .stream()
                .filter(cat -> cat.getNombre().equalsIgnoreCase(categoryDTO.getName()))
                .findFirst();

        TenantMenuCategory categoryEntity;
        if (existingCategoryOpt.isPresent()) {
            categoryEntity = existingCategoryOpt.get();
            categoryEntity.setUpdatedAt(LocalDateTime.now());
            categoryEntity.setNombre(categoryDTO.getName());
            categoryEntity.setActive(categoryDTO.isActive());
            categoryEntity.setDescripcion(categoryDTO.getDescription());
        } else {
            categoryEntity = TenantMenuCategory.builder()
                    .nombre(categoryDTO.getName())
                    .descripcion(categoryDTO.getDescription())
                    .isActive(categoryDTO.isActive())
                    .tenant(Tenant.builder().id(categoryDTO.getTenantId()).build())
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
        }
        categoryEntity = categoryRepository.save(categoryEntity);

        // Si hay productos en el DTO, crear y asociar
        if (categoryDTO.getProductsDTO() != null && !categoryDTO.getProductsDTO().isEmpty()) {
            for (TenantMenuProductDTO productDTO : categoryDTO.getProductsDTO()) {
                TenantMenuProduct productEntity = TenantMenuProduct.builder()
                        .nombre(productDTO.getName())
                        .descripcion(productDTO.getDescription())
                        .precio(productDTO.getPrice() != null ? productDTO.getPrice() : BigDecimal.ZERO)
                        .imgUrl(productDTO.getImageUrl())
                        .category(categoryEntity)
                        .isActive(true)
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build();
                productRepository.save(productEntity);
            }
        }

        // Mapear productos a DTO
        List<TenantMenuProduct> products = productRepository.findByCategoryId(categoryEntity.getId());
        TenantMenuCategory finalCategoryEntity = categoryEntity;
        List<TenantMenuProductDTO> productsDTO = products.stream()
                .map(product -> TenantMenuProductDTO.builder()
                        .id(product.getId())
                        .name(product.getNombre())
                        .description(product.getDescripcion())
                        .price(product.getPrecio() != null ? product.getPrecio() : BigDecimal.ZERO)
                        .imageUrl(product.getImgUrl())
                        .tenantId(finalCategoryEntity.getTenant().getId())
                        .build())
                .collect(toList());

        return TenantMenuCategoryDTO.builder()
                .id(categoryEntity.getId())
                .tenantId(categoryEntity.getTenant().getId())
                .name(categoryEntity.getNombre())
                .productsDTO(productsDTO)
                .build();
    }

    @Override
    public List<CategoryDTO> getCategoriesByTenantId(Long tenantId) {
        List<TenantMenuCategory> categoriesEntity = categoryRepository.findByTenantId(tenantId);
        if (categoriesEntity == null || categoriesEntity.isEmpty()) {
            return null;
        }
        return categoriesEntity.stream()
                .map(cat -> CategoryDTO.builder()
                        .categoryId(cat.getId())
                        .categoryName(cat.getNombre())
                        .categoryDescription(cat.getDescripcion())
                        .isActive(cat.isActive())
                        .tenantId(cat.getTenant().getId())
                        .build())
                .collect(toList());
    }
}
