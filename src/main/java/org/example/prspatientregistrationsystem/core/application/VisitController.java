package org.example.prspatientregistrationsystem.core.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.prspatientregistrationsystem.core.payment.PaymentService;
import org.example.prspatientregistrationsystem.core.payment.dto.PaymentRequestDto;
import org.example.prspatientregistrationsystem.core.payment.dto.PaymentResponseDto;
import org.example.prspatientregistrationsystem.core.payment.PaymentEntity;
import org.example.prspatientregistrationsystem.core.visit.VisitDto;
import org.example.prspatientregistrationsystem.core.visit.VisitEntity;
import org.example.prspatientregistrationsystem.core.visit.VisitService;
import org.example.prspatientregistrationsystem.core.service.ServiceDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/api/visit/")
@CrossOrigin(origins = "http://localhost:3000")
@Slf4j
public class VisitController {

    private final VisitService visitService;
    private final PaymentService paymentService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    public VisitController(VisitService visitService, PaymentService paymentService) {
        this.visitService = visitService;
        this.paymentService = paymentService;
        // Configure ObjectMapper for better BigDecimal handling
        this.objectMapper.findAndRegisterModules();
    }

    @PostMapping
    public void addVisit(@RequestBody VisitDto visitDto) {
        visitService.addVisit(visitDto);
    }

