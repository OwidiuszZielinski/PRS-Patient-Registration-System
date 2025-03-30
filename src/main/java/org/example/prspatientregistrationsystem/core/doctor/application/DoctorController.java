package org.example.prspatientregistrationsystem.core.doctor.application;

import org.example.prspatientregistrationsystem.core.doctor.DoctorDto;
import org.example.prspatientregistrationsystem.core.doctor.DoctorService;
import org.example.prspatientregistrationsystem.core.doctor.commad.DoctorAddCommand;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/doctors/")
public class DoctorController {

  private final DoctorService doctorService;

  public DoctorController(DoctorService doctorService) {
    this.doctorService = doctorService;
  }

  @GetMapping
  public List<DoctorDto> getDoctors() {
    return doctorService.findAll();
  }

  @PostMapping
  public void addDoctor(@RequestBody DoctorAddCommand command) {
    doctorService.add(command);
  }

}

