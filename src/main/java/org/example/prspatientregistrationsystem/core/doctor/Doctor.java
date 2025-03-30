package org.example.prspatientregistrationsystem.core.doctor;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Data;
import org.example.prspatientregistrationsystem.employeeworkschedule.EmployeeWorkSchedule;

import java.util.List;

@Entity
@Data
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstName;
    private String lastName;
    private String licenseNumber;
    private String workingHours;
    @OneToMany
    private List<EmployeeWorkSchedule> employeeWorkSchedules;
}
