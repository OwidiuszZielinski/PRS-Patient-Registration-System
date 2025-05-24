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
}
