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
        var doctorToSave = Doctor.builder()
                .firstName(command.firstName())
                .lastName(command.lastName())
                .licenseNumber(command.licenseNumber())
                .employeeWorkSchedules(command.employeeWorkSchedules())
                .build();

        command.employeeWorkSchedules().forEach(schedule -> schedule.setDoctor(doctorToSave));
        doctorRepository.save(doctorToSave);
        saveWorkSchedule(command.employeeWorkSchedules());
    }

    public List<DoctorDto> findAll() {
        return doctorRepository.findAll()
                .stream().map(DoctorDto::mapToDoctor)
                .toList();
    }

    private void saveWorkSchedule(List<EmployeeWorkSchedule> employeeWorkSchedules) {
        employeeWorkSchedules.forEach(employeeWorkScheduleService::add);
    }

}
