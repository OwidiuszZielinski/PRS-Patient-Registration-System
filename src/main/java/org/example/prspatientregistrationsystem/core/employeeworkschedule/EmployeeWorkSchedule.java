package org.example.prspatientregistrationsystem.core.employeeworkschedule;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import org.example.prspatientregistrationsystem.core.doctor.Doctor;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Entity
@Data
public class EmployeeWorkSchedule {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Enumerated(EnumType.STRING)
  private DayOfWeek dayOfWeek;
  private LocalTime startTime;
  private LocalTime endTime;
  @ManyToOne
  private Doctor doctor;
}
