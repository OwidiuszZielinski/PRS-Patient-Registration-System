package org.example.prspatientregistrationsystem.core.doctor;

import lombok.Builder;
import org.example.prspatientregistrationsystem.core.employeeworkschedule.DoctorSchedule;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Builder
public record DoctorDto(
        Long id,
        String firstName,
        String lastName,
        String licenseNumber,
        List<DoctorScheduleDto> doctorSchedules
) {

    public static DoctorDto mapToDoctor(Doctor doctor) {
        return DoctorDto.builder()
                .id(doctor.getDoctorId())
                .firstName(doctor.getFirstName())
                .lastName(doctor.getLastName())
                .licenseNumber(doctor.getLicenseNumber())
                .doctorSchedules(doctor.getDoctorSchedules()
                        .stream()
                        .map(DoctorScheduleDto::of)
                        .toList())
                .build();
    }
}


@Builder
record DoctorScheduleDto(
        Long id,
        Long doctorId,
        LocalDate scheduleDate,
        Boolean isWorkingDay,
        Boolean isVacation,
        LocalTime startTime,
        LocalTime endTime
) {
    static DoctorScheduleDto of(DoctorSchedule employeeWorkSchedule) {
        Long id = 0L;
        if (employeeWorkSchedule.getDoctor() != null) {
            id = employeeWorkSchedule.getDoctor().getDoctorId();
        }
        return DoctorScheduleDto.builder()
            .id(employeeWorkSchedule.getId())
            .doctorId(id)
            .scheduleDate(employeeWorkSchedule.getScheduleDate())
            .isWorkingDay(employeeWorkSchedule.isWorkingDay())
            .isVacation(employeeWorkSchedule.isVacation())
            .startTime(employeeWorkSchedule.getStartTime())
            .endTime(employeeWorkSchedule.getEndTime())
                .build();
    }
}
