package com.lealtixservice.service.impl;

import com.lealtixservice.entity.TenantMenuCategory;
import com.lealtixservice.repository.TenantMenuCategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TenantMenuCategoryServiceImplTest {

    @Mock
    private TenantMenuCategoryRepository categoryRepository;

    @InjectMocks
    private TenantMenuCategoryServiceImpl service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void save_returnsSavedCategory() {
        TenantMenuCategory category = new TenantMenuCategory();
        when(categoryRepository.save(category)).thenReturn(category);
        assertEquals(category, service.save(category));
    }

    @Test
    void findById_found() {
        TenantMenuCategory category = new TenantMenuCategory();
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        assertTrue(service.findById(1L).isPresent());
    }

    @Test
    void findById_notFound() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());
        assertFalse(service.findById(1L).isPresent());
    }

    @Test
    void findAll_returnsList() {
        List<TenantMenuCategory> categories = Arrays.asList(new TenantMenuCategory(), new TenantMenuCategory());
        when(categoryRepository.findAll()).thenReturn(categories);
        assertEquals(2, service.findAll().size());
    }

    @Test
    void deleteById_callsRepository() {
        doNothing().when(categoryRepository).deleteById(1L);
        service.deleteById(1L);
        verify(categoryRepository).deleteById(1L);
    }
}

