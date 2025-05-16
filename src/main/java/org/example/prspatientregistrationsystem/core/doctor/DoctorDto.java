package org.example.prspatientregistrationsystem.core.doctor;

import lombok.Builder;
import org.example.prspatientregistrationsystem.core.employeeworkschedule.DoctorSchedule;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Builder
public record DoctorDto(
        Long id,
        Long officeId,
        String firstName,
        String lastName,
        String licenseNumber,
        List<DoctorScheduleDto> doctorSchedules
) {

    public static DoctorDto mapToDto(Doctor doctor) {
        return DoctorDto.builder()
                .id(doctor.getDoctorId())
                .officeId(doctor.getOfficeId())
                .firstName(doctor.getFirstName())
                .lastName(doctor.getLastName())
                .licenseNumber(doctor.getLicenseNumber())
                .doctorSchedules(doctor.getDoctorSchedules()
                        .stream()
                        .map(DoctorScheduleDto::of)
                        .toList())
                .build();
    }

    public Doctor toDoctor() {
        return Doctor.builder()
                .doctorId(this.id)
                .officeId(this.officeId)
                .firstName(this.firstName)
                .lastName(this.lastName)
                .licenseNumber(this.licenseNumber)
                .doctorSchedules(
                        buildDoctorSchedules()
                )
                .build();
    }

    private List<DoctorSchedule> buildDoctorSchedules() {
        return !this.doctorSchedules.isEmpty() ?
                this.doctorSchedules.stream()
                        .map(dto -> DoctorSchedule.builder()
                                    .id(dto.id())
                                    .doctor(this.toDoctor())
                                    .scheduleDate(dto.scheduleDate())
                                    .isWorkingDay(dto.isWorkingDay())
                                    .isVacation(dto.isVacation())
                                    .startTime(dto.startTime())
                                    .endTime(dto.endTime())
                                    .build()
                        ).toList() : List.of();
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
