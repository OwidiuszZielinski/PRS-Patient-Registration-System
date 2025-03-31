package org.example.prspatientregistrationsystem.core.doctor;

import lombok.Builder;
import org.example.prspatientregistrationsystem.core.employeeworkschedule.EmployeeWorkSchedule;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

@Builder
public record DoctorDto(
        Long id,
        String firstName,
        String lastName,
        String licenseNumber,
        List<EmployeeWorkScheduleDto> employeeWorkSchedules
) {

    public static DoctorDto mapToDoctor(Doctor doctor) {
        return DoctorDto.builder()
                .id(doctor.getDoctorId())
                .firstName(doctor.getFirstName())
                .lastName(doctor.getLastName())
                .licenseNumber(doctor.getLicenseNumber())
                .employeeWorkSchedules(doctor.getEmployeeWorkSchedules()
                        .stream()
                        .map(EmployeeWorkScheduleDto::of)
                        .toList())
                .build();
    }
}


@Builder
record EmployeeWorkScheduleDto(
        Long id,
        DayOfWeek dayOfWeek,
        LocalTime startTime,
        LocalTime endTime,
        Long doctorId
) {
    static EmployeeWorkScheduleDto of(EmployeeWorkSchedule employeeWorkSchedule) {
        Long id = 0L;
        if (employeeWorkSchedule.getDoctor() != null) {
            id = employeeWorkSchedule.getDoctor().getDoctorId();
        }
        return EmployeeWorkScheduleDto.builder()
                .id(employeeWorkSchedule.getId())
                .dayOfWeek(employeeWorkSchedule.getDayOfWeek())
                .startTime(employeeWorkSchedule.getStartTime())
                .endTime(employeeWorkSchedule.getEndTime())
                .doctorId(id)
                .build();
    }
}
