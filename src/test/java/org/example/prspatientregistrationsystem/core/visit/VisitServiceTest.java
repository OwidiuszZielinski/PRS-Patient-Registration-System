package org.example.prspatientregistrationsystem.core.visit;

import org.example.prspatientregistrationsystem.core.mail.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class VisitServiceTest {

    private VisitRepository visitRepository;
    private EmailService emailService;
    private VisitService visitService;

    @BeforeEach
    void setUp() {
        visitRepository = mock(VisitRepository.class);
        emailService = mock(EmailService.class);
        visitService = new VisitService(emailService, visitRepository);
    }

    @Test
    void addVisit_shouldSaveVisitAndScheduleEmail() {
        VisitDto visitDto = VisitDto.builder()
                .doctorName("Dr. Smith")
                .patient("John Doe")
                .date(LocalDateTime.now())
                .description("Routine check-up")
                .build();

        visitService.addVisit(visitDto);

        ArgumentCaptor<VisitEntity> entityCaptor = ArgumentCaptor.forClass(VisitEntity.class);
        verify(visitRepository).save(entityCaptor.capture());

        VisitEntity savedEntity = entityCaptor.getValue();
        assertEquals(visitDto.getDoctorName(), savedEntity.getDoctorName());
        assertEquals(visitDto.getPatient(), savedEntity.getPatient());
        assertEquals(visitDto.getDate(), savedEntity.getDate());
        assertEquals(visitDto.getDescription(), savedEntity.getDescription());

        verify(emailService).scheduleEmailInOneMinute(eq("owi19955@gmail.com"), eq("Visit"), contains("Remember visit"));
    }

    @Test
    void findAll_shouldReturnListOfVisitDtos() {
        VisitEntity visitEntity = new VisitEntity(1L, "Dr. Who", "Patient A", LocalDateTime.now(), "Description");
        when(visitRepository.findAll()).thenReturn(List.of(visitEntity));

        List<VisitDto> visits = visitService.findAll();

        assertEquals(1, visits.size());
        VisitDto dto = visits.get(0);
        assertEquals(visitEntity.getId(), dto.getId());
        assertEquals(visitEntity.getDoctorName(), dto.getDoctorName());
        assertEquals(visitEntity.getPatient(), dto.getPatient());
        assertEquals(visitEntity.getDate(), dto.getDate());
        assertEquals(visitEntity.getDescription(), dto.getDescription());
    }

    @Test
    void delete_shouldCallDeleteById() {
        Long id = 42L;

        visitService.delete(id);

        verify(visitRepository).deleteById(id);
    }

    @Test
    void update_shouldUpdateFieldsAndSave() {
        Long id = 1L;
        VisitEntity existingEntity = new VisitEntity(id, "Old Doctor", "Old Patient", LocalDateTime.now().minusDays(1), "Old Desc");

        VisitDto updatedDto = VisitDto.builder()
                .id(id)
                .doctorName("New Doctor")
                .patient("New Patient")
                .date(LocalDateTime.now())
                .description("New Desc")
                .build();

        when(visitRepository.findById(id)).thenReturn(Optional.of(existingEntity));

        visitService.update(updatedDto);

        assertEquals("New Doctor", existingEntity.getDoctorName());
        assertEquals("New Patient", existingEntity.getPatient());
        assertEquals(updatedDto.getDate(), existingEntity.getDate());
        assertEquals("New Desc", existingEntity.getDescription());

        verify(visitRepository).save(existingEntity);
    }

    @Test
    void update_shouldThrowWhenVisitNotFound() {
        Long id = 999L;
        VisitDto visitDto = VisitDto.builder().id(id).build();
        when(visitRepository.findById(id)).thenReturn(Optional.empty());

        NoSuchElementException ex = assertThrows(NoSuchElementException.class, () -> visitService.update(visitDto));
        assertTrue(ex.getMessage().contains("Visit with id"));
    }
}