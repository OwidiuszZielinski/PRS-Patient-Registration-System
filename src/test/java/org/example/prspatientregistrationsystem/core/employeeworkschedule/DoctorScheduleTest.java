package org.example.prspatientregistrationsystem.core.employeeworkschedule;

import org.example.prspatientregistrationsystem.core.doctor.Doctor;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.time.LocalTime;
import static org.junit.jupiter.api.Assertions.*;

class DoctorScheduleTest {
    @Test void testBuilderAndGetters() {
        Doctor doctor = new Doctor();
        LocalDate date = LocalDate.of(2024, 6, 27);
        LocalTime start = LocalTime.of(8, 0);
        LocalTime end = LocalTime.of(16, 0);
        DoctorSchedule schedule = DoctorSchedule.builder()
                .id(1L)
                .doctor(doctor)
                .scheduleDate(date)
                .isWorkingDay(true)
                .isVacation(false)
                .startTime(start)
                .endTime(end)
                .build();
        assertEquals(1L, schedule.getId());
        assertEquals(doctor, schedule.getDoctor());
        assertEquals(date, schedule.getScheduleDate());
        assertTrue(schedule.isWorkingDay());
        assertFalse(schedule.isVacation());
        assertEquals(start, schedule.getStartTime());
        assertEquals(end, schedule.getEndTime());
    }
    @Test void testConstructorWithDoctorAndDate() {
        Doctor doctor = new Doctor();
        LocalDate date = LocalDate.of(2024, 6, 27);
        DoctorSchedule schedule = new DoctorSchedule(doctor, date);
        assertEquals(doctor, schedule.getDoctor());
        assertEquals(date, schedule.getScheduleDate());
    }
} 