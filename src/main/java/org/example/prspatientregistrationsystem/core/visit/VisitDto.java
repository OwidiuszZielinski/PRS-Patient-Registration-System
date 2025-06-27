package org.example.prspatientregistrationsystem.core.visit;

import lombok.Builder;
import lombok.Data;
import org.example.prspatientregistrationsystem.core.service.ServiceDto;
import org.example.prspatientregistrationsystem.core.service.ServiceEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Builder
@Data
public class VisitDto {
    private Long id;
    private String doctorName;
    private String patient;
    private LocalDateTime date;
    private String description;
    private List<ServiceDto> selectedServices;
    private BigDecimal totalCost;

    public static VisitEntity mapToEntity(VisitDto visitDto) {
        List<ServiceEntity> services = null;
        if (visitDto.selectedServices != null) {
            services = visitDto.selectedServices.stream()
                    .map(ServiceDto::mapToEntity)
                    .toList();
        }
        
        return new VisitEntity(
                visitDto.doctorName,
                visitDto.patient,
                visitDto.date,
                visitDto.description,
                services,
                visitDto.totalCost
        );
    }

    public static VisitDto mapToVisitDto(VisitEntity visitEntity) {
        List<ServiceDto> services = null;
        if (visitEntity.getSelectedServices() != null) {
            services = visitEntity.getSelectedServices().stream()
                    .map(ServiceDto::mapToServiceDto)
                    .toList();
        }
        
        return VisitDto.builder()
            .id(visitEntity.getId())
            .date(visitEntity.getDate())
            .doctorName(visitEntity.getDoctorName())
            .patient(visitEntity.getPatient())
            .description(visitEntity.getDescription())
            .selectedServices(services)
            .totalCost(visitEntity.getTotalCost())
            .build();
    }
}
