package org.example.prspatientregistrationsystem.core.visit;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.prspatientregistrationsystem.core.mail.EmailService;
import org.example.prspatientregistrationsystem.core.service.ServiceEntity;
import org.example.prspatientregistrationsystem.core.service.ServiceRepository;
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
    private final ServiceRepository serviceRepository;

    public Long addVisit(VisitDto visitDto) {
        try {
            log.info("Adding visit: doctor={}, patient={}, date={}, selectedServices={}, totalCost={}", 
                    visitDto.getDoctorName(), visitDto.getPatient(), visitDto.getDate(), 
                    visitDto.getSelectedServices(), visitDto.getTotalCost());
            
            List<ServiceEntity> serviceEntities = null;
            if (visitDto.getSelectedServices() != null) {
                serviceEntities = visitDto.getSelectedServices().stream()
                    .map(dto -> serviceRepository.findById(dto.getId())
                        .orElseThrow(() -> new RuntimeException("Service not found: " + dto.getId())))
                    .toList();
                log.info("SelectedServices size: {}, services: {}", 
                        serviceEntities.size(), serviceEntities);
            } else {
                log.info("SelectedServices is null");
            }
            
            VisitEntity entity = VisitEntity.create(
                visitDto.getDoctorName(),
                visitDto.getPatient(),
                visitDto.getDate(),
                visitDto.getDescription(),
                serviceEntities,
                visitDto.getTotalCost()
            );
            
            // Use totalCost from VisitDto if provided, otherwise calculate it
            BigDecimal totalCost = visitDto.getTotalCost();
            if (totalCost == null || totalCost.compareTo(BigDecimal.ZERO) == 0) {
                totalCost = calculateTotalCost(visitDto.getSelectedServices());
                log.info("Calculated total cost: {} for {} services", totalCost, 
                        visitDto.getSelectedServices() != null ? visitDto.getSelectedServices().size() : 0);
            } else {
                log.info("Using totalCost from VisitDto: {}", totalCost);
            }
            
            entity.setTotalCost(totalCost);
            
            log.info("Visit entity created with totalCost={}, selectedServices size={}, saving to database...", 
                    totalCost, entity.getSelectedServices() != null ? entity.getSelectedServices().size() : 0);
            VisitEntity savedEntity = visitRepository.save(entity);
            log.info("Visit saved successfully with ID: {}", savedEntity.getId());
            
            // Update the DTO with the generated ID
            visitDto.setId(savedEntity.getId());
            
            // Send email notification safely
            try {
                emailService.scheduleEmailInOneMinute("owi19955@gmail.com", "Visit", "Remember visit at --> ");
                log.info("Email notification scheduled");
            } catch (Exception e) {
                log.warn("Failed to schedule email notification", e);
                // Don't fail the visit creation if email fails
            }
            
            return savedEntity.getId();
        } catch (Exception e) {
            log.error("Error adding visit", e);
            throw e;
        }
    }

    public List<VisitDto> findAll() {
        List<VisitEntity> entities = visitRepository.findAll();
        log.info("Found {} visit entities", entities.size());
        
        List<VisitDto> result = entities.stream()
            .map(entity -> {
                log.info("Mapping entity ID {} with selectedServices size: {}", 
                        entity.getId(), entity.getSelectedServices() != null ? entity.getSelectedServices().size() : 0);
                return VisitDto.mapToVisitDto(entity);
            })
            .toList();
        
        log.info("Mapped {} visit DTOs", result.size());
        return result;
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
        try {
            if (services == null || services.isEmpty()) {
                log.info("No services provided, total cost is 0");
                return BigDecimal.ZERO;
            }
            
            BigDecimal total = services.stream()
                    .filter(service -> service != null && service.getPrice() != null)
                    .map(org.example.prspatientregistrationsystem.core.service.ServiceDto::getPrice)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            log.info("Calculated total cost: {} for {} services", total, services.size());
            return total;
        } catch (Exception e) {
            log.error("Error calculating total cost", e);
            return BigDecimal.ZERO;
        }
    }
}
