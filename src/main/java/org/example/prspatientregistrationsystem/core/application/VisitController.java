package org.example.prspatientregistrationsystem.core.application;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.prspatientregistrationsystem.core.payment.PaymentEntity;
import org.example.prspatientregistrationsystem.core.payment.PaymentService;
import org.example.prspatientregistrationsystem.core.payment.dto.PaymentRequestDto;
import org.example.prspatientregistrationsystem.core.service.ServiceDto;
import org.example.prspatientregistrationsystem.core.visit.VisitDto;
import org.example.prspatientregistrationsystem.core.visit.VisitService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static java.util.Optional.ofNullable;

@RestController
@RequestMapping("/api/visit/")
@CrossOrigin(origins = "http://localhost:3000")
@Slf4j
@RequiredArgsConstructor
public class VisitController {

    private final VisitService visitService;
    private final PaymentService paymentService;
    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @PostMapping
    public void addVisit(@RequestBody VisitDto visitDto) {
        visitService.addVisit(visitDto);
    }

    @PostMapping("/with-payment")
    public ResponseEntity<Map<String, Object>> addVisitWithPayment(@RequestBody VisitDto visitDto) {
        if (visitDto.getTotalCost() == null || visitDto.getTotalCost().compareTo(BigDecimal.ZERO) <= 0) {
            Long savedVisitId = visitService.addVisit(visitDto);
            return success("Visit registered successfully (no payment required)", "visitId", savedVisitId);
        }
        var selectedServicesJson = serializeSelectedServices(visitDto.getSelectedServices());

        var paymentRequest = buildPaymentRequest(visitDto, UUID.randomUUID().toString(), selectedServicesJson);
        var paymentResponse = paymentService.createPayment(paymentRequest);

        if ("SUCCESS".equals(paymentResponse.getStatus())) {
            return success("Payment created successfully. Visit will be registered after payment completion.",
                    "paymentId", paymentResponse.getPaymentId(),
                    "redirectUrl", paymentResponse.getRedirectUrl());
        } else {
            return error("Failed to create payment: " + paymentResponse.getMessage());
        }

    }

    @GetMapping
    public List<VisitDto> getVisits() {
        return visitService.findAll();
    }

