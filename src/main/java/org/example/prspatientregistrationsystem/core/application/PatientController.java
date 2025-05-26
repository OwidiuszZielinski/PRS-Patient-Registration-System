package org.example.prspatientregistrationsystem.core.application;

import lombok.RequiredArgsConstructor;
import org.example.prspatientregistrationsystem.core.patient.PatientService;
import org.example.prspatientregistrationsystem.core.patient.command.PatientUpdateCommand;
import org.example.prspatientregistrationsystem.core.patient.dto.PatientDto;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patient")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;

    @GetMapping
    public List<PatientDto> findAll() {
        return patientService.findAll();
    }

    @PostMapping
    public void add(@RequestBody PatientDto patientDto) {
        patientService.save(patientDto);
    }

    @GetMapping(path = "/{id}/")
    public PatientDto findById(@PathVariable Long id) {
        return patientService.findById(id);
    }

    @PostMapping(path = "/update")
    public void update(@RequestBody PatientUpdateCommand command) {
        patientService.update(command);
    }

    @DeleteMapping(path = "/{id}")
    public void delete(@PathVariable Long id) {
        patientService.delete(id);
    }
}
