package org.example.prspatientregistrationsystem.core.doctor;

import lombok.Builder;
import org.example.prspatientregistrationsystem.core.doctor.commad.DoctorAddCommand;
import org.example.prspatientregistrationsystem.core.employeeworkschedule.EmployeeWorkSchedule;

import java.util.List;

@Builder
public record DoctorDto(
    Long id,
    String firstName,
    String lastName,
    String licenseNumber,
    List<EmployeeWorkSchedule> employeeWorkSchedules
) {

  public static DoctorDto of(Doctor doctor) {
    return new DoctorDto(
        doctor.getId(),
        doctor.getFirstName(),
        doctor.getLastName(),
        doctor.getLicenseNumber(),
        doctor.getEmployeeWorkSchedules()
    );
  }

  public static DoctorDto of(DoctorAddCommand command) {
    return new DoctorDto(
        null,
        command.firstName(),
        command.lastName(),
        command.licenseNumber(),
        command.employeeWorkSchedules()
    );
  }
}
