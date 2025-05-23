package org.example.prspatientregistrationsystem.core.application;

import lombok.RequiredArgsConstructor;
import org.example.prspatientregistrationsystem.core.doctor.dto.ScheduleDto;
import org.example.prspatientregistrationsystem.core.employeeworkschedule.DoctorScheduleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/schedule/")
@CrossOrigin(origins = "http://localhost:3000")
public class ScheduleController {

    private final DoctorScheduleService doctorScheduleService;

    @PostMapping(path = "/{doctorId}/")
    public ResponseEntity<String> setSchedule(@PathVariable Long doctorId, @RequestBody List<ScheduleDto> schedules) {
        doctorScheduleService.setSchedule(schedules, doctorId);
        return ResponseEntity.ok("Schedule for doctor with id: %d updated: ".formatted(doctorId));
    }

    @PostMapping(path = "/{doctorId}/fill")
    public ResponseEntity<String> autoFillSchedule(@PathVariable Long doctorId) {
        doctorScheduleService.autoFillSchedule(doctorId);
        return ResponseEntity.ok("Schedule for doctor with id: %d updated: ".formatted(doctorId));
    }

    @DeleteMapping(path = "/{doctorId}/")
    public void clearSchedule(@PathVariable Long doctorId) {
        doctorScheduleService.clearByDoctorId(doctorId);
    }
}
