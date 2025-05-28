package org.example.prspatientregistrationsystem.core.application;

import org.example.prspatientregistrationsystem.core.doctor.DoctorDto;
import org.example.prspatientregistrationsystem.core.doctor.DoctorService;
import org.example.prspatientregistrationsystem.core.doctor.commad.DoctorAddCommand;
import org.example.prspatientregistrationsystem.core.doctor.commad.DoctorUpdateCommand;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DoctorControllerTest {

    @Mock
    private DoctorService doctorService;

    @InjectMocks
    private DoctorController doctorController;

    @Test
    void findAll_shouldReturnListOfDoctors() {
        DoctorDto doc1 = new DoctorDto(4L, 14L, "Roman", "Kidler", "DOC476576", List.of());
        DoctorDto doc2 = new DoctorDto(7L, 17L, "Rudolf", "Wstrząs", "DOC746764", List.of());

        when(doctorService.findAll()).thenReturn(List.of(doc1, doc2));

        var result = doctorController.findAll();

        assertEquals(2, result.size());
        assertEquals("Roman", result.get(0).firstName());
        assertEquals("Rudolf", result.get(1).firstName());

        verify(doctorService).findAll();
    }

    @Test
    void findById_shouldReturnDoctor() {
        DoctorDto doc = new DoctorDto(4L, 14L, "Roman", "Kidler", "DOC476576", List.of());

        when(doctorService.findById(4L)).thenReturn(doc);

        var result = doctorController.findById(4L);

        assertEquals("Roman", result.firstName());
        verify(doctorService).findById(4L);
    }

    @Test
    void getDoctorsFullName_shouldReturnListOfNames() {
        when(doctorService.findAllFullNames()).thenReturn(List.of("Jan Kowalski", "Anna Nowak"));

        var result = doctorController.getDoctorsFullName();

        assertEquals(2, result.size());
        assertEquals("Jan Kowalski", result.get(0));

        verify(doctorService).findAllFullNames();
    }

    @Test
    void add_shouldCallAddService() {
        DoctorAddCommand command = new DoctorAddCommand(9L, "Czesław", "Basen", "DOC974674", List.of());

        doctorController.add(command);

        verify(doctorService).add(command);
    }

    @Test
    void update_shouldCallUpdateService() {
        DoctorUpdateCommand command = new DoctorUpdateCommand(4L, 16L, "Roman", "Kidler", "DOC476576");

        doctorController.update(command);

        verify(doctorService).update(command);
    }

    @Test
    void delete_shouldCallDeleteService() {
        doctorController.delete(5L);

        verify(doctorService).delete(5L);
    }
}