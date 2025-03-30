package org.example.prspatientregistrationsystem.core.doctor.commad;

import lombok.Builder;
import org.example.prspatientregistrationsystem.core.employeeworkschedule.EmployeeWorkSchedule;

import java.util.List;

@Builder
public record DoctorAddCommand(
    String firstName,
    String lastName,
    String licenseNumber,
    List<EmployeeWorkSchedule> employeeWorkSchedules
) {

}
