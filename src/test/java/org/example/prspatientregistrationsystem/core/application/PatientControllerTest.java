package org.example.prspatientregistrationsystem.core.application;

import org.example.prspatientregistrationsystem.core.patient.PatientService;
import org.example.prspatientregistrationsystem.core.patient.command.PatientUpdateCommand;
import org.example.prspatientregistrationsystem.core.patient.dto.PatientDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PatientControllerTest {

    @Mock
    private PatientService patientService;

    @InjectMocks
    private PatientController patientController;

    private PatientDto samplePatientDto;

    @BeforeEach
    void setUp() {
        samplePatientDto = PatientDto.builder()
                .id(1L)
                .firstname("Adam")
                .lastname("Mickiewicz")
                .email("adas.mickey@example.com")
                .phoneNumber("700880772")
                .identificationNumber("17981224044")
                .birthDate(LocalDate.of(1798, 12, 24))
                .build();
    }

    @Test
    void findAll_ShouldReturnListOfPatients() {
        List<PatientDto> expectedList = List.of(samplePatientDto);

        when(patientService.findAll()).thenReturn(expectedList);

        List<PatientDto> actualList = patientController.findAll();

        assertEquals(expectedList, actualList);
        verify(patientService, times(1)).findAll();
    }

    @Test
    void add_ShouldCallSaveOnService() {
        patientController.add(samplePatientDto);

        verify(patientService, times(1)).save(samplePatientDto);
    }

    @Test
    void findById_ShouldReturnPatientDto() {
        Long id = 1L;

        when(patientService.findById(id)).thenReturn(samplePatientDto);

        PatientDto actual = patientController.findById(id);

        assertEquals(samplePatientDto, actual);
        verify(patientService, times(1)).findById(id);
    }

    @Test
    void update_ShouldCallUpdateOnService() {
        PatientUpdateCommand command = mock(PatientUpdateCommand.class);

        patientController.update(command);

        verify(patientService, times(1)).update(command);
    }

    @Test
    void delete_ShouldCallDeleteOnService() {
        Long id = 1L;

        patientController.delete(id);

        verify(patientService, times(1)).delete(id);
    }
}