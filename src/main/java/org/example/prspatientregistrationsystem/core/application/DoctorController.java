package org.example.prspatientregistrationsystem.core.application;

import lombok.RequiredArgsConstructor;
import org.example.prspatientregistrationsystem.core.doctor.DoctorDto;
import org.example.prspatientregistrationsystem.core.doctor.DoctorService;
import org.example.prspatientregistrationsystem.core.doctor.commad.DoctorAddCommand;
import org.example.prspatientregistrationsystem.core.doctor.commad.DoctorUpdateCommand;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/doctor/")
@CrossOrigin(origins = "http://localhost:3000")
public class DoctorController {

    private final DoctorService doctorService;

    @GetMapping
    public List<DoctorDto> findAll() {
        return doctorService.findAll();
    }

    @GetMapping(path = "/{id}/")
    public DoctorDto findById(@PathVariable Long id) {
        return doctorService.findById(id);
    }

    @GetMapping(path = "fullname")
    public List<String> getDoctorsFullName() {
        return doctorService.findAllFullNames();
    }

    @PostMapping
    public void add(@RequestBody DoctorAddCommand command) {
        doctorService.add(command);
    }

    @PostMapping(path = "update")
    public void update(@RequestBody DoctorUpdateCommand command) {
        doctorService.update(command);
    }

    @DeleteMapping(path = "/{id}/")
    public void delete(@PathVariable Long id) {
        doctorService.delete(id);
    }

}

