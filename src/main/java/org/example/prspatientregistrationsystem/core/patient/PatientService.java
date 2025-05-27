package org.example.prspatientregistrationsystem.core.patient;

import lombok.RequiredArgsConstructor;
import org.example.prspatientregistrationsystem.core.patient.command.PatientUpdateCommand;
import org.example.prspatientregistrationsystem.core.patient.dto.PatientDto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepository patientRepository;

    public List<PatientDto> findAll() {
        return patientRepository.findAll().stream().map(PatientDto::mapToPatientDto).toList();
    }

    public void save(PatientDto patientDto) {
        if (patientDto.identificationNumber().isEmpty()) {
            throw new RuntimeException("identification number is null");
        }
        patientRepository.save(patientDto.toPatient());
    }

    public PatientDto findById(Long id) {
        return PatientDto.mapToPatientDto(findPatientById(id));
    }

    public void delete(Long id) {
        patientRepository.deleteById(id);
    }

    public void update(PatientUpdateCommand command) {
        var toUpdate = findPatientById(command.id());
        toUpdate.setLastName(command.lastname());
        toUpdate.setEmail(command.email());
        toUpdate.setPhoneNumber(command.phoneNumber());
        patientRepository.save(toUpdate);
    }

    private Patient findPatientById(Long id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Patient with id %d not found".formatted(id)));
    }

}
