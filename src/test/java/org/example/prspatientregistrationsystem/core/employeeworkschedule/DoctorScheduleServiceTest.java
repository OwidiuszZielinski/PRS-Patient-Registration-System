package org.example.prspatientregistrationsystem.core.employeeworkschedule;

import org.example.prspatientregistrationsystem.core.doctor.Doctor;
import org.example.prspatientregistrationsystem.core.doctor.DoctorRepository;
import org.example.prspatientregistrationsystem.core.doctor.dto.ScheduleDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DoctorScheduleServiceTest {

    @Mock
    private DoctorScheduleRepo doctorScheduleRepo;

    @Mock
    private DoctorRepository doctorRepository;

    @InjectMocks
    private DoctorScheduleService doctorScheduleService;

    @Captor
    private ArgumentCaptor<List<DoctorSchedule>> doctorScheduleListCaptor;

    @Test
    void shouldSetScheduleCorrectly() {
        Long doctorId = 1L;
        Doctor doctor = Doctor.builder()
                .doctorId(doctorId)
                .build();

        ScheduleDto dto = new ScheduleDto(LocalDate.of(2024, 5, 30),
                LocalTime.of(8, 0), LocalTime.of(16, 0));
        List<ScheduleDto> scheduleDtos = List.of(dto);

        when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(doctor));
        when(doctorScheduleRepo.findByDoctorAndScheduleDateIn(eq(doctor), anyList()))
                .thenReturn(List.of());

        doctorScheduleService.setSchedule(scheduleDtos, doctorId);

        verify(doctorScheduleRepo).saveAll(doctorScheduleListCaptor.capture());
        List<DoctorSchedule> savedSchedules = doctorScheduleListCaptor.getValue();
        assertEquals(1, savedSchedules.size());
        DoctorSchedule saved = savedSchedules.get(0);
        assertEquals(dto.scheduleDate(), saved.getScheduleDate());
        assertTrue(saved.isWorkingDay());
        assertFalse(saved.isVacation());
        assertEquals(dto.startTime(), saved.getStartTime());
        assertEquals(dto.endTime(), saved.getEndTime());
    }

    @Test
    void shouldAutoFillScheduleForWeekdays() {
        Long doctorId = 2L;
        Doctor doctor = Doctor.builder().doctorId(doctorId).build();

        when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(doctor));

        doctorScheduleService.autoFillSchedule(doctorId);

        verify(doctorScheduleRepo).saveAll(argThat(schedules ->
                StreamSupport.stream(schedules.spliterator(), false)
                        .allMatch(s ->
                                !EnumSet.of(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY).contains(s.getScheduleDate().getDayOfWeek())
                                        && s.isWorkingDay()
                                        && !s.isVacation()
                                        && s.getStartTime().equals(LocalTime.of(8, 0))
                                        && s.getEndTime().equals(LocalTime.of(16, 0))
                        )
        ));
    }

    @Test
    void shouldReturnScheduleDtosByDoctorId() {
        Long doctorId = 3L;
        Doctor doctor = Doctor.builder().doctorId(doctorId).build();

        DoctorSchedule schedule = DoctorSchedule.builder()
                .scheduleDate(LocalDate.of(2024, 6, 1))
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(17, 0))
                .build();

        doctor.setDoctorSchedules(List.of(schedule));

        when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(doctor));

        List<ScheduleDto> result = doctorScheduleService.findByDoctorId(doctorId);

        assertEquals(1, result.size());
        ScheduleDto dto = result.get(0);
        assertEquals(schedule.getScheduleDate(), dto.scheduleDate());
        assertEquals(schedule.getStartTime(), dto.startTime());
        assertEquals(schedule.getEndTime(), dto.endTime());
    }

    @Test
    void shouldDeleteScheduleByDoctorIdAndDate() {
        Long doctorId = 4L;
        LocalDate date = LocalDate.of(2024, 6, 10);
        ScheduleDto dto = new ScheduleDto(date, null, null);

        doctorScheduleService.deleteSchedule(doctorId, dto);

        verify(doctorScheduleRepo).deleteByDoctor_DoctorIdAndScheduleDate(doctorId, date);
    }
}