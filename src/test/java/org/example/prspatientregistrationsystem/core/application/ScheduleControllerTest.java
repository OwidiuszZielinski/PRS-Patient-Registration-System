package org.example.prspatientregistrationsystem.core.application;

import org.example.prspatientregistrationsystem.core.doctor.dto.ScheduleDto;
import org.example.prspatientregistrationsystem.core.employeeworkschedule.DoctorScheduleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ScheduleControllerTest {

    @Mock
    private DoctorScheduleService doctorScheduleService;

    @InjectMocks
    private ScheduleController scheduleController;

    private Long doctorId;
    private ScheduleDto scheduleDto;

    @BeforeEach
    void setUp() {
        doctorId = 44L;
        scheduleDto = new ScheduleDto(
                LocalDate.of(2025, 5, 31),
                LocalTime.of(17, 24),
                LocalTime.of(17, 35)
        );
    }

    @Test
    void getSchedule_ShouldReturnListFromService() {
        List<ScheduleDto> schedules = List.of(scheduleDto);

        when(doctorScheduleService.findByDoctorId(doctorId)).thenReturn(schedules);

        List<ScheduleDto> result = scheduleController.getSchedule(doctorId);

        assertEquals(schedules, result);
        verify(doctorScheduleService, times(1)).findByDoctorId(doctorId);
    }

    @Test
    void setSchedule_ShouldCallServiceAndReturnOk() {
        List<ScheduleDto> schedules = List.of(scheduleDto);

        ResponseEntity<String> response = scheduleController.setSchedule(doctorId, schedules);

        verify(doctorScheduleService, times(1)).setSchedule(schedules, doctorId);
        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertEquals("Schedule for doctor with id: 44 updated: ", response.getBody());
    }

    @Test
    void autoFillSchedule_ShouldCallServiceAndReturnOk() {
        ResponseEntity<String> response = scheduleController.autoFillSchedule(doctorId);

        verify(doctorScheduleService, times(1)).autoFillSchedule(doctorId);
        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertEquals("Schedule for doctor with id: 44 updated: ", response.getBody());
    }

    @Test
    void autoFillAllSchedules_ShouldCallServiceAndReturnOk() {
        ResponseEntity<String> response = scheduleController.autoFillAllSchedules();

        verify(doctorScheduleService, times(1)).fillAllDoctorSchedules();
        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertEquals("All schedules filled.", response.getBody());
    }

    @Test
    void deleteSchedule_ShouldCallService() {
        scheduleController.deleteSchedule(doctorId, scheduleDto);

        verify(doctorScheduleService, times(1)).deleteSchedule(doctorId, scheduleDto);
    }

    @Test
    void clearSchedule_ShouldCallService() {
        scheduleController.clearSchedule();

        verify(doctorScheduleService, times(1)).clearByDoctorId();
    }
}