package org.example.prspatientregistrationsystem.core.patient.dto;

import lombok.Builder;
import org.example.prspatientregistrationsystem.core.patient.Patient;
import org.example.prspatientregistrationsystem.core.user.AppUser;

import java.time.LocalDate;

@Builder
public record PatientDto(
        Long id,
        String firstname,
        String lastname,
        String email,
        String phoneNumber,
        String identificationNumber,
        LocalDate birthDate,
        AppUser appUser
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
                .appUser(patient.getAppUser())
                .build();
    }

    public Patient toPatient() {
        Patient patient = new Patient();
        patient.setId(this.id);
        patient.setFirstName(this.firstname);
        patient.setLastName(this.lastname);
        patient.setEmail(this.email);
        patient.setPhoneNumber(this.phoneNumber);
        patient.setIdentificationNumber(this.identificationNumber);
        patient.setBirthDate(this.birthDate);
        patient.setAppUser(this.appUser);
        return patient;
    }
}
