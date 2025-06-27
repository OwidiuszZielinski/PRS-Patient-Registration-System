package org.example.prspatientregistrationsystem.core.visit;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.prspatientregistrationsystem.core.mail.EmailService;
import org.example.prspatientregistrationsystem.core.service.ServiceDto;
import org.example.prspatientregistrationsystem.core.service.ServiceEntity;
import org.example.prspatientregistrationsystem.core.service.ServiceRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
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
        var serviceEntities = getServiceEntities(visitDto);
        var entity = buildVisitEntity(visitDto, serviceEntities);
        buildTotalCostOfVisit(visitDto, entity);
        var savedEntity = visitRepository.save(entity);
        visitDto.setId(savedEntity.getId());
        sendEmailNotification(visitDto);
        return savedEntity.getId();
    }

    private static void buildTotalCostOfVisit(VisitDto visitDto, VisitEntity entity) {
        var totalCost = visitDto.getTotalCost();
        totalCost = calculateCost(visitDto, totalCost);
        entity.setTotalCost(totalCost);
    }

    private static BigDecimal calculateCost(VisitDto visitDto, BigDecimal totalCost) {
        if (totalCost == null || totalCost.compareTo(BigDecimal.ZERO) == 0) {
            totalCost = calculateTotalCost(visitDto.getSelectedServices());
        }
        return totalCost;
    }


    public List<VisitDto> findAll() {
        return visitRepository.findAll().stream()
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
                        .map(ServiceDto::mapToEntity)
                        .toList() : null);
    }

    private VisitEntity findById(Long id) {
        return visitRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Visit with id %s not found]".formatted(id)));
    }

    private static BigDecimal calculateTotalCost(List<ServiceDto> services) {
        return services.stream()
                .filter(service -> service != null && service.getPrice() != null)
                .map(ServiceDto::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private void sendEmailNotification(VisitDto visitDto) {
        var formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy 'at' HH:mm");
        try {
            emailService.scheduleEmailInOneMinute("owi19955@gmail.com", "Visit", "Remember visit at %s".formatted(visitDto.getDate().format(formatter)));
            log.info("Email notification scheduled");
        } catch (Exception e) {
            log.warn("Failed to schedule email notification", e);
        }
    }

    private static VisitEntity buildVisitEntity(VisitDto visitDto, List<ServiceEntity> serviceEntities) {
        return VisitEntity.create(
                visitDto.getDoctorName(),
                visitDto.getPatient(),
                visitDto.getDate(),
                visitDto.getDescription(),
                serviceEntities,
                visitDto.getTotalCost()
        );
    }

    private List<ServiceEntity> getServiceEntities(VisitDto visitDto) {
        List<ServiceEntity> serviceEntities = null;
        if (visitDto.getSelectedServices() != null) {
            serviceEntities = visitDto.getSelectedServices().stream()
                    .map(dto -> serviceRepository.findById(dto.getId())
                            .orElseThrow(() -> new RuntimeException("Service not found: " + dto.getId())))
                    .toList();
        }
        return serviceEntities;
    }
}