    @GetMapping("test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Visit controller is working!");
    }

    @DeleteMapping(path = "{id}/")
    public void delete(@PathVariable Long id) {
        visitService.delete(id);
    }

    @PostMapping(path = "update")
    public void update(@RequestBody VisitDto visitDto) {
        visitService.update(visitDto);
    }

    @PostMapping("/check-payment-and-create-visit/{visitId}")
    public ResponseEntity<Map<String, Object>> checkPaymentAndCreateVisit(@PathVariable String visitId) {
        try {
            if (visitId == null || visitId.trim().isEmpty()) {
                return error("VisitId is required");
            }

            Map<String, Object> paymentStatus = paymentService.getPaymentStatusByVisitId(visitId);
            if (paymentStatus == null || paymentStatus.containsKey("error")) {
                return error("Payment error: " + paymentStatus.getOrDefault("error", "unknown"));
            }

            String status = (String) paymentStatus.get("status");
            if (status == null) {
                return error("Payment status is unknown");
            }

            return handlePaymentStatus(status, visitId);
        } catch (Exception e) {
            return serverError("Error processing request: " + e.getMessage());
        }
    }

    private ResponseEntity<Map<String, Object>> handlePaymentStatus(String status, String visitId) {
        return switch (status) {
            case "NOT_FOUND", "ERROR" -> error("Payment not found or error occurred");
            case "COMPLETED", "SUCCESS", "APPROVED" -> createVisitAfterSuccessfulPayment(visitId);
            case "CANCELED", "REJECTED", "FAILED" ->
                    error("Payment was " + status.toLowerCase(), "paymentStatus", status);
            default -> pending("Payment is still pending or has unknown status", "paymentStatus", status);
        };
    }

    private ResponseEntity<Map<String, Object>> createVisitAfterSuccessfulPayment(String visitId) {
        PaymentEntity payment = paymentService.getPaymentEntityByVisitId(visitId);
        if (payment == null || payment.getDoctorName() == null || payment.getPatientName() == null) {
            return error("Payment data is incomplete or not found");
        }

        var selectedServices = parseSelectedServices(payment.getSelectedServices());
        var visitDto = VisitDto.builder()
                .doctorName(payment.getDoctorName())
                .patient(payment.getPatientName())
                .date(parseVisitDate(payment.getVisitDate()))
                .description(ofNullable(payment.getVisitDescription()).orElse(""))
                .selectedServices(selectedServices)
                .totalCost(ofNullable(payment.getAmount()).orElse(BigDecimal.ZERO))
                .build();

        if (visitDto.getDoctorName() == null || visitDto.getPatient() == null) {
            return error("Visit data is invalid");
        }

        return success("Visit created successfully after payment verification", "visitId", visitService.addVisit(visitDto));
    }

    private List<ServiceDto> parseSelectedServices(String selectedServices) {
        try {
            if (selectedServices != null && !selectedServices.isBlank() && !"null".equals(selectedServices)) {
                return objectMapper.readValue(selectedServices, new TypeReference<>() {
                });
            }
        } catch (Exception ignored) {
            try {
                ServiceDto service = objectMapper.readValue(selectedServices, ServiceDto.class);
                return List.of(service);
            } catch (Exception ignoredAgain) {
            }
        }
        return null;
    }

    private LocalDateTime parseVisitDate(String dateString) {
        if (dateString == null || dateString.isBlank() || "null".equals(dateString)) {
            return LocalDateTime.now();
        }

        DateTimeFormatter[] formatters = {
                DateTimeFormatter.ISO_LOCAL_DATE_TIME,
                DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"),
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        };

        for (DateTimeFormatter formatter : formatters) {
            try {
                return LocalDateTime.parse(dateString, formatter);
            } catch (Exception ignored) {
            }
        }
        return LocalDateTime.now();
    }

    private String serializeSelectedServices(List<ServiceDto> selectedServices) {
        try {
            return selectedServices != null ? objectMapper.writeValueAsString(selectedServices) : null;
        } catch (Exception e) {
            return null;
        }
    }

    private PaymentRequestDto buildPaymentRequest(VisitDto visitDto, String visitId, String selectedServicesJson) {
        return PaymentRequestDto.builder()
                .visitId(visitId)
                .patientName(visitDto.getPatient())
                .patientEmail(visitDto.getPatient())
                .amount(visitDto.getTotalCost())
                .description("Medical visit: " + visitDto.getDescription())
                .currency("PLN")
                .successUrl("http://localhost:3000/patient-view?tab=list&paymentId=" + visitId)
                .failureUrl("http://localhost:3000/payment-status?status=failure&paymentId=" + visitId)
                .doctorName(visitDto.getDoctorName())
                .visitDate(visitDto.getDate() != null ? visitDto.getDate().toString() : null)
                .visitDescription(visitDto.getDescription())
                .selectedServices(selectedServicesJson)
                .build();
    }

    private ResponseEntity<Map<String, Object>> success(String message, Object... additionalData) {
        return buildResponse("SUCCESS", message, additionalData);
    }

    private ResponseEntity<Map<String, Object>> error(String message, Object... additionalData) {
        return buildResponse("ERROR", message, additionalData);
    }

    private ResponseEntity<Map<String, Object>> pending(String message, Object... additionalData) {
        return buildResponse("PENDING", message, additionalData);
    }

    private ResponseEntity<Map<String, Object>> serverError(String message) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("status", "ERROR", "message", message));
    }

    private ResponseEntity<Map<String, Object>> buildResponse(String status, String message, Object... additionalData) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("status", status);
        response.put("message", message);
        for (int i = 0; i < additionalData.length - 1; i += 2) {
            response.put(additionalData[i].toString(), additionalData[i + 1]);
        }
        return ResponseEntity.ok(response);
    }
}