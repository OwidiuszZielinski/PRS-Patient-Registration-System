package org.example.prspatientregistrationsystem.core.application;

import lombok.RequiredArgsConstructor;
import org.example.prspatientregistrationsystem.core.payment.PaymentService;
import org.example.prspatientregistrationsystem.core.payment.dto.PaymentRequestDto;
import org.example.prspatientregistrationsystem.core.payment.dto.PaymentResponseDto;
import org.example.prspatientregistrationsystem.core.payment.PaymentRepository;
import org.example.prspatientregistrationsystem.core.payment.PaymentEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.HashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payment")
@CrossOrigin(origins = "http://localhost:3000")
public class PaymentController {

    private final PaymentService paymentService;
    private final PaymentRepository paymentRepository;
    private static final Logger log = LoggerFactory.getLogger(PaymentController.class);

    @PostMapping("/create")
    public ResponseEntity<PaymentResponseDto> createPayment(@RequestBody PaymentRequestDto paymentRequest) {
        try {
            PaymentResponseDto response = paymentService.createPayment(paymentRequest);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/callback")
    public ResponseEntity<String> handlePaymentCallback(@RequestBody Map<String, Object> callbackData) {
        try {
            paymentService.handlePaymentCallback(callbackData);
            return ResponseEntity.ok("OK");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("ERROR");
        }
    }

    @GetMapping("/status/{paymentId}")
    public ResponseEntity<Map<String, Object>> getPaymentStatus(@PathVariable String paymentId) {
        try {
            log.info("Checking payment status for paymentId: {}", paymentId);
            
            PaymentEntity payment = paymentRepository.findByPaymentId(paymentId)
                    .orElseThrow(() -> new RuntimeException("Payment not found"));
            
            Map<String, Object> response = new HashMap<>();
            response.put("paymentId", payment.getPaymentId());
            response.put("status", payment.getStatus());
            response.put("amount", payment.getAmount());
            response.put("visitId", payment.getVisitId());
            response.put("patientName", payment.getPatientName());
            
            log.info("Payment status: {}", payment.getStatus());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error checking payment status", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Payment controller is working!");
    }

    @GetMapping("/test-payu")
    public ResponseEntity<Map<String, Object>> testPayU() {
        try {
            log.info("Testing PayU authentication...");
            
            // Test getting access token
            String accessToken = paymentService.getAccessToken();
            
            Map<String, Object> response = new HashMap<>();
            if (accessToken != null) {
                response.put("status", "SUCCESS");
                response.put("message", "PayU authentication successful");
                response.put("accessToken", accessToken.substring(0, Math.min(20, accessToken.length())) + "...");
                log.info("PayU authentication test successful");
            } else {
                response.put("status", "ERROR");
                response.put("message", "PayU authentication failed");
                log.error("PayU authentication test failed");
            }
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error testing PayU authentication", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "ERROR");
            errorResponse.put("message", "Error testing PayU: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
} 