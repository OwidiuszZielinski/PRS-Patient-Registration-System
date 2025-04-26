package org.example.prspatientregistrationsystem.core.doctor;

import lombok.RequiredArgsConstructor;
import org.example.prspatientregistrationsystem.core.doctor.commad.DoctorAddCommand;
import org.example.prspatientregistrationsystem.core.doctor.dto.ScheduleDto;
import org.example.prspatientregistrationsystem.core.employeeworkschedule.DoctorSchedule;
import org.example.prspatientregistrationsystem.core.employeeworkschedule.DoctorScheduleRepo;
import org.example.prspatientregistrationsystem.core.employeeworkschedule.DoctorScheduleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final DoctorScheduleRepo doctorScheduleRepo;
    private final DoctorScheduleService doctorScheduleService;


    public void add(DoctorAddCommand command) {
        var doctorToSave = Doctor.builder()
                .firstName(command.firstName())
                .lastName(command.lastName())
                .licenseNumber(command.licenseNumber())
                .doctorSchedules(command.doctorSchedules())
                .build();

        command.doctorSchedules().forEach(schedule -> schedule.setDoctor(doctorToSave));
        doctorRepository.save(doctorToSave);
        saveWorkSchedule(command.doctorSchedules());
    }

    public List<DoctorDto> findAll() {
        return doctorRepository.findAll()
                .stream().map(DoctorDto::mapToDoctor)
                .toList();
    }

    private void saveWorkSchedule(List<DoctorSchedule> doctorSchedules) {
        doctorSchedules.forEach(doctorScheduleService::add);
    }

    public List<String> findAllFullNames() {
        return findAll().stream()
            .map(doctor -> "%s %s".formatted(doctor.firstName(), doctor.lastName()))
            .toList();
    }

    @Transactional
    public void setSchedule(List<ScheduleDto> vacationSchedules, Long doctorId) {
        var doctor = doctorRepository.findById(doctorId)
            .orElseThrow(() -> new NoSuchElementException("Doctor not found with id: %s".formatted(doctorId)));

        var datesToUpdate = getDatesToUpdate(vacationSchedules);
        var existingSchedules = doctorScheduleRepo.findByDoctorAndScheduleDateIn(doctor, datesToUpdate);

        var scheduleMap = existingSchedules.stream()
            .collect(Collectors.toMap(DoctorSchedule::getScheduleDate, Function.identity()));

        var schedulesToSave = vacationSchedules.stream()
            .map(dto -> {
                var schedule = scheduleMap.computeIfAbsent(
                    dto.scheduleDate(),
                    date -> new DoctorSchedule(doctor, date)
                );

                if (dto.startTime() == null && dto.endTime() == null) {
                    schedule.setVacation(true);
                    schedule.setWorkingDay(false);
                    schedule.setStartTime(null);
                    schedule.setEndTime(null);
                } else {
                    schedule.setVacation(false);
                    schedule.setWorkingDay(true);
                    schedule.setStartTime(dto.startTime());
                    schedule.setEndTime(dto.endTime());
                }

                return schedule;
            })
            .toList();
        doctorScheduleRepo.saveAll(schedulesToSave);
    }

    private static List<LocalDate> getDatesToUpdate(List<ScheduleDto> vacationSchedules) {
        return vacationSchedules.stream()
            .map(ScheduleDto::scheduleDate)
            .toList();
    }
}