    @PostMapping("/with-payment")
    public ResponseEntity<Map<String, Object>> addVisitWithPayment(@RequestBody VisitDto visitDto) {
        try {
            log.info("Adding visit with payment: {}", visitDto);
            
            // Check if payment is needed
            if (visitDto.getTotalCost() == null || visitDto.getTotalCost().compareTo(BigDecimal.ZERO) <= 0) {
                // No payment needed, save visit directly
                Long savedVisitId = visitService.addVisit(visitDto);
                return ResponseEntity.ok(Map.of(
                    "status", "SUCCESS",
                    "message", "Visit registered successfully (no payment required)",
                    "visitId", savedVisitId
                ));
            }
            
            // Payment is needed - create payment first
            String visitId = UUID.randomUUID().toString();
            
            log.info("Creating payment request with selectedServices: {}", visitDto.getSelectedServices());
            String selectedServicesJson = null;
            if (visitDto.getSelectedServices() != null) {
                selectedServicesJson = objectMapper.writeValueAsString(visitDto.getSelectedServices());
                log.info("Serialized selectedServices to JSON: {}", selectedServicesJson);
            }
            
            PaymentRequestDto paymentRequest = PaymentRequestDto.builder()
                    .visitId(visitId)
                    .patientName(visitDto.getPatient())
                    .patientEmail(visitDto.getPatient()) // You might want to get actual email from patient service
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
            
            // Try to create payment in PayU first
            PaymentResponseDto paymentResponse = paymentService.createPayment(paymentRequest);
            
            if (paymentResponse.getStatus().equals("SUCCESS")) {
                // Payment created successfully, visit will be saved after successful payment
                log.info("Payment created successfully, visit will be saved after payment completion: {}", paymentResponse.getPaymentId());
                
                return ResponseEntity.ok(Map.of(
                    "status", "SUCCESS",
                    "message", "Payment created successfully. Visit will be registered after payment completion.",
                    "paymentId", paymentResponse.getPaymentId(),
                    "redirectUrl", paymentResponse.getRedirectUrl()
                ));
            } else {
                // Payment failed, don't save the visit
                log.error("Payment creation failed: {}", paymentResponse.getMessage());
                return ResponseEntity.ok(Map.of(
                    "status", "ERROR",
                    "message", "Failed to create payment: " + paymentResponse.getMessage()
                ));
            }
            
        } catch (Exception e) {
            log.error("Error adding visit with payment", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                        "status", "ERROR",
                        "message", "Error processing request: " + e.getMessage()
                    ));
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
            log.info("Checking payment status and creating visit for visitId: {}", visitId);
            
            if (visitId == null || visitId.trim().isEmpty()) {
                log.error("VisitId is null or empty");
                return ResponseEntity.badRequest().body(Map.of(
                    "status", "ERROR",
                    "message", "VisitId is required"
                ));
            }
            
            // Get payment status by visitId
            Map<String, Object> paymentStatus = paymentService.getPaymentStatusByVisitId(visitId);
            log.info("Payment status response: {}", paymentStatus);
            
            if (paymentStatus == null) {
                log.error("Payment status is null for visitId: {}", visitId);
                return ResponseEntity.ok(Map.of(
                    "status", "ERROR",
                    "message", "Payment not found"
                ));
            }
            
            // Check if there's an error in the response
            if (paymentStatus.containsKey("error")) {
                log.error("Payment error: {}", paymentStatus.get("error"));
                return ResponseEntity.ok(Map.of(
                    "status", "ERROR",
                    "message", "Payment error: " + paymentStatus.get("error")
                ));
            }
            
            String status = (String) paymentStatus.get("status");
            log.info("Payment status: {}", status);
            
            if (status == null) {
                log.error("Payment status is null in response for visitId: {}", visitId);
                return ResponseEntity.ok(Map.of(
                    "status", "ERROR",
                    "message", "Payment status is unknown"
                ));
            }
            
            // Handle different status types
            if ("NOT_FOUND".equals(status)) {
                return ResponseEntity.ok(Map.of(
                    "status", "ERROR",
                    "message", "Payment not found"
                ));
            } else if ("ERROR".equals(status)) {
                return ResponseEntity.ok(Map.of(
                    "status", "ERROR",
                    "message", "Payment error occurred"
                ));
            } else if ("COMPLETED".equals(status) || "SUCCESS".equals(status) || "APPROVED".equals(status)) {
                // Payment is successful, create the visit
                PaymentEntity payment = paymentService.getPaymentEntityByVisitId(visitId);
                log.info("Payment entity: {}", payment);
                
                if (payment != null) {
                    // Check if required fields are not null
                    if (payment.getDoctorName() == null || payment.getPatientName() == null) {
                        log.error("Required payment fields are null: doctorName={}, patientName={}", 
                                payment.getDoctorName(), payment.getPatientName());
                        return ResponseEntity.ok(Map.of(
                            "status", "ERROR",
                            "message", "Payment data is incomplete"
                        ));
                    }
                    
                    log.info("Payment data: doctorName={}, patientName={}, visitDate={}, visitDescription={}, selectedServices={}, amount={}", 
                            payment.getDoctorName(), payment.getPatientName(), payment.getVisitDate(), 
                            payment.getVisitDescription(), payment.getSelectedServices(), payment.getAmount());
                    
                    // Parse selected services
                    List<ServiceDto> selectedServices = parseSelectedServices(payment.getSelectedServices());
                    log.info("Parsed selected services: {}", selectedServices);
                    
                    VisitDto visitDto = VisitDto.builder()
                            .doctorName(payment.getDoctorName())
                            .patient(payment.getPatientName())
                            .date(parseVisitDate(payment.getVisitDate()))
                            .description(payment.getVisitDescription() != null ? payment.getVisitDescription() : "")
                            .selectedServices(selectedServices)
                            .totalCost(payment.getAmount() != null ? payment.getAmount() : BigDecimal.ZERO)
                            .build();
                    
                    log.info("Created VisitDto: {}", visitDto);
                    
                    // Validate VisitDto before saving
                    if (visitDto.getDoctorName() == null || visitDto.getPatient() == null) {
                        log.error("VisitDto validation failed: doctorName={}, patient={}", 
                                visitDto.getDoctorName(), visitDto.getPatient());
                        return ResponseEntity.ok(Map.of(
                            "status", "ERROR",
                            "message", "Visit data is invalid"
                        ));
                    }
                    
                    log.info("Creating visit with data: {}", visitDto);
                    Long savedVisitId = visitService.addVisit(visitDto);
                    log.info("Visit created successfully after payment verification: {}", savedVisitId);
                    
                    return ResponseEntity.ok(Map.of(
                        "status", "SUCCESS",
                        "message", "Visit created successfully after payment verification",
                        "visitId", savedVisitId
                    ));
                } else {
                    log.error("Payment entity not found for visitId: {}", visitId);
                    return ResponseEntity.ok(Map.of(
                        "status", "ERROR",
                        "message", "Payment entity not found"
                    ));
                }
            } else if ("CANCELED".equals(status) || "REJECTED".equals(status) || "FAILED".equals(status)) {
                log.info("Payment was canceled, rejected or failed with status: {}", status);
                return ResponseEntity.ok(Map.of(
                    "status", "ERROR",
                    "message", "Payment was " + status.toLowerCase(),
                    "paymentStatus", status
                ));
            } else {
                log.info("Payment is still pending or has unknown status: {}", status);
                return ResponseEntity.ok(Map.of(
                    "status", "PENDING",
                    "message", "Payment is still pending or has unknown status",
                    "paymentStatus", status
                ));
            }
            
        } catch (Exception e) {
            log.error("Error checking payment and creating visit", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                        "status", "ERROR",
                        "message", "Error processing request: " + e.getMessage()
                    ));
        }
    }
    
    private List<ServiceDto> parseSelectedServices(String selectedServices) {
        try {
            if (selectedServices != null && !selectedServices.isEmpty() && !"null".equals(selectedServices)) {
                log.info("Parsing selected services: {}", selectedServices);
                
                // Try to parse as JSON array
                List<ServiceDto> result = objectMapper.readValue(selectedServices, new TypeReference<List<ServiceDto>>() {});
                log.info("Successfully parsed {} services: {}", result != null ? result.size() : 0, result);
                return result;
            } else {
                log.info("Selected services is null or empty, returning null");
                return null;
            }
        } catch (Exception e) {
            log.error("Error parsing selected services: {}", e.getMessage());
            log.error("Selected services string was: '{}'", selectedServices);
            log.error("Full exception: ", e);
            
            // Try to parse as single object first
            try {
                log.info("Trying to parse as single ServiceDto object...");
                ServiceDto singleService = objectMapper.readValue(selectedServices, ServiceDto.class);
                log.info("Successfully parsed as single service: {}", singleService);
                return List.of(singleService);
            } catch (Exception e2) {
                log.error("Failed to parse as single service as well: {}", e2.getMessage());
                return null;
            }
        }
    }
    
    private LocalDateTime parseVisitDate(String dateString) {
        try {
            log.info("Parsing visit date: '{}'", dateString);
            
            if (dateString != null && !dateString.isEmpty() && !"null".equals(dateString)) {
                // Try different date formats
                DateTimeFormatter[] formatters = {
                    DateTimeFormatter.ISO_LOCAL_DATE_TIME,
                    DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"),
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                };
                
                for (DateTimeFormatter formatter : formatters) {
                    try {
                        LocalDateTime parsedDate = LocalDateTime.parse(dateString, formatter);
                        log.info("Successfully parsed date: {} using formatter: {}", parsedDate, formatter);
                        return parsedDate;
                    } catch (Exception ignored) {
                        log.debug("Failed to parse date with formatter: {}", formatter);
                    }
                }
                
                log.warn("Could not parse date with any formatter, using current time");
                return LocalDateTime.now();
            } else {
                log.info("Date string is null or empty, using current time");
                return LocalDateTime.now(); // Default to current time if parsing fails
            }
        } catch (Exception e) {
            log.error("Error parsing visit date: {}", e.getMessage());
            return LocalDateTime.now();
        }
    }
}
