package org.example.prspatientregistrationsystem.core.employeeworkschedule;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DoctorScheduleService {

  private final DoctorScheduleRepo doctorScheduleRepo;

  public void add(DoctorSchedule doctorSchedule) {
    doctorScheduleRepo.save(doctorSchedule);
  }
}
