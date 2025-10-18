package com.lealtixservice.controller;

import com.lealtixservice.entity.TenantMenuCategory;
import com.lealtixservice.service.TenantMenuCategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TenantMenuCategoryControllerTest {

    @Mock
    private TenantMenuCategoryService categoryService;

    @InjectMocks
    private TenantMenuCategoryController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAll_returnsList() {
        List<TenantMenuCategory> categories = Arrays.asList(new TenantMenuCategory(), new TenantMenuCategory());
        when(categoryService.findAll()).thenReturn(categories);
        ResponseEntity<List<TenantMenuCategory>> response = controller.getAll();
        assertEquals(2, response.getBody().size());
        verify(categoryService).findAll();
    }

    @Test
    void getById_found() {
        TenantMenuCategory category = new TenantMenuCategory();
        when(categoryService.findById(1L)).thenReturn(Optional.of(category));
        ResponseEntity<TenantMenuCategory> response = controller.getById(1L);
        assertEquals(category, response.getBody());
    }

    @Test
    void getById_notFound() {
        when(categoryService.findById(1L)).thenReturn(Optional.empty());
        ResponseEntity<TenantMenuCategory> response = controller.getById(1L);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    void delete_callsService() {
        doNothing().when(categoryService).deleteById(1L);
        ResponseEntity<Void> response = controller.delete(1L);
        assertEquals(204, response.getStatusCodeValue());
        verify(categoryService).deleteById(1L);
    }
}

