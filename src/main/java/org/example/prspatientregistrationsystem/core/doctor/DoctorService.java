package org.example.prspatientregistrationsystem.core.doctor;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;


@Service
@RequiredArgsConstructor
public class DoctorService {

  private final DoctorRepository doctorRepository;

  public List<DoctorDto> findAllDoctors() {
    return doctorRepository.findAll()
        .stream().map(DoctorDto::of)
        .toList();
  }

}
