package org.example.prspatientregistrationsystem.core.doctor;

import lombok.Builder;

@Builder
public record DoctorDto(
    Long id,
    String firstName,
    String lastName,
    String licenseNumber,
    String workingHours
) {

  public static DoctorDto of(Doctor doctor) {
    return new DoctorDto(
        doctor.getId(),
        doctor.getFirstName(),
        doctor.getLastName(),
        doctor.getLicenseNumber(),
        doctor.getWorkingHours()
    );
  }
}
