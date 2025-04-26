package org.example.prspatientregistrationsystem.core.doctor.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public record ScheduleDto(
    LocalDate scheduleDate,
    LocalTime startTime,
    LocalTime endTime
) {
}
