package org.example.prspatientregistrationsystem.core.employeeworkschedule;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.prspatientregistrationsystem.core.doctor.Doctor;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DoctorSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "doctor_id")
    private Doctor doctor;
    private LocalDate scheduleDate;
    private boolean isWorkingDay = true;
    private boolean isVacation = false;
    private LocalTime startTime;
    private LocalTime endTime;

    public DoctorSchedule(Doctor doctor, LocalDate scheduleDate) {
        this.doctor = doctor;
        this.scheduleDate = scheduleDate;
    }
}
