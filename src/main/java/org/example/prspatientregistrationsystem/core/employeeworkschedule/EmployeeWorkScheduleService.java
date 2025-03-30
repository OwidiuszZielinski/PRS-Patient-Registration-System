package org.example.prspatientregistrationsystem.core.employeeworkschedule;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmployeeWorkScheduleService {

  private final EmployeeWorkScheduleRepository employeeWorkScheduleRepository;

  public void add(EmployeeWorkSchedule employeeWorkSchedule) {
    employeeWorkScheduleRepository.save(employeeWorkSchedule);
  }
}
