package com.lealtixservice.service.impl;

import com.lealtixservice.entity.TenantMenuCategory;
import com.lealtixservice.repository.TenantMenuCategoryRepository;
import com.lealtixservice.service.TenantMenuCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TenantMenuCategoryServiceImpl implements TenantMenuCategoryService {

    private final TenantMenuCategoryRepository categoryRepository;

    @Autowired
    public TenantMenuCategoryServiceImpl(TenantMenuCategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

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
}

