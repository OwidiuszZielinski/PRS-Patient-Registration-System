package org.example.prspatientregistrationsystem.core.doctor.commad;

import java.time.LocalDate;
import java.time.LocalTime;

public record DoctorScheduleAddCommand(
        LocalDate scheduleDate,
        boolean isWorkingDay,
        boolean isVacation,
        LocalTime startTime,
        LocalTime endTime
) {
}
