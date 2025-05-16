package org.example.prspatientregistrationsystem.core.doctor.commad;

import lombok.Builder;

import java.util.List;

@Builder
public record DoctorAddCommand(
        Long officeId,
        String firstName,
        String lastName,
        String licenseNumber,
        List<DoctorScheduleAddCommand> doctorSchedules
) {

}
