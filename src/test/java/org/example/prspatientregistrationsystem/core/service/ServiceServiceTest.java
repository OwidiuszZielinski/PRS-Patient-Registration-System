package org.example.prspatientregistrationsystem.core.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ServiceServiceTest {
    @Mock ServiceRepository serviceRepository;
    @InjectMocks ServiceService serviceService;

    @BeforeEach
    void setUp() { MockitoAnnotations.openMocks(this); }

    @Test void testFindAll() {
        when(serviceRepository.findAll()).thenReturn(List.of());
        assertNotNull(serviceService.findAll());
    }
    @Test void testFindAllNotEmpty() {
        ServiceEntity entity = ServiceEntity.builder().id(1L).build();
        when(serviceRepository.findAll()).thenReturn(List.of(entity));
        var result = serviceService.findAll();
        assertEquals(1, result.size());
    }
    @Test void testFindById() {
        ServiceEntity entity = ServiceEntity.builder().id(1L).build();
        when(serviceRepository.findById(1L)).thenReturn(Optional.of(entity));
        assertNotNull(serviceService.findById(1L));
    }
    @Test void testFindByIdThrows() {
        when(serviceRepository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> serviceService.findById(2L));
    }
    @Test void testFindAllReturnsList() {
        when(serviceRepository.findAll()).thenReturn(List.of(ServiceEntity.builder().id(1L).build(), ServiceEntity.builder().id(2L).build()));
        var result = serviceService.findAll();
        assertEquals(2, result.size());
    }
} 