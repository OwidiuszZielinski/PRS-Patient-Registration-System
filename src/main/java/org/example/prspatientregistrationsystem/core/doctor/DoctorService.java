package org.example.prspatientregistrationsystem.core.doctor;

import lombok.RequiredArgsConstructor;
import org.example.prspatientregistrationsystem.core.doctor.commad.DoctorAddCommand;
import org.example.prspatientregistrationsystem.core.doctor.commad.DoctorUpdateCommand;
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
        doctorRepository.save(buildDoctorToSave(command));
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


    private static Doctor buildDoctorToSave(DoctorAddCommand command) {
        return Doctor.builder()
                .officeId(command.officeId())
                .firstName(command.firstName())
                .lastName(command.lastName())
                .licenseNumber(command.licenseNumber())
                .build();
    }

    public void update(DoctorUpdateCommand command) {
        var toUpdate = doctorRepository.findById(command.id())
            .orElseThrow(() -> new NoSuchElementException("Doctor with id %d not found".formatted(command.id())));
        toUpdate.setLastName(command.lastName());
        toUpdate.setOfficeId(command.officeId());
        toUpdate.setLicenseNumber(command.licenseNumber());

        doctorRepository.save(toUpdate);
    }
}

