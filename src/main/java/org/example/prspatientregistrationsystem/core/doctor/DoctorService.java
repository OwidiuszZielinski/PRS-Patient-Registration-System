package org.example.prspatientregistrationsystem.core.doctor;

import lombok.RequiredArgsConstructor;
import org.example.prspatientregistrationsystem.core.doctor.commad.DoctorAddCommand;
import org.example.prspatientregistrationsystem.core.employeeworkschedule.DoctorSchedule;
import org.example.prspatientregistrationsystem.core.employeeworkschedule.DoctorScheduleService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final DoctorScheduleService doctorScheduleService;

    public void add(DoctorAddCommand command) {
        var doctorToSave = buildDoctorToSave(command);
        var saved = doctorRepository.save(doctorToSave);

        var doctorScheduleToSave = command.doctorSchedules()
                .stream()
                .map(schedule -> DoctorSchedule.builder()
                        .doctor(saved)
                        .scheduleDate(schedule.scheduleDate())
                        .isWorkingDay(schedule.isWorkingDay())
                        .isVacation(schedule.isVacation())
                        .startTime(schedule.startTime())
                        .endTime(schedule.endTime())
                        .build()
                ).toList();

        doctorRepository.save(doctorToSave);
        saveWorkSchedule(doctorScheduleToSave);
    }

    public List<DoctorDto> findAll() {
        return doctorRepository.findAll()
                .stream().map(DoctorDto::mapToDto)
                .toList();
    }

    public DoctorDto findById(Long id) {
        return DoctorDto.mapToDto(doctorRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Doctor with id %d not found".formatted(id)))
        );
    }

    public List<String> findAllFullNames() {
        return findAll().stream()
                .map(doctor -> "%s %s".formatted(doctor.firstName(), doctor.lastName()))
                .toList();
    }

    public void delete(Long id) {
        doctorRepository.deleteById(id);
    }

    private void saveWorkSchedule(List<DoctorSchedule> doctorSchedules) {
        doctorSchedules.forEach(doctorScheduleService::add);
    }

    private static Doctor buildDoctorToSave(DoctorAddCommand command) {
        return Doctor.builder()
                .officeId(command.officeId())
                .firstName(command.firstName())
                .lastName(command.lastName())
                .licenseNumber(command.licenseNumber())
                .build();
    }
}

