package org.example.prspatientregistrationsystem.core.visit;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class VisitDtoTest {
    @Test void testBuilderAndFields() {
        VisitDto dto = VisitDto.builder().id(1L).doctorName("Dr X").patient("Pat Y").build();
        assertEquals(1L, dto.getId());
        assertEquals("Dr X", dto.getDoctorName());
        assertEquals("Pat Y", dto.getPatient());
    }
    @Test void testMapToEntity() {
        VisitDto dto = VisitDto.builder().id(2L).doctorName("A").build();
        VisitEntity entity = VisitDto.mapToEntity(dto);
        assertEquals(dto.getDoctorName(), entity.getDoctorName());
    }
    @Test void testMapToVisitDto() {
        VisitEntity entity = VisitEntity.builder().id(3L).doctorName("B").build();
        VisitDto dto = VisitDto.mapToVisitDto(entity);
        assertEquals(entity.getDoctorName(), dto.getDoctorName());
    }
    @Test void testNullServices() {
        VisitDto dto = VisitDto.builder().selectedServices(null).build();
        VisitEntity entity = VisitDto.mapToEntity(dto);
        assertNull(entity.getSelectedServices());
    }
    @Test void testTotalCost() {
        VisitDto dto = VisitDto.builder().totalCost(BigDecimal.TEN).build();
        assertEquals(BigDecimal.TEN, dto.getTotalCost());
    }
    @Test void testDateField() {
        LocalDateTime now = LocalDateTime.now();
        VisitDto dto = VisitDto.builder().date(now).build();
        assertEquals(now, dto.getDate());
    }
    @Test void testDescription() {
        VisitDto dto = VisitDto.builder().description("desc").build();
        assertEquals("desc", dto.getDescription());
    }
    @Test void testSelectedServices() {
        List<org.example.prspatientregistrationsystem.core.service.ServiceDto> services = List.of();
        VisitDto dto = VisitDto.builder().selectedServices(services).build();
        assertEquals(services, dto.getSelectedServices());
    }
    @Test void testIdField() {
        VisitDto dto = VisitDto.builder().id(99L).build();
        assertEquals(99L, dto.getId());
    }
    @Test void testPatientField() {
        VisitDto dto = VisitDto.builder().patient("Test").build();
        assertEquals("Test", dto.getPatient());
    }
} 