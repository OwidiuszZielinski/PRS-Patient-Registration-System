package org.example.prspatientregistrationsystem.core.visit;

import lombok.RequiredArgsConstructor;
import org.example.prspatientregistrationsystem.core.mail.EmailService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class VisitService {

    private final EmailService emailService;
    private final VisitRepository visitRepository;

    public void addVisit(VisitDto visitDto) {
        visitRepository.save(VisitDto.mapToEntity(visitDto));
        emailService.scheduleEmailInOneMinute("owi19955@gmail.com", "Visit", "Remember visit at --> ");
    }

    public List<VisitDto> findAll() {
        return visitRepository.findAll()
                .stream()
            .map(VisitDto::mapToVisitDto)
            .toList();
    }

    public void delete(Long id) {
        visitRepository.deleteById(id);
    }

    public void update(VisitDto visitDto) {
        var toSave = findById(visitDto.getId());
        updateFieleds(visitDto, toSave);
        visitRepository.save(toSave);
    }

    private static void updateFieleds(VisitDto visitDto, VisitEntity toSave) {
        toSave.setDate(visitDto.getDate());
        toSave.setDoctorName(visitDto.getDoctorName());
        toSave.setDescription(visitDto.getDescription());
        toSave.setPatient(visitDto.getPatient());
    }

    private VisitEntity findById(Long id) {
        return visitRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Visit with id %s not found]".formatted(id)));
    }
}
