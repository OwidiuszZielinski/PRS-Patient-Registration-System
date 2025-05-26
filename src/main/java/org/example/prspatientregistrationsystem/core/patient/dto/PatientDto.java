package org.example.prspatientregistrationsystem.core.patient.dto;

import lombok.Builder;
import org.example.prspatientregistrationsystem.core.patient.Patient;

@Builder
public record PatientDto(
        Long id,
        String firstname,
        String lastname,
        String email,
        String phoneNumber,
        String identificationNumber
) {
    public static PatientDto mapToPatientDto(Patient patient) {
        return PatientDto.builder()
                .id(patient.getId())
                .firstname(patient.getFirstName())
                .lastname(patient.getLastName())
                .email(patient.getEmail())
                .phoneNumber(patient.getPhoneNumber())
                .identificationNumber(patient.getIdentificationNumber())
                .build();
    }
}
