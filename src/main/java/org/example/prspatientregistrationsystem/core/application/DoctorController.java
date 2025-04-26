package org.example.prspatientregistrationsystem.core.application;

import lombok.RequiredArgsConstructor;
import org.example.prspatientregistrationsystem.core.doctor.DoctorDto;
import org.example.prspatientregistrationsystem.core.doctor.DoctorService;
import org.example.prspatientregistrationsystem.core.doctor.commad.DoctorAddCommand;
import org.example.prspatientregistrationsystem.core.doctor.dto.ScheduleDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/doctors/")
@CrossOrigin(origins = "http://localhost:3000")
public class DoctorController {

    private final DoctorService doctorService;

    @GetMapping
    public List<DoctorDto> findAll() {
        return doctorService.findAll();
    }

    @GetMapping(path = "fullname")
    public List<String> getDoctorsFullName() {
        return doctorService.findAllFullNames();
    }

    @PostMapping
    public void add(@RequestBody DoctorAddCommand command) {
        doctorService.add(command);
    }

    @PostMapping(path = "/{id}/schedule")
    public ResponseEntity<String> setSchedule(
        @PathVariable Long id,
        @RequestBody List<ScheduleDto> schedules) {
        doctorService.setSchedule(schedules, id);
        return ResponseEntity.ok("Schedule for doctor with id: %d updated: ".formatted(id));
    }

}

