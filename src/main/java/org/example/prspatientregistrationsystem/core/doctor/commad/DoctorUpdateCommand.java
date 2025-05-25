package org.example.prspatientregistrationsystem.core.doctor.commad;

public record DoctorUpdateCommand(
    Long id,
    Long officeId,
    String firstName,
    String lastName,
    String licenseNumber
) {
}
