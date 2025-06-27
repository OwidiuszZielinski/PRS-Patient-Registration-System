package org.example.prspatientregistrationsystem.core.visit;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.prspatientregistrationsystem.core.mail.EmailService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Slf4j
public class VisitService {

    private final EmailService emailService;
    private final VisitRepository visitRepository;

    public Long addVisit(VisitDto visitDto) {
        try {
            log.info("Adding visit: doctor={}, patient={}, date={}", 
                    visitDto.getDoctorName(), visitDto.getPatient(), visitDto.getDate());
            
            VisitEntity entity = VisitDto.mapToEntity(visitDto);
            entity.setTotalCost(calculateTotalCost(visitDto.getSelectedServices()));
            
            log.info("Visit entity created, saving to database...");
            VisitEntity savedEntity = visitRepository.save(entity);
            log.info("Visit saved successfully with ID: {}", savedEntity.getId());
            
            // Update the DTO with the generated ID
            visitDto.setId(savedEntity.getId());
            
            emailService.scheduleEmailInOneMinute("owi19955@gmail.com", "Visit", "Remember visit at --> ");
            
            return savedEntity.getId();
        } catch (Exception e) {
            log.error("Error adding visit", e);
            throw e;
        }
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
        toSave.setTotalCost(calculateTotalCost(visitDto.getSelectedServices()));
        visitRepository.save(toSave);
    }

    private static void updateFieleds(VisitDto visitDto, VisitEntity toSave) {
        toSave.setDate(visitDto.getDate());
        toSave.setDoctorName(visitDto.getDoctorName());
        toSave.setDescription(visitDto.getDescription());
        toSave.setPatient(visitDto.getPatient());
        toSave.setSelectedServices(visitDto.getSelectedServices() != null ? 
            visitDto.getSelectedServices().stream()
                .map(org.example.prspatientregistrationsystem.core.service.ServiceDto::mapToEntity)
                .toList() : null);
    }

    private VisitEntity findById(Long id) {
        return visitRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Visit with id %s not found]".formatted(id)));
    }
    
    private BigDecimal calculateTotalCost(List<org.example.prspatientregistrationsystem.core.service.ServiceDto> services) {
        if (services == null || services.isEmpty()) {
            return BigDecimal.ZERO;
        }
        return services.stream()
                .map(org.example.prspatientregistrationsystem.core.service.ServiceDto::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
