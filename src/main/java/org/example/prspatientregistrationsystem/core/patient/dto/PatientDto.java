package org.example.prspatientregistrationsystem.core.patient.dto;

import lombok.Builder;
import org.example.prspatientregistrationsystem.core.patient.Patient;

import java.time.LocalDate;

@Builder
public record PatientDto(
        Long id,
        String firstname,
        String lastname,
        String email,
        String phoneNumber,
        String identificationNumber,
        LocalDate birthDate
) {
    public static PatientDto mapToPatientDto(Patient patient) {
        return PatientDto.builder()
                .id(patient.getId())
                .firstname(patient.getFirstName())
                .lastname(patient.getLastName())
                .email(patient.getEmail())
                .phoneNumber(patient.getPhoneNumber())
                .identificationNumber(patient.getIdentificationNumber())
                .birthDate(patient.getBirthDate())
                .build();
    }

    public Patient toPatient() {
        return new Patient(
                null,
                this.firstname,
                this.lastname,
                this.email,
                this.phoneNumber,
                this.identificationNumber,
                this.birthDate
        );
    }
}
