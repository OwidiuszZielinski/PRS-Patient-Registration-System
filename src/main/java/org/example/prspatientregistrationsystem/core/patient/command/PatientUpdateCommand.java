package org.example.prspatientregistrationsystem.core.patient.command;


public record PatientUpdateCommand(
        Long id,
        String lastname,
        String email,
        String phoneNumber
) {
}
