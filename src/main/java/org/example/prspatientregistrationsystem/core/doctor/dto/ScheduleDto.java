package org.example.prspatientregistrationsystem.core.doctor.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;
import java.time.LocalTime;

public record ScheduleDto(
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate scheduleDate,
        LocalTime startTime,
        LocalTime endTime
) {
}
