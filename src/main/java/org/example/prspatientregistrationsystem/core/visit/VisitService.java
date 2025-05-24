package org.example.prspatientregistrationsystem.core.visit;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VisitService {

    private final VisitRepository visitRepository;

    public void addVisit(VisitDto visitDto) {
        visitRepository.save(VisitDto.mapToEntity(visitDto));
    }

    public List<VisitDto> findAll() {
        return visitRepository.findAll()
                .stream()
                .map(it -> VisitDto.builder()
                        .id(it.getId())
                        .date(it.getDate())
                        .doctorName(it.getDoctorName())
                        .patient(it.getPatient())
                        .description(it.getDescription())
                        .build()
                ).toList();
    }

    public void delete(Long id) {
        visitRepository.deleteById(id);
    }
}
