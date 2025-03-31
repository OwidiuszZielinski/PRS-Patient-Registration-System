package org.example.prspatientregistrationsystem.core.doctor;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.prspatientregistrationsystem.core.employeeworkschedule.EmployeeWorkSchedule;

import java.util.List;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long doctorId;
    private String firstName;
    private String lastName;
    private String licenseNumber;
    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EmployeeWorkSchedule> employeeWorkSchedules;

}
