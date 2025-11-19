package com.lealtixservice.service.impl;

import com.lealtixservice.dto.CategoryDTO;
import com.lealtixservice.dto.ReorderCategoryRequest;
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
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

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
            // Obtener el máximo displayOrder actual y sumar 1 para la nueva categoría
            Integer maxDisplayOrder = categoryRepository.findMaxDisplayOrderByTenantId(categoryDTO.getTenantId());
            Integer newDisplayOrder = (maxDisplayOrder != null ? maxDisplayOrder : 0) + 1;

            categoryEntity = TenantMenuCategory.builder()
                    .nombre(categoryDTO.getName())
                    .descripcion(categoryDTO.getDescription())
                    .isActive(categoryDTO.isActive())
                    .tenant(Tenant.builder().id(categoryDTO.getTenantId()).build())
                    .displayOrder(newDisplayOrder)
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
        List<TenantMenuCategory> categoriesEntity = categoryRepository.findByTenantIdOrderByDisplayOrderAsc(tenantId);
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
                        .displayOrder(cat.getDisplayOrder())
                        .build())
                .collect(toList());
    }

    @Override
    @Transactional
    public void reorderCategories(Long tenantId, List<ReorderCategoryRequest> reorderRequests) {
        if (reorderRequests == null || reorderRequests.isEmpty()) {
            throw new IllegalArgumentException("La lista de reordenamiento no puede estar vacía");
        }

        // Obtener todas las categorías del tenant ordenadas
        List<TenantMenuCategory> categories = categoryRepository
                .findByTenantIdOrderByDisplayOrderAsc(tenantId);

        // Crear un mapa para acceso rápido por ID
        Map<Long, TenantMenuCategory> categoryMap = categories.stream()
                .collect(Collectors.toMap(TenantMenuCategory::getId, Function.identity()));

        // Actualizar el displayOrder de cada categoría según la nueva posición
        for (ReorderCategoryRequest request : reorderRequests) {
            TenantMenuCategory category = categoryMap.get(request.getId());
            if (category != null && category.getTenant().getId().equals(tenantId)) {
                category.setDisplayOrder(request.getDisplayOrder());
                category.setUpdatedAt(LocalDateTime.now());
            }
        }

        // Guardar todos los cambios
        categoryRepository.saveAll(categories);
    }
}
