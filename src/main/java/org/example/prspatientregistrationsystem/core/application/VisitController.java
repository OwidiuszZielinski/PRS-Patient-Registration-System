package org.example.prspatientregistrationsystem.core.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.prspatientregistrationsystem.core.payment.PaymentService;
import org.example.prspatientregistrationsystem.core.payment.dto.PaymentRequestDto;
import org.example.prspatientregistrationsystem.core.payment.dto.PaymentResponseDto;
import org.example.prspatientregistrationsystem.core.visit.VisitDto;
import org.example.prspatientregistrationsystem.core.visit.VisitEntity;
import org.example.prspatientregistrationsystem.core.visit.VisitService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/visit/")
@CrossOrigin(origins = "http://localhost:3000")
@Slf4j
public class VisitController {

    private final VisitService visitService;
    private final PaymentService paymentService;

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
            
            PaymentRequestDto paymentRequest = PaymentRequestDto.builder()
                    .visitId(visitId)
                    .patientName(visitDto.getPatient())
                    .patientEmail(visitDto.getPatient()) // You might want to get actual email from patient service
                    .amount(visitDto.getTotalCost())
                    .description("Medical visit: " + visitDto.getDescription())
                    .currency("PLN")
                    .successUrl("http://localhost:3000/payment-status?status=success&paymentId=" + visitId)
                    .failureUrl("http://localhost:3000/payment-status?status=failure&paymentId=" + visitId)
                    .build();
            
            // Try to create payment in PayU first
            PaymentResponseDto paymentResponse = paymentService.createPayment(paymentRequest);
            
            if (paymentResponse.getStatus().equals("SUCCESS")) {
                // Payment created successfully, now save the visit
                Long savedVisitId = visitService.addVisit(visitDto);
                log.info("Visit saved successfully after payment creation: {}", savedVisitId);
                
                return ResponseEntity.ok(Map.of(
                    "status", "SUCCESS",
                    "message", "Visit registered and payment created successfully",
                    "visitId", savedVisitId,
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
}
