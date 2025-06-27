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
    
    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
        name = "visit_services",
        joinColumns = @JoinColumn(name = "visit_id", referencedColumnName = "id"),
        inverseJoinColumns = @JoinColumn(name = "service_id", referencedColumnName = "id")
    )
    private List<ServiceEntity> selectedServices;
    
    private BigDecimal totalCost;

    // Custom constructor for creating new visits
    public VisitEntity(String doctorName, String patient, LocalDateTime date, String description, 
                      List<ServiceEntity> selectedServices, BigDecimal totalCost) {
        System.out.println("VisitEntity constructor called with selectedServices size: " + 
                (selectedServices != null ? selectedServices.size() : 0));
        this.doctorName = doctorName;
        this.patient = patient;
        this.date = date;
        this.description = description;
        this.selectedServices = selectedServices;
        this.totalCost = totalCost;
        System.out.println("VisitEntity constructor finished, selectedServices size: " + 
                (this.selectedServices != null ? this.selectedServices.size() : 0));
    }
    
    // Static method to create VisitEntity using builder
    public static VisitEntity create(String doctorName, String patient, LocalDateTime date, String description, 
                                   List<ServiceEntity> selectedServices, BigDecimal totalCost) {
        System.out.println("VisitEntity.create called with selectedServices size: " + 
                (selectedServices != null ? selectedServices.size() : 0));
        VisitEntity entity = VisitEntity.builder()
                .doctorName(doctorName)
                .patient(patient)
                .date(date)
                .description(description)
                .selectedServices(selectedServices)
                .totalCost(totalCost)
                .build();
        System.out.println("VisitEntity.create finished, selectedServices size: " + 
                (entity.getSelectedServices() != null ? entity.getSelectedServices().size() : 0));
        return entity;
    }
}
