package org.example.prspatientregistrationsystem.core.doctor;

import lombok.RequiredArgsConstructor;
import org.example.prspatientregistrationsystem.core.doctor.commad.DoctorAddCommand;
import org.example.prspatientregistrationsystem.core.employeeworkschedule.EmployeeWorkSchedule;
import org.example.prspatientregistrationsystem.core.employeeworkschedule.EmployeeWorkScheduleService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DoctorService {

  private final DoctorRepository doctorRepository;
  private final EmployeeWorkScheduleService employeeWorkScheduleService;

  public void add(DoctorAddCommand command) {

    var toSave = DoctorDto.of(command);
    doctorRepository.save(Doctor.of(toSave));
    saveWorkSchedule(command.employeeWorkSchedules());
  }

  public List<DoctorDto> findAll() {
    return doctorRepository.findAll()
        .stream().map(DoctorDto::of)
        .toList();
  }

  private void saveWorkSchedule(List<EmployeeWorkSchedule> employeeWorkSchedules) {
    employeeWorkSchedules.forEach(employeeWorkScheduleService::add);
  }

}
