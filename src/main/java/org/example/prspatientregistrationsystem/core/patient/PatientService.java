package org.example.prspatientregistrationsystem.core.patient;

import lombok.RequiredArgsConstructor;
import org.example.prspatientregistrationsystem.core.patient.dto.PatientDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepository patientRepository;

    public List<PatientDto> findAll() {
        return patientRepository.findAll().stream().map(PatientDto::mapToPatientDto).toList();
    }

}
