package com.lealtixservice.controller;

import com.lealtixservice.entity.TenantMenuProduct;
import com.lealtixservice.service.TenantMenuProductService;
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

class TenantMenuProductControllerTest {

    @Mock
    private TenantMenuProductService productService;

    @InjectMocks
    private TenantMenuProductController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAll_returnsList() {
        List<TenantMenuProduct> products = Arrays.asList(new TenantMenuProduct(), new TenantMenuProduct());
        when(productService.findAll()).thenReturn(products);
        ResponseEntity<List<TenantMenuProduct>> response = controller.getAll();
        assertEquals(2, response.getBody().size());
        verify(productService).findAll();
    }

    @Test
    void getById_found() {
        TenantMenuProduct product = new TenantMenuProduct();
        when(productService.findById(1L)).thenReturn(Optional.of(product));
        ResponseEntity<TenantMenuProduct> response = controller.getById(1L);
        assertEquals(product, response.getBody());
    }

    @Test
    void getById_notFound() {
        when(productService.findById(1L)).thenReturn(Optional.empty());
        ResponseEntity<TenantMenuProduct> response = controller.getById(1L);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    void create_returnsSaved() {
        TenantMenuProduct product = new TenantMenuProduct();
        when(productService.save(product)).thenReturn(product);
        ResponseEntity<TenantMenuProduct> response = controller.create(product);
        assertEquals(product, response.getBody());
    }

    @Test
    void delete_callsService() {
        doNothing().when(productService).deleteById(1L);
        ResponseEntity<Void> response = controller.delete(1L);
        assertEquals(204, response.getStatusCodeValue());
        verify(productService).deleteById(1L);
    }
}

