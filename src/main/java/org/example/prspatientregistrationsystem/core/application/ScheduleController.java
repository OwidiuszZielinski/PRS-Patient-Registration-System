package org.example.prspatientregistrationsystem.core.application;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.RequiredArgsConstructor;
import org.example.prspatientregistrationsystem.core.doctor.dto.ScheduleDto;
import org.example.prspatientregistrationsystem.core.employeeworkschedule.DoctorScheduleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/schedule/")
@CrossOrigin(origins = "http://localhost:3000")
public class ScheduleController {

    private final DoctorScheduleService doctorScheduleService;

    @GetMapping(path = "{doctorId}")
    public List<ScheduleDto> getSchedule(@PathVariable("doctorId") Long doctorId) {
        return doctorScheduleService.findByDoctorId(doctorId);
    }

    @PostMapping(path = "{doctorId}")
    public ResponseEntity<String> setSchedule(@PathVariable Long doctorId, @RequestBody List<ScheduleDto> schedules) {
        doctorScheduleService.setSchedule(schedules, doctorId);
        return ResponseEntity.ok("Schedule for doctor with id: %d updated: ".formatted(doctorId));
    }

    @PostMapping(path = "/{doctorId}/fill")
    public ResponseEntity<String> autoFillSchedule(@PathVariable Long doctorId) {
        doctorScheduleService.autoFillSchedule(doctorId);
        return ResponseEntity.ok("Schedule for doctor with id: %d updated: ".formatted(doctorId));
    }

    @PostMapping(path = "fill")
    public ResponseEntity<String> autoFillAllSchedules() {
        doctorScheduleService.fillAllDoctorSchedules();
        return ResponseEntity.ok("All schedules filled.");
    }

    @DeleteMapping(path = "{doctorId}")
    public void deleteSchedule(@PathVariable Long doctorId, @RequestBody ScheduleDto scheduleDto) {
        doctorScheduleService.deleteSchedule(doctorId, scheduleDto);
    }

    @DeleteMapping(path = "{doctorId}/all")
    public void clearSchedule(@PathVariable Long doctorId) {
        doctorScheduleService.clearByDoctorId(doctorId);
    }
}
