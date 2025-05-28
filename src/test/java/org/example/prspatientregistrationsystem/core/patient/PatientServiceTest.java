package org.example.prspatientregistrationsystem.core.patient;

import org.example.prspatientregistrationsystem.core.patient.command.PatientUpdateCommand;
import org.example.prspatientregistrationsystem.core.patient.dto.PatientDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PatientServiceTest {

    @Mock
    private PatientRepository patientRepository;

    @InjectMocks
    private PatientService patientService;

    @Test
    void findAll_shouldReturnListOfPatientDto() {
        var patient1 = new Patient(1L, "Jan", "Kowalski", "jan.kowalski@example.com", "123456789", "1234567890", LocalDate.of(1990,1,1));
        var patient2 = new Patient(2L, "Anna", "Nowak", "anna.nowak@example.com", "987654321", "0987654321", LocalDate.of(1985,5,5));
        when(patientRepository.findAll()).thenReturn(List.of(patient1, patient2));

        List<PatientDto> result = patientService.findAll();

        assertEquals(2, result.size());
        assertEquals("Jan", result.get(0).firstname());
        assertEquals("Anna", result.get(1).firstname());
        verify(patientRepository).findAll();
    }

    @Test
    void save_shouldThrowException_whenIdentificationNumberIsEmpty() {
        var dto = PatientDto.builder()
                .id(null)
                .firstname("Test")
                .lastname("User")
                .email("test@example.com")
                .phoneNumber("000000000")
                .identificationNumber("")
                .birthDate(LocalDate.now())
                .build();

        RuntimeException ex = assertThrows(RuntimeException.class, () -> patientService.save(dto));
        assertEquals("identification number is null", ex.getMessage());
        verify(patientRepository, never()).save(any());
    }

    @Test
    void save_shouldCallRepositorySave_whenIdentificationNumberIsNotEmpty() {
        var dto = PatientDto.builder()
                .id(null)
                .firstname("Test")
                .lastname("User")
                .email("test@example.com")
                .phoneNumber("000000000")
                .identificationNumber("ID123")
                .birthDate(LocalDate.now())
                .build();

        patientService.save(dto);

        verify(patientRepository).save(any(Patient.class));
    }

    @Test
    void findById_shouldReturnPatientDto_whenPatientExists() {
        var patient = new Patient(1L, "Jan", "Kowalski", "jan.k@example.com", "123", "ID123", LocalDate.of(1990,1,1));
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));

        PatientDto result = patientService.findById(1L);

        assertEquals("Jan", result.firstname());
        verify(patientRepository).findById(1L);
    }

    @Test
    void findById_shouldThrowNoSuchElementException_whenPatientNotFound() {
        when(patientRepository.findById(1L)).thenReturn(Optional.empty());

        NoSuchElementException ex = assertThrows(NoSuchElementException.class, () -> patientService.findById(1L));
        assertTrue(ex.getMessage().contains("Patient with id 1 not found"));
    }

    @Test
    void delete_shouldCallRepositoryDeleteById() {
        Long id = 5L;
        patientService.delete(id);
        verify(patientRepository).deleteById(id);
    }

    @Test
    void update_shouldModifyPatientAndSave() {
        var existingPatient = new Patient(1L, "Jan", "Kowalski", "jan.k@example.com", "123", "ID123", LocalDate.of(1990,1,1));
        when(patientRepository.findById(1L)).thenReturn(Optional.of(existingPatient));

        PatientUpdateCommand cmd = mock(PatientUpdateCommand.class);
        when(cmd.id()).thenReturn(1L);
        when(cmd.lastname()).thenReturn("Nowak");
        when(cmd.email()).thenReturn("nowak@example.com");
        when(cmd.phoneNumber()).thenReturn("999999999");

        patientService.update(cmd);

        assertEquals("Nowak", existingPatient.getLastName());
        assertEquals("nowak@example.com", existingPatient.getEmail());
        assertEquals("999999999", existingPatient.getPhoneNumber());

        verify(patientRepository).save(existingPatient);
    }
}