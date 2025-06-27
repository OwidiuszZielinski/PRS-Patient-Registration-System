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
            System.out.println("VisitDto.mapToEntity: selectedServices size: " + visitDto.selectedServices.size());
            System.out.println("VisitDto.mapToEntity: selectedServices: " + visitDto.selectedServices);
            services = visitDto.selectedServices.stream()
                    .map(ServiceDto::mapToEntity)
                    .toList();
            System.out.println("VisitDto.mapToEntity: mapped services size: " + services.size());
        } else {
            System.out.println("VisitDto.mapToEntity: selectedServices is null");
        }
        
        return VisitEntity.create(
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
            System.out.println("VisitDto.mapToVisitDto: selectedServices size: " + visitEntity.getSelectedServices().size());
            System.out.println("VisitDto.mapToVisitDto: selectedServices: " + visitEntity.getSelectedServices());
            services = visitEntity.getSelectedServices().stream()
                    .map(ServiceDto::mapToServiceDto)
                    .toList();
            System.out.println("VisitDto.mapToVisitDto: mapped services size: " + services.size());
        } else {
            System.out.println("VisitDto.mapToVisitDto: selectedServices is null");
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
