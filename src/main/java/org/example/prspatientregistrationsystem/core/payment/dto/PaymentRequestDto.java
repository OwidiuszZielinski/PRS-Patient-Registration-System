package org.example.prspatientregistrationsystem.core.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequestDto {
    private String visitId;
    private String patientName;
    private String patientEmail;
    private BigDecimal amount;
    private String description;
    private String currency = "PLN";
    private String successUrl;
    private String failureUrl;
    private String doctorName;
    private String visitDate;
    private String visitDescription;
    private String selectedServices;
} 