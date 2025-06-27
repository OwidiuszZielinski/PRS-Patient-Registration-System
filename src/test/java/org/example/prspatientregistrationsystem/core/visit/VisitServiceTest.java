package org.example.prspatientregistrationsystem.core.visit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class VisitServiceTest {
    @Mock VisitRepository visitRepository;
    @Mock org.example.prspatientregistrationsystem.core.mail.EmailService emailService;
    @InjectMocks VisitService visitService;

    @BeforeEach
    void setUp() { MockitoAnnotations.openMocks(this); }

    @Test void testFindAll() {
        when(visitRepository.findAll()).thenReturn(List.of());
        assertNotNull(visitService.findAll());
    }
    @Test void testDelete() {
        doNothing().when(visitRepository).deleteById(1L);
        visitService.delete(1L);
        verify(visitRepository).deleteById(1L);
    }
    @Test void testAddVisit() {
        VisitDto dto = VisitDto.builder().build();
        when(visitRepository.save(any())).thenReturn(new VisitEntity());
        visitService.addVisit(dto);
        verify(visitRepository).save(any());
    }
    @Test void testUpdate() {
        VisitDto dto = VisitDto.builder().id(1L).build();
        VisitEntity entity = VisitEntity.builder().id(1L).build();
        when(visitRepository.findById(1L)).thenReturn(java.util.Optional.of(entity));
        when(visitRepository.save(any())).thenReturn(entity);
        visitService.update(dto);
        verify(visitRepository).save(any());
    }
    @Test void testDeleteNotFound() {
        doThrow(new RuntimeException()).when(visitRepository).deleteById(99L);
        assertThrows(Exception.class, () -> visitService.delete(99L));
    }
    @Test void testFindAllEmpty() {
        when(visitRepository.findAll()).thenReturn(List.of());
        assertTrue(visitService.findAll().isEmpty());
    }
    @Test void testUpdateFields() {
        VisitDto dto = VisitDto.builder().id(2L).build();
        VisitEntity entity = VisitEntity.builder().id(2L).build();
        when(visitRepository.findById(2L)).thenReturn(java.util.Optional.of(entity));
        visitService.update(dto);
        verify(visitRepository).save(any());
    }
    @Test void testAddVisitNull() {
        assertThrows(Exception.class, () -> visitService.addVisit(null));
    }
    @Test void testDeleteById() {
        doNothing().when(visitRepository).deleteById(5L);
        visitService.delete(5L);
        verify(visitRepository).deleteById(5L);
    }
    @Test void testFindByIdNotFound() {
        when(visitRepository.findById(123L)).thenReturn(java.util.Optional.empty());
        assertThrows(Exception.class, () -> visitService.update(VisitDto.builder().id(123L).build()));
    }
} 