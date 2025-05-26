package org.example.prspatientregistrationsystem.core.visit;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Builder
@Data
public class VisitDto {
    private Long id;
    private String doctorName;
    private String patient;
    private LocalDateTime date;
    private String description;

    public static VisitEntity mapToEntity(VisitDto visitDto) {
        return new VisitEntity(
                null,
                visitDto.doctorName,
                visitDto.patient,
                visitDto.date,
                visitDto.description
        );
    }

    public static VisitDto mapToVisitDto(VisitEntity visitEntity) {
        return VisitDto.builder()
            .id(visitEntity.getId())
            .date(visitEntity.getDate())
            .doctorName(visitEntity.getDoctorName())
            .patient(visitEntity.getPatient())
            .description(visitEntity.getDescription())
            .build();
    }
}
