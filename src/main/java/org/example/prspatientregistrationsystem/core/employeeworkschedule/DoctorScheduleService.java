package org.example.prspatientregistrationsystem.core.employeeworkschedule;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.prspatientregistrationsystem.core.doctor.Doctor;
import org.example.prspatientregistrationsystem.core.doctor.DoctorRepository;
import org.example.prspatientregistrationsystem.core.doctor.dto.ScheduleDto;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DoctorScheduleService {

    private final DoctorScheduleRepo doctorScheduleRepo;
    private final DoctorRepository doctorRepository;

    public void add(DoctorSchedule doctorSchedule) {
        doctorScheduleRepo.save(doctorSchedule);
    }

    public List<DoctorSchedule> findByDoctorAndScheduleDateIn(Doctor doctor, List<LocalDate> datesToUpdate) {
        return doctorScheduleRepo.findByDoctorAndScheduleDateIn(doctor, datesToUpdate);
    }

    public void saveAll(List<DoctorSchedule> schedulesToSave) {
        doctorScheduleRepo.saveAll(schedulesToSave);
    }

    public void setSchedule(List<ScheduleDto> vacationSchedules, Long doctorId) {
        var doctor = findDoctor(doctorId);
        var datesToUpdate = getDatesToUpdate(vacationSchedules);
        var existingSchedules = findByDoctorAndScheduleDateIn(doctor, datesToUpdate);
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
        saveAll(schedulesToSave);
    }

    public void autoFillSchedule(Long doctorId) {
        var doctor = findDoctor(doctorId);
        var currentDate = LocalDate.now();
        var schedulesToSave = new ArrayList<DoctorSchedule>();
        var month = currentDate.getMonth();
        while (month == currentDate.getMonth()) {
            if (currentDate.getDayOfWeek() != DayOfWeek.SATURDAY && currentDate.getDayOfWeek() != DayOfWeek.SUNDAY) {
                schedulesToSave.add(
                        DoctorSchedule.builder()
                                .doctor(doctor)
                                .scheduleDate(currentDate)
                                .isVacation(false)
                                .isWorkingDay(true)
                                .startTime(LocalTime.of(8, 0))
                                .endTime(LocalTime.of(16, 0))
                                .build());
            }
            currentDate = currentDate.plusDays(1);
        }
        saveAll(schedulesToSave);

    }

    private Doctor findDoctor(Long doctorId) {
        return doctorRepository.findById(doctorId).orElseThrow(NoSuchElementException::new);
    }

    private static List<LocalDate> getDatesToUpdate(List<ScheduleDto> vacationSchedules) {
        return vacationSchedules.stream()
                .map(ScheduleDto::scheduleDate)
                .toList();
    }


    @Transactional
    public void clearByDoctorId(Long doctorId) {
        doctorScheduleRepo.deleteDoctorScheduleByDoctor_DoctorId(doctorId);
    }
}
