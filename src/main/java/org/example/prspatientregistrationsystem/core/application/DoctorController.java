package org.example.prspatientregistrationsystem.core.application;

import lombok.RequiredArgsConstructor;
import org.example.prspatientregistrationsystem.core.doctor.DoctorDto;
import org.example.prspatientregistrationsystem.core.doctor.DoctorService;
import org.example.prspatientregistrationsystem.core.doctor.commad.DoctorAddCommand;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/doctors/")
public class DoctorController {

    private final DoctorService doctorService;

    @GetMapping
    public List<DoctorDto> getDoctors() {
        return doctorService.findAll();
    }

    @PostMapping
    public void addDoctor(@RequestBody DoctorAddCommand command) {
        doctorService.add(command);
    }

}

