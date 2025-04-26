package org.example.prspatientregistrationsystem.core.employeeworkschedule;

import org.example.prspatientregistrationsystem.core.doctor.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DoctorScheduleRepo extends JpaRepository<DoctorSchedule, Long> {
  List<DoctorSchedule> findByDoctorAndScheduleDateIn(Doctor doctor, List<LocalDate> dates);
}
