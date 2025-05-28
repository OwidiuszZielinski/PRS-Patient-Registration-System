package org.example.prspatientregistrationsystem.core.doctor;

import org.example.prspatientregistrationsystem.core.doctor.commad.DoctorAddCommand;
import org.example.prspatientregistrationsystem.core.doctor.commad.DoctorUpdateCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class DoctorServiceTest {

    @Mock
    private DoctorRepository doctorRepository;

    @InjectMocks
    private DoctorService doctorService;

    private Doctor doctor;

    @BeforeEach
    void setUp() {
        doctor = Doctor.builder()
                .doctorId(4L)
                .firstName("Roman")
                .lastName("Kidler")
                .licenseNumber("DOC476576")
                .officeId(4L)
                .doctorSchedules(List.of())
                .build();
    }

    @Test
    void shouldAddDoctor() {
        DoctorAddCommand command = new DoctorAddCommand(14L, "Roman", "Kidler", "DOC476576", List.of());

        doctorService.add(command);

        ArgumentCaptor<Doctor> captor = ArgumentCaptor.forClass(Doctor.class);
        verify(doctorRepository, times(1)).save(captor.capture());

        Doctor saved = captor.getValue();
        assertEquals("Roman", saved.getFirstName());
        assertEquals("Kidler", saved.getLastName());
        assertEquals("DOC476576", saved.getLicenseNumber());
        assertEquals(14L, saved.getOfficeId());
    }

    @Test
    void shouldFindAllDoctors() {
        when(doctorRepository.findAll()).thenReturn(List.of(doctor));

        List<DoctorDto> result = doctorService.findAll();

        assertEquals(1, result.size());
        DoctorDto dto = result.get(0);
        assertEquals("Roman", dto.firstName());
        assertTrue(dto.doctorSchedules().isEmpty());
    }

    @Test
    void shouldFindDoctorById() {
        when(doctorRepository.findById(4L)).thenReturn(Optional.of(doctor));

        DoctorDto result = doctorService.findById(4L);

        assertEquals("Kidler", result.lastName());
        assertEquals(4L, result.id());
        assertEquals("DOC476576", result.licenseNumber());
        assertTrue(result.doctorSchedules().isEmpty());
    }

    @Test
    void shouldThrowWhenDoctorNotFoundById() {
        when(doctorRepository.findById(4L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> doctorService.findById(4L));
    }

    @Test
    void shouldReturnAllDoctorFullNames() {
        when(doctorRepository.findAll()).thenReturn(List.of(doctor));

        List<String> result = doctorService.findAllFullNames();

        assertEquals(List.of("Roman Kidler"), result);
    }

    @Test
    void shouldDeleteDoctorById() {
        doctorService.delete(4L);

        verify(doctorRepository).deleteById(4L);
    }

    @Test
    void shouldUpdateDoctor() {
        when(doctorRepository.findById(4L)).thenReturn(Optional.of(doctor));
        DoctorUpdateCommand command = new DoctorUpdateCommand(4L, 17L, "Roman", "Kinder", "DOC474676");

        doctorService.update(command);

        verify(doctorRepository).save(argThat(updated ->
                updated.getLastName().equals("Kinder") &&
                        updated.getLicenseNumber().equals("DOC474676") &&
                        updated.getOfficeId().equals(17L)
        ));
    }

    @Test
    void shouldThrowWhenUpdatingNonexistentDoctor() {
        when(doctorRepository.findById(4L)).thenReturn(Optional.empty());
        DoctorUpdateCommand command = new DoctorUpdateCommand(4L, 17L, "Roman", "Kinder", "DOC474676");

        assertThrows(NoSuchElementException.class, () -> doctorService.update(command));
    }
}