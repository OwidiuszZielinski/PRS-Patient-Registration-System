package org.example.prspatientregistrationsystem.core.visit;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.prspatientregistrationsystem.core.service.ServiceEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VisitEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String doctorName;
    private String patient;
    private LocalDateTime date;
    private String description;
    
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "visit_services",
        joinColumns = @JoinColumn(name = "visit_id"),
        inverseJoinColumns = @JoinColumn(name = "service_id")
    )
    private List<ServiceEntity> selectedServices;
    
    private BigDecimal totalCost;
}
