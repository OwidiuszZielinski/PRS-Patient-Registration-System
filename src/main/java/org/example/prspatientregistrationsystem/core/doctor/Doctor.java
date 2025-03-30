package org.example.prspatientregistrationsystem.core.doctor;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Builder;
import lombok.Data;
import org.example.prspatientregistrationsystem.core.employeeworkschedule.EmployeeWorkSchedule;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Builder
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstName;
    private String lastName;
    private String licenseNumber;
    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EmployeeWorkSchedule> employeeWorkSchedules;

    public static Doctor of(DoctorDto doctorDto) {
        var doctor = Doctor.builder()
            .firstName(doctorDto.firstName())
            .lastName(doctorDto.lastName())
            .licenseNumber(doctorDto.licenseNumber())
            .build();
        setRelationWitchEmployeeWorkSchedules(doctorDto, doctor);

        return doctor;

    }

    private static void setRelationWitchEmployeeWorkSchedules(DoctorDto doctorDto, Doctor doctor) {
        if (doctorDto.employeeWorkSchedules() != null) {
            doctorDto.employeeWorkSchedules().forEach(schedule -> schedule.setDoctor(doctor));
            doctor.setEmployeeWorkSchedules(doctorDto.employeeWorkSchedules());
        }
    }
}
